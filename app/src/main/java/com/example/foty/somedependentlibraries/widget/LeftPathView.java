package com.example.foty.somedependentlibraries.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class LeftPathView extends View {

    Path path;
    Paint paint;

    Path pathTouch;
    Paint paintTouch;

    int mWidth = 0;
    int mHeight = 0;

    int visibleHeight = 20;

    public LeftPathView(Context context) {
        this(context, null);
    }

    public LeftPathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LeftPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        path = new Path();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        pathTouch = new Path();
        paintTouch = new Paint();
        paintTouch.setColor(Color.parseColor("#80904EB3"));
        paintTouch.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        Log.d("lxx", "LeftPathView: width= " + getWidth() + ", height= " + getHeight());

        canvas.drawArc(new RectF(-mWidth, 0, mWidth, mWidth * 2), 270, 90, true, paintTouch);
        canvas.drawArc(new RectF(-mWidth, mHeight - mWidth * 2, mWidth, mHeight), 90, -90, true, paintTouch);
        pathTouch.addRect(new RectF(0,  mWidth, mWidth, mHeight - mWidth), Path.Direction.CCW);
        canvas.drawPath(pathTouch, paintTouch);


//        canvas.drawArc(new RectF(0, mHeight - visibleHeight, visibleHeight * 2, mHeight * 2), 180, 90, true, paint);
//        canvas.drawArc(new RectF(mWidth - visibleHeight * 2, mHeight - visibleHeight, mWidth, mHeight * 2), 0, -90, true, paint);
//        path.addRect(new RectF(visibleHeight, mHeight - visibleHeight, mWidth - visibleHeight, mHeight), Path.Direction.CW);
//        canvas.drawPath(path, paint);


    }
}
