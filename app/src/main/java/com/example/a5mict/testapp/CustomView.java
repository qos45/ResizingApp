package com.example.a5mict.testapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 5M ICT on 10/9/2017.
 */

public class CustomView extends View {

    private Rect rectangle;

    private Canvas temp;
    private Paint paint;
    private Paint p = new Paint();
    private Paint transparentPaint;


    public CustomView(Context context) {
        super(context);
        int x = 100;
        int y = 100;
        int sideLength = 200;
        setWillNotDraw(false);

        //this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // create a rectangle that we'll draw later
        rectangle = new Rect(x, y, sideLength, sideLength);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("Canv width", String.valueOf(canvas.getWidth()));
        Log.i("Canv hei", String.valueOf(canvas.getHeight()));
        setMeasuredDimension(100, 100);
        canvas.drawRect(rectangle,paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i("Canvas width", String.valueOf(MeasureSpec.getSize(widthMeasureSpec)));
        Log.i("Canvas hei", String.valueOf(MeasureSpec.getSize(heightMeasureSpec)));
        setMeasuredDimension(100, 100);

    }
}
