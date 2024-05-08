package com.example.foty.flower;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startService(new Intent(this, MyService.class));
//        start();
        addChild();

       new  Handler(getMainLooper()).postDelayed(new Runnable() {
           @Override
           public void run() {

           }
       },5000);

    }

    private void startAnimation(){
        if (view == null || view.getVisibility() == View.VISIBLE){
            return;
        }

        view.setSystemUiVisibility(1280);
        float f2 = 0f;
        float f3 = (1.2f - f2) * 500.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(f2, 1.2f);
        ofFloat.setDuration((long) f3);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: sg0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {

            }
        });
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

            }
        });
        ofFloat.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ofFloat.start();

    }
    private void addChild() {
        ConstraintLayout layout = findViewById(R.id.root);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT
                , ConstraintLayout.LayoutParams.MATCH_PARENT);
        view = LayoutInflater.from(this).inflate(R.layout.layout_window_center, null);
        view.setVisibility(View.GONE);
        layout.addView(view, layoutParams);
    }

    private void addWindow() {
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.format = -2;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        View view = LayoutInflater.from(this).inflate(R.layout.layout_window_center, null);
//        view.setVisibility(View.GONE);
        windowManager.addView(view, layoutParams);
    }

    private void start() {
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.width = 400;
        layoutParams.height = 50;
        ImageView imageView = new FloatWindowImageView(windowManager, layoutParams, this);
        imageView.setBackgroundColor(getColor(R.color.colorAccent));

    }
}
