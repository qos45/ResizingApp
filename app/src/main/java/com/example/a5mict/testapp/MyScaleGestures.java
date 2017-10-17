package com.example.a5mict.testapp;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by 5M ICT on 10/4/2017.
 */

public class MyScaleGestures implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {
    private View view;
    private ScaleGestureDetector gestureScale;
    private float scaleFactor = 1;
    private boolean inScale = false;
    private Context context;
    //private ListView myListView;

    public MyScaleGestures(Context c){
        gestureScale = new ScaleGestureDetector(c, this);
        this.context = c;
        //this.myListView = listView;

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        this.view = view;
        gestureScale.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        Log.d("POVECAVA","DA");
        scaleFactor *= detector.getScaleFactor();
        //scaleFactor = (scaleFactor < 1 ? 0.9f : scaleFactor); // prevent our view from becoming too small //
        scaleFactor = ((float)((int)(scaleFactor * 100))) / 100; // Change precision to help with jitter when user just rests their fingers //
        //view.setScaleX(scaleFactor);
        view.setScaleY(scaleFactor);
        view.getLayoutParams().height = view.getHeight()+ 25;
        view.requestLayout();
       // ViewGroup.LayoutParams params = view.getLayoutParams();
        //int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int)(view.getHeight()+ scaleFactor), context.getResources().getDisplayMetrics());
        //params.height= height;
        //view.setLayoutParams(params);
        //view.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), (int) (view.getHeight()+ 0.5f)));
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        inScale = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        inScale = false;

    }
}