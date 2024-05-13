package com.example.foty.flower.view

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
import android.media.session.PlaybackState
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast

class MusicViewManager(val context: Context) : MediaController.Callback(),
    OnActiveSessionsChangedListener {

    private lateinit var mediaSessionManager: MediaSessionManager
    private lateinit var componentName: ComponentName
    private var mediaController: MediaController? = null

    private var musicListener: MusicManager? = null
    private var artist = ""
    private var title = ""
    private var packageName = ""
    private var musicBitmap: Bitmap? = null

    private var time = 0L
    private var playTime = 0L
    private val timeHandler = Handler()
    private val timeRunnable = object : Runnable {
        override fun run() {
            time += 1000
            Log.e("lxx", "-------------自己的倒计时,time= $time")
            timeHandler.postDelayed(this, 1000)
        }
    }

    private var state = -1
    private var songArtist = ""

    private val handler by lazy { Handler() }
    private val sessionRunnable: Runnable = Runnable {
        val activeSessions: List<MediaController> =
            mediaSessionManager.getActiveSessions(componentName)
        var seccess = false
        if (activeSessions.isNotEmpty() && isCurPackage(
                activeSessions[0].getPackageName(),
                context.packageManager
            )
        ) {
            mediaController = activeSessions[0]
            packageName = mediaController?.getPackageName() ?: ""
            mediaController?.registerCallback(this)

            val state = mediaController?.playbackState
            val meta = mediaController?.metadata
            Log.d("lxx", "已经在播放了----------")
            notifyState(state)
            meta?.let { parseMetadata(it) }
            seccess = true
        }
        if (seccess) {

        } else {
            callFailed()
        }
    }

    init {
        initManager()
    }

    override fun onActiveSessionsChanged(list: List<MediaController?>?) {
        Log.d("lxx", "onActiveSessionsChanged ----------------------");
        initManager()
    }

    override fun onPlaybackStateChanged(playbackState: PlaybackState?) {
        super.onPlaybackStateChanged(playbackState)
        playbackState?.let {
            state = it.state
            if (state == PlaybackState.STATE_PAUSED){
                timeHandler.removeCallbacks(timeRunnable)
                time = playTime
            }
            notifyState(it)
        }
    }

    override fun onMetadataChanged(mediaMetadata: MediaMetadata?) {
        super.onMetadataChanged(mediaMetadata)
        mediaMetadata ?: return
        parseMetadata(mediaMetadata)
    }


    override fun onSessionDestroyed() {
        super.onSessionDestroyed()
        callFailed()
    }

    private fun initManager() {
        try {
            mediaSessionManager =
                context.getSystemService("media_session") as MediaSessionManager
            componentName = ComponentName(
                context,
                (NotificationListener::class.java as Class<*>)
            )
            mediaSessionManager.addOnActiveSessionsChangedListener(this, componentName)
            next()
        } catch (e: SecurityException) {
            getPermission(context)
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun destroy(){
        mediaController?.unregisterCallback(this)
        timeHandler.removeCallbacks(timeRunnable)
    }

    fun notifyState(playbackState: PlaybackState?) {
        if (playbackState != null) {
            if (playbackState.state == 3) {
                musicListener?.stateChange(3)
            } else {
                musicListener?.stateChange(2)
            }
        }
    }

    fun parseMetadata(mediaMetadata: MediaMetadata) {
        val str: String?
        val str2: String?
        val bitmap: Bitmap?
        if (mediaMetadata != null) {
            var bitmap2: Bitmap? = null
            str = try {
                mediaMetadata.getString("android.media.metadata.ARTIST")
            } catch (unused: java.lang.Exception) {
                null
            }
            str2 = try {
                mediaMetadata.getString("android.media.metadata.TITLE")
            } catch (unused2: java.lang.Exception) {
                null
            }
            if (str != null && !str.isEmpty()) {
                artist = str
            }
            if (str2 != null && !str2.isEmpty()) {
                title = str2
            }
            bitmap = try {
                mediaMetadata.getBitmap("android.media.metadata.ART")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                null
            }
            if (bitmap != null) {
                musicBitmap = bitmap
            } else {
                try {
                    bitmap2 = mediaMetadata.getBitmap("android.media.metadata.ALBUM_ART")
                } catch (e2: java.lang.Exception) {
                    e2.printStackTrace()
                }
                musicBitmap = bitmap2
            }

            // 移动进度
//            mediaController?.transportControls?.seekTo(2000L)

            val currentPosition = mediaController?.playbackState?.position ?: -0L
            playTime = currentPosition
            val allTime = mediaMetadata?.getLong("android.media.metadata.DURATION")
            Log.d("lxx", "进度进度进度-------- currentPosition= $currentPosition, time= $allTime")


            if (songArtist != artist) {
                Log.d("lxx", "播放新歌曲啦。。。。。。启动自己倒计时去")
                time = if (playTime != 0L) playTime else 0L
                timeHandler.removeCallbacks(timeRunnable)
                timeHandler.post(timeRunnable)
            }


            val author = mediaMetadata?.getString("android.media.metadata.AUTHOR")
            val author2 = mediaMetadata?.getString("android.media.metadata.ALBUM_ARTIST")
            val writer = mediaMetadata?.getString("android.media.metadata.WRITER")

            songArtist = artist
            // 回调信息出去
            Log.i(
                "lxx",
                "content: artist= $artist, title= $title, packageName= $packageName"
            )
        }
        musicListener?.contentChange(artist, title, musicBitmap, packageName)
    }

    fun callFailed() {
        artist = ""
        title = "音乐"
        musicBitmap = null
        Log.d("lxx", "callFailed  *********** ")
        setState()
        musicListener?.contentChange(this.artist, this.title, musicBitmap, packageName)
    }

    fun setState() {
        if (getAudioManager(context) && mediaController != null) {
            //处于播放状态
            musicListener?.stateChange(3)
        } else {
            if (getAudioManager(context)) {
                return
            }
            // 处于未播放状态
            musicListener?.stateChange(2)
        }
    }

    fun getAudioManager(context: Context): Boolean {
        val audio = context.getSystemService("audio") as AudioManager
        return audio.isMusicActive
    }

    fun next() {
        if (notification(context).not()) {
            getPermission(context)
            return
        }
        initMediaController()
        handler.removeCallbacks(sessionRunnable)
        handler.postDelayed(sessionRunnable, 100L)
    }


    interface MusicManager {
        fun contentChange(title: String?, lyrics: String?, cover: Bitmap?, str3: String?)
        fun stateChange(i: Int)
    }

    fun setMusicListener(listener: MusicManager) {
        musicListener = listener
    }

    fun initMediaController() {
        mediaController?.unregisterCallback(this)
    }

    @SuppressLint("WrongConstant")
    fun notification(context: Context): Boolean {
        return try {
            val componentName = ComponentName(
                context.packageName,
                NotificationListener::class.java.getName()
            )
            if (Build.VERSION.SDK_INT >= 27 && context.getSystemService("notification") != null) {
                return (context.getSystemService("notification") as NotificationManager).isNotificationListenerAccessGranted(
                    componentName
                )
            }
            val string =
                Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
            string != null && string.contains(componentName.flattenToString())
        } catch (unused: java.lang.Exception) {
            false
        }
    }

    fun isCurPackage(str: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(str, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    fun getPermission(context: Context) {
        Toast.makeText(context, "SecurityException", Toast.LENGTH_SHORT)
            .show()
        // TODO: 检查是否已经授予了权限


    }

}