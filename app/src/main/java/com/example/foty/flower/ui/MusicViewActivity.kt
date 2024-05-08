package com.example.foty.flower.ui

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.foty.flower.FloatWindowImageView
import com.example.foty.flower.R
import com.example.foty.flower.databinding.ActivityMainBinding
import com.example.foty.flower.databinding.ActivityMusicViewBinding

class MusicViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}