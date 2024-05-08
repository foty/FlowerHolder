package com.example.foty.flower

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foty.flower.databinding.ActivityMainBinding
import com.example.foty.flower.ui.FloatViewActivity
import com.example.foty.flower.ui.MusicViewActivity
import com.example.foty.flower.ui.ServiceActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvService.setOnClickListener {
            startActivity(Intent(this, ServiceActivity::class.java))
        }
        binding.tvFloat.setOnClickListener {
            startActivity(Intent(this, FloatViewActivity::class.java))
        }
        binding.tvMusic.setOnClickListener {
            startActivity(Intent(this, MusicViewActivity::class.java))
        }
    }

}
