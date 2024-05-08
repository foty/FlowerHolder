package com.example.foty.flower.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

public class FloatWindowImageView extends AppCompatImageView implements View.OnTouchListener {
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private Boolean isMove = false;
    private float lastX;
 
    public FloatWindowImageView(WindowManager windowManager,WindowManager.LayoutParams layoutParams,Context context) {
        super(context);
        this.windowManager = windowManager;
        this.layoutParams = layoutParams;
        setOnTouchListener(this);
        setOnClickListener(v -> Toast.makeText(context,"点击了",Toast.LENGTH_LONG).show());
        requestSettingCanDrawOverlays();
    }
 
    public FloatWindowImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public FloatWindowImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
 
 
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getRawX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - lastX) > ViewConfiguration.get(getContext()).getScaledTouchSlop()){
                    isMove = true;
                    layoutParams.x = (int) (event.getRawX() - getWidth() / 2);
                    layoutParams.y = (int) (event.getRawY() - getHeight() / 2);
                    windowManager.updateViewLayout(this,layoutParams);
                }
                return true;
                case MotionEvent.ACTION_UP:
                    if (isMove){
                        return true;
                    }
        }
        lastX = x;
        return super.onTouchEvent(event);
    }
 
// 申请悬浮权限
    private void requestSettingCanDrawOverlays() {
        try {
            //判断当前系统版本
            if (Build.VERSION.SDK_INT >= 23) {
                //判断权限是否已经申请过了（加上这个判断，则使用的悬浮窗的时候；如果权限已经申请则不再跳转到权限开启界面）
                if (!Settings.canDrawOverlays(getContext())) {
                    //申请权限
                    Intent intent2 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    ((Activity)getContext()).startActivityForResult(intent2, 1001);
                } else {
                    //创建悬浮窗
                    windowManager.addView(this, layoutParams);
                }
            } else {
                windowManager.addView(this, layoutParams);
            }
            System.out.println("Build.VERSION.SDK_INT::::" + Build.VERSION.SDK_INT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
 
// 申请完权限后显示出来
    public void showFloatWindow(){
        windowManager.addView(this, layoutParams);
    }
}