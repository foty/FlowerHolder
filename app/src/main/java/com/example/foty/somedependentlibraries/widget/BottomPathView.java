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

public class BottomPathView extends View {
    Path path;
    Paint paint;

    Path pathTouch;
    Paint paintTouch;

    int mWidth = 0;
    int mHeight = 0;

    int visibleHeight = 20;

    public BottomPathView(Context context) {
        this(context, null);
    }

    public BottomPathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BottomPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        pathTouch = new Path();
        paintTouch = new Paint();
        paintTouch.setAntiAlias(true);
        paintTouch.setColor(Color.parseColor("#80904EB3"));
        paintTouch.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        Log.d("lxx", "BottomPathView: width= " + getWidth() + ", height= " + getHeight());

//        pathTouch.reset();
//        pathTouch.moveTo(0, 0);
//        pathTouch.arcTo(new RectF(0, 0, mHeight * 2, mHeight * 2), 180, 90, true);
//        pathTouch.lineTo(mWidth - mHeight, 0);
//        pathTouch.arcTo(new RectF(mWidth - mHeight * 2, 0, mWidth, mHeight * 2), 0, -90, true);
//        pathTouch.lineTo(0, mHeight);
//        canvas.drawPath(pathTouch, paintTouch);

        canvas.drawArc(new RectF(0, 0, mHeight * 2, mHeight * 2), 180, 90, true, paintTouch);
        canvas.drawArc(new RectF(mWidth - mHeight * 2, 0, mWidth, mHeight * 2), 0, -90, true, paintTouch);
        pathTouch.addRect(new RectF(mHeight, 0, mWidth - mHeight, mHeight), Path.Direction.CCW);
        pathTouch.moveTo(0,mHeight);
        pathTouch.lineTo(mHeight,0);
        canvas.drawPath(pathTouch, paintTouch);


        canvas.drawArc(new RectF(0, mHeight-visibleHeight, visibleHeight * 2, mHeight * 2), 180, 90, true, paint);
        canvas.drawArc(new RectF(mWidth - visibleHeight * 2, mHeight-visibleHeight, mWidth, mHeight * 2), 0, -90, true, paint);
        path.addRect(new RectF(visibleHeight, mHeight-visibleHeight, mWidth - visibleHeight, mHeight), Path.Direction.CW);
        canvas.drawPath(path, paint);


    }
}
