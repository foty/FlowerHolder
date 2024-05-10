package com.example.foty.flower.view

import android.Manifest
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MusicViewManager(val context: Context) : MediaController.Callback(),
    OnActiveSessionsChangedListener {

    private lateinit var mediaSessionManager: MediaSessionManager
    private lateinit var componentName: ComponentName
    private var mediaController: MediaController? = null

    private var musicListener: MusicManager? = null
    private var d = ""
    private var e = ""
    private var name = ""
    private var musicBitmap: Bitmap? = null

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
            name = mediaController?.getPackageName() ?: ""
            mediaController?.registerCallback(this)

            val state = mediaController?.playbackState
            notifyState(state)
            val meta = mediaController?.metadata
            Log.d("lxx", "state= $state, meta= ${meta}")
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
        Log.d("lxx", "onActiveSessionsChanged");
        initManager()
    }

    override fun onMetadataChanged(mediaMetadata: MediaMetadata?) {
        super.onMetadataChanged(mediaMetadata)
        mediaMetadata ?: return
        parseMetadata(mediaMetadata)
    }

    override fun onPlaybackStateChanged(playbackState: PlaybackState?) {
        super.onPlaybackStateChanged(playbackState)
        notifyState(playbackState)
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
                d = str
            }
            if (str2 != null && !str2.isEmpty()) {
                e = str2
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
        }
        // 回调信息出去
        Log.d("lxx", "ARTIST= $d,TITLE= $e,packname= $name")
        musicListener?.contentChange(d, e, musicBitmap, name)
    }

    fun callFailed() {
        d = ""
        e = "音乐"
        musicBitmap = null
        setState()
        musicListener?.contentChange(this.d, this.e, musicBitmap, name)
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