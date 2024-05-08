package com.example.foty.flower.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foty.flower.databinding.ActivityMusicViewBinding

class MusicViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}