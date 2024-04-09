package com.example.foty.somedependentlibraries

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {

        var context: Application? = null

        fun getApplication(): Application? {
            return context
        }
    }
}