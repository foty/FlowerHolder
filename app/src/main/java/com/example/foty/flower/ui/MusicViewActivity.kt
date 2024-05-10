package com.example.foty.flower.ui

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.foty.flower.databinding.ActivityMusicViewBinding
import com.example.foty.flower.view.NotificationListener


class MusicViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicViewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkNotification(this).not()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                checkPermission()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    fun checkPermission() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        if (notificationManager != null && !notificationManager.isNotificationListenerAccessGranted(
                ComponentName(
                    this,
                    (NotificationListener::class.java as Class<*>)
                )
            )
        ) {

            Log.d("lxx", "没有多媒体权限")
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivityForResult(intent, 1000)

        } else {
            // 已经授予了权限，可以执行需要权限的操作
            Log.d("lxx", "有多媒体权限")
        }
    }

    fun checkNotification(context: Context): Boolean {
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
        } catch (unused: Exception) {
            false
        }
    }
}