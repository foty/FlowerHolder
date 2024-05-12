package com.example.foty.flower.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foty.flower.databinding.ActivitySystemSetBinding


class SettingActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySystemSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerVolume()
        registerBattery()
        Log.d("lxx", "==== 开启sim数据: ${getSimState(this)}")
        getNetState(this)
        registerNetwork()

        binding.tvStart.setOnClickListener {
            getAllInstalledApps(this)
        }

        binding.tvEnd.setOnClickListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(volumeReceiver)
        unregisterReceiver(batteryReceiver)
        unregisterReceiver(networkReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(this) && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_SETTINGS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "获取到了权限", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "未获取到权限", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getAllInstalledApps(context: Context) {
        val packageManager = context.packageManager
        val list =  packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        list.forEach {appInfo ->
            val packageName = appInfo.packageName
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            Log.d("lxx", "         name= [$appName], packageName= [$packageName]");
        }
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

    /**
     * 设置息屏时间
     */
    fun setScreenLock(timeOut: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(this)
                .not()
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
            getContentResolver(), "screen_off_timeout", timeOut
        )
        try {
            val str = "time: " + timeOut + ", System.getInt=: " + Settings.System.getInt(
                getContentResolver(), "screen_off_timeout"
            ) + ", time: " + time + ",设置结果 result: " + putInt

            Log.d("lxx", str)

        } catch (e2: SettingNotFoundException) {
            e2.printStackTrace()
        }
    }

    /**
     * 申请系统修改权限
     */
    private fun requestWriteSettingsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, 1000)
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

    /**
     * 是否有sim状态
     */
    fun getSimState(context: Context): Boolean {
        val connectivityManager = context.getSystemService("connectivity") as ConnectivityManager
        try {
            val cls = Class.forName(connectivityManager.javaClass.name)
            val declaredMethod = cls.getDeclaredMethod("getMobileDataEnabled", *arrayOfNulls(0))
            declaredMethod.isAccessible = true
            val telephonyManager = context.getSystemService("phone") as TelephonyManager
            if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                val b = declaredMethod.invoke(connectivityManager, arrayOf<Any>(0)) as Boolean
                return b
            }
            return false
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun getHasSim(context: Context): Boolean {
        try {
            val telephonyManager = context.getSystemService("phone") as TelephonyManager
            return if ((telephonyManager.simOperatorName == null || telephonyManager.simOperatorName == ""))
                false
            else
                true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }


    private val volumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.getIntExtra(
                    "android.media.EXTRA_VOLUME_STREAM_TYPE",
                    -1
                ) == AudioManager.STREAM_MUSIC
            ) {
                val intExtra = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1)
                Log.d("lxx", "音量：$intExtra")
            }
        }
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val intExtra = intent.getIntExtra("level", 0)
            val intExtra2 = intent.getIntExtra("status", -1)
            Log.d("lxx", "当前电量= $intExtra")
            // || status == BatteryManager.BATTERY_STATUS_FULL
            if (intExtra2 == BatteryManager.BATTERY_STATUS_CHARGING || intExtra2 == BatteryManager.BATTERY_STATUS_FULL) {
                Log.d("lxx", "充电中。。。。。。。。。")
            }
        }
    }

    /**
     * 电量广播
     */
    fun registerBattery() {
        val intentFilter = IntentFilter("android.intent.action.BATTERY_CHANGED")
        registerReceiver(batteryReceiver, intentFilter)
    }

    /**
     * 注册音量改变广播
     */
    fun registerVolume() {
        val intentFilter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
        registerReceiver(volumeReceiver, intentFilter)
    }


    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //获取联网状态的NetworkInfo对象
            val info =
                intent.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (info.state == NetworkInfo.State.CONNECTED && info.isAvailable) {
                    if (info.type == ConnectivityManager.TYPE_WIFI) {
                        Log.d("lxx", "BroadcastReceiver: 使用WIFI网络")
                        getWifiSSID(context)
                    }
                    if (info.type == ConnectivityManager.TYPE_MOBILE) {
                        Log.d("lxx", "BroadcastReceiver: 使用移动数据网络")
                    }
                } else {
                    Log.d("lxx", "BroadcastReceiver: 当前网络不可用")
                }
            }

        }
    }

    fun registerNetwork() {
        val filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter)
    }

    fun getWifiSSID(context: Context) {
        // 获取ssid 需要开启定位权限，android10.0需要申请新添加的隐私权限ACCESS_FINE_LOCATION
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            200
        )

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        if (info.supplicantState == SupplicantState.COMPLETED) {
            Log.d("lxx", "连接的wifi名称：ssid= ${info?.ssid}")
        }
    }

    fun getNetState(context: Context) {
        val networkInfo =
            (context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            Log.d("lxx", "ConnectivityManager: 网络已连接")
            if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                Log.d("lxx", "ConnectivityManager: wifi上网")
                getWifiSSID(context)
            }
            if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                Log.d("lxx", "ConnectivityManager: 数据上网")
            }
        } else {
            Log.d("lxx", "ConnectivityManager: 网络不可用")
        }
    }
}