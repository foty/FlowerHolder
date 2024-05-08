package com.example.foty.flower.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foty.flower.R

class ServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

//        startService(Intent(this, MyService::class.java))
    }
}