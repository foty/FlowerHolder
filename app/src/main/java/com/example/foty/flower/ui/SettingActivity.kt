package com.example.foty.flower.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.foty.flower.databinding.ActivitySystemSetBinding

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySystemSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var value = 50
        binding.tvOpenNightMode.setOnClickListener {
            setScreenLock(10000)
        }
        binding.tvCloseNightMode.setOnClickListener {

        }
        Log.d("lxx", "亮度为： ${getBrightness()},max= ${getMaxBrightness()}")

    }

    /**
     * 当前屏幕亮度
     */
    fun getBrightness(): Int {
        return Settings.System.getInt(contentResolver, "screen_brightness")
    }

    /**
     * 最大亮度
     */
    fun getMaxBrightness(): Int {
        val powerManager = getSystemService("power") as PowerManager
        if (powerManager != null) {
            for (field in powerManager.javaClass.declaredFields) {
                if (field.getName() == "BRIGHTNESS_ON") {
                    field.isAccessible = true
                    return try {
                        field[powerManager] as Int
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                        -255
                    }
                }
            }
        }
        return -255
    }

    /**
     * 设置亮度
     */
    fun setBrightness(context: Context, i: Int) {
        try {
            Settings.System.putInt(context.contentResolver, "screen_brightness", i)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 设置声音
     */
    fun setVolume(volume: Int) {
        val audioManager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

    /**
     * 获取当前声音
     */
    fun getVolume(volume: Int) {
        val audioManager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    /**
     * 当前最大声音
     */
    fun getMaxVolume() {
        val audioManager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.getStreamMaxVolume(3) // AudioManager.STREAM_MUSIC
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                Settings.System.canWrite(this)
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "获取到了权限", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "未获取到权限", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestWriteSettingsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, 1000)
        }
    }

    /**
     * 设置息屏时间
     */
    fun setScreenLock(timeOut: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            Settings.System.canWrite(this).not()
        ) {
            requestWriteSettingsPermission()
            return
        }

        var time = 0
        try {
            time = Settings.System.getInt(getContentResolver(), "screen_off_timeout")
            Log.d("lxx", "息屏时间为： $time")
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
            time = 0
        }
        val putInt = Settings.System.putInt(
            getContentResolver(),
            "screen_off_timeout",
            timeOut
        )
        try {
            val str =
                "time: " + timeOut + ", System.getInt=: " + Settings.System.getInt(
                    getContentResolver(),
                    "screen_off_timeout"
                ) + ", time: " + time + ",设置结果 result: " + putInt

            Log.d("lxx", str)

        } catch (e2: SettingNotFoundException) {
            e2.printStackTrace()
        }
    }

    /**
     * 根据包名启动应用
     */
    fun launcherApp(context: Context, packageName: String?) {
        try {
            val launchIntentForPackage = context.packageManager.getLaunchIntentForPackage(
                packageName!!
            )
            if (launchIntentForPackage != null) {
                launchIntentForPackage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntentForPackage)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}