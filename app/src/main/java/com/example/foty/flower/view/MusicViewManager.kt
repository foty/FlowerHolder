package com.example.foty.flower.view

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.widget.Toast

class MusicViewManager(val context: Context) : MediaController.Callback(),
    OnActiveSessionsChangedListener {

    private lateinit var mediaSessionManager: MediaSessionManager
    private lateinit var componentName: ComponentName
    private var mediaController: MediaController? = null
    private var name = ""

    private var d = ""
    private var e = ""
    private var g = ""
    private var bitmap: Bitmap? = null

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
            val meta = mediaController?.metadata

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

    override fun onActiveSessionsChanged(controllers: MutableList<MediaController>?) {

    }

    private fun initManager() {
        try {
            mediaSessionManager =
                context.getSystemService("android.content.Context.MEDIA_SESSION_SERVICE") as MediaSessionManager
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


    fun callFailed() {
        d = ""
        e = "音乐"
        bitmap = null
        setState()
        // todo 回调空内容
//        this.i.contentChange(this.d, this.e, this.f, this.g)
    }

    fun setState() {
//        if (n5.b(this.h).i() && this.b != null) {
//            this.i.stateChange(3)
//        } else {
//            if (n5.b(this.h).i()) {
//                return
//            }
//            this.i.stateChange(2)
//        }
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
        // TODO: 申请权限
    }
}