package com.example.a5mict.testapp;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.support.v7.widget.AppCompatTextView;
import android.widget.TextView;

/**
 * Created by dev01 on 4-10-2017.
 */

public class ScaleableTextView extends AppCompatTextView implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener{

    ScaleGestureDetector mScaleDetector =
            new ScaleGestureDetector(getContext(), this);

    private float mScaleFactor = 1.f;

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        mScaleFactor *= scaleGestureDetector.getScaleFactor();

        // Don't let the object get too small or too large.
        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

    }

    public ScaleableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mScaleDetector.onTouchEvent(event))
            return true;
        return super.onTouchEvent(event);
    }
}
