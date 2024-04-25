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

public class TopPathView extends View {

    Path path;
    Paint paint;

    Path pathTouch;
    Paint paintTouch;

    int mWidth = 0;
    int mHeight = 0;

    int visibleHeight = 17;

    public TopPathView(Context context) {
        this(context, null);
    }

    public TopPathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TopPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        path = new Path();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1f);

        pathTouch = new Path();
        paintTouch = new Paint();
        paintTouch.setColor(Color.parseColor("#80904EB3"));
        paintTouch.setStyle(Paint.Style.FILL_AND_STROKE);
        paintTouch.setStrokeWidth(1f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        Log.d("lxx", "width= " + getWidth() + ", height= " + getHeight());

        pathTouch.reset();
        pathTouch.moveTo(0, 0);
        pathTouch.arcTo(new RectF(0, 0, mHeight, mHeight), 180, -90);
        pathTouch.lineTo(mWidth - mHeight, mHeight);
        pathTouch.arcTo(new RectF(mWidth - mHeight, 0, mWidth, mHeight), 90, -90);
        pathTouch.lineTo(mWidth,0);
        canvas.drawPath(pathTouch, paintTouch);


        path.reset();
        path.moveTo(0, 0);
        path.arcTo(new RectF(0, 0, visibleHeight, visibleHeight), 180, -90);
        path.lineTo(mWidth - visibleHeight, visibleHeight);
        path.arcTo(new RectF(mWidth - visibleHeight, 0, mWidth, visibleHeight), 90, -90);
        path.lineTo(mWidth,0);
        canvas.drawPath(path, paint);


    }
}
