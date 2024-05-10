package com.example.foty.flower.view

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService

class NotificationListener : NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }
}
