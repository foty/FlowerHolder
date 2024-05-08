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
import com.example.foty.flower.view.FloatWindowImageView
import com.example.foty.flower.R

class FloatViewActivity : AppCompatActivity() {

    private var view: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_float_view)

//        addFloatView()
//        start();
//        addChild()
//        startAnimation()
    }

    private fun startAnimation() {
        if (view == null || view?.getVisibility() == View.VISIBLE) {
            return
        }
        view?.setSystemUiVisibility(1280)
        val f2 = 0f
        val f3 = (1.2f - f2) * 500.0f
        val ofFloat = ValueAnimator.ofFloat(f2, 1.2f)
        ofFloat.setDuration(f3.toLong())
        ofFloat.addUpdateListener { }
        ofFloat.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        ofFloat.start()
    }

    /**
     * 添加窗口
     */
    private fun addWindow() {
        val windowManager = applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //8.0
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.format = -2
        layoutParams.gravity = Gravity.CENTER
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        val view: View = LayoutInflater.from(this).inflate(R.layout.layout_window_center, null)
        //        view.setVisibility(View.GONE);
        windowManager.addView(view, layoutParams)
    }

    /**
     * 添加漂浮view
     */
    private fun addFloatView() {
        val windowManager = applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //8.0
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.x = 0
        layoutParams.y = 0
        layoutParams.gravity = Gravity.START or Gravity.TOP
        layoutParams.width = 400
        layoutParams.height = 50
        val imageView: ImageView =
            FloatWindowImageView(
                windowManager,
                layoutParams,
                this
            )
        imageView.setBackgroundColor(getColor(R.color.colorAccent))
    }
}