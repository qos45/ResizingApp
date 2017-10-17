package com.example.a5mict.testapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dev01 on 25-9-2017.
 */

public class ReservationRowAdapter extends ArrayAdapter<Reservation> implements View.OnTouchListener{
    private ArrayList<Reservation> dataSet;
    Context mContext;

    protected static int INVALID_POINTER_ID = 0x0;

    private int lastPosition = -1;
    private int red_color;
    private int green_color;

    private float mLastTouchX = 0.0f;
    private float mLastTouchY = 0.0f;

    private float y1 = 0.0f;
    private float y2 = 0.0f;
    private int mActivePointerId = 0;

    float x = 0.0f;
    float y = 0.0f;

    private float mPosX = 0.0f;
    private float mPosY = 0.0f;

    ListView testList;

    ScaleGestureDetector mScaleDetector;
    private Float lastSpacing = 0.0f;
    private Boolean canResize = true;
    private Integer resizeOffset = 0;
    private TextView whichWasSelected;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        //mScaleDetector.onTouchEvent(motionEvent);
        //Log.i("Y coor", String.valueOf(view.getY()));
        // Get the index of the pointer associated with the action.

        final int action = MotionEventCompat.getActionMasked(motionEvent);

        switch (action) {

            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(motionEvent);
                int pointerId = motionEvent.getPointerId(pointerIndex);
                float x = MotionEventCompat.getX(motionEvent, pointerIndex);
                float y = MotionEventCompat.getY(motionEvent, pointerIndex);

                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(motionEvent, 0);
                Log.i("Pritiso", "ACTION_DOWN");

                return true;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_MOVE: {

                int index = MotionEventCompat.getActionIndex(motionEvent);

                try {

                    if(canResize)
                    {
                        if (motionEvent.getPointerCount() > 1) {

                            // The coordinates of the current screen contact, relative to
                            // the responding View or Activity.
                            x = MotionEventCompat.getX(motionEvent, index);
                            y = MotionEventCompat.getX(motionEvent, index);
                            int[] location = new int[2];
                            int pos = testList.pointToPosition(location[0], location[1]);
                            //Log.i("MULTITOUCH",String.valueOf(spacing(motionEvent)) + " " + String.valueOf(location[1]));
                            if(spacing(motionEvent) > lastSpacing)
                            {
                                Reservation res = getItem(pos);
                                Calendar c = Calendar.getInstance();
                                c.setTime(res.getStartDate());
                                c.add(Calendar.MINUTE, 15);
                                res.setStopDate(c.getTime());
                                LinearLayout layout = (LinearLayout) view;
                                int count = layout.getChildCount();
                                View v = null;
                                for(int i = 0; i < count; i++)
                                {
                                    v = layout.getChildAt(i);
                                    if(v instanceof LinearLayout)
                                    {
                                        layout = (LinearLayout) v;
                                        for(int j = 0; j < layout.getChildCount(); j++)
                                        {
                                            View tmp = layout.getChildAt(j);

                                            if(tmp instanceof TextView)
                                            {
                                                if(tmp.getTag().equals("duzina"))
                                                    ((TextView) tmp).setText(res.getDuration());
                                            }
                                        }
                                    }
                                }
                                ViewGroup.LayoutParams params = view.getLayoutParams();
                                int height = view.getHeight() + 20;
                                //Log.i("Menjam","Promena " + String.valueOf(height));
                                params.height= height;
                                view.setLayoutParams(params);
                                //view.setLayoutParams(new LinearLayout.LayoutParams(view.getWidth(), (int) (view.getHeight()+ dy)));
                                view.invalidate();
                                canResize = false;
                            }
                            else {
                                ViewGroup.LayoutParams params = view.getLayoutParams();
                                int height = view.getHeight() - 20;
                                //Log.i("Menjam","Promena " + String.valueOf(height));
                                params.height= height;
                                view.setLayoutParams(params);
                                //view.setLayoutParams(new LinearLayout.LayoutParams(view.getWidth(), (int) (view.getHeight()+ dy)));
                                view.invalidate();
                                canResize = false;
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    /*Log.i("Exception","Ulazi");
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int)(view.getHeight()+ dy), mContext.getResources().getDisplayMetrics());
                    params.height= height;
                    view.setLayoutParams(params);
                    //view.setLayoutParams(new LinearLayout.LayoutParams(view.getWidth(), (int) (view.getHeight()+ dy)));
                    view.invalidate();
                    mPosX += dx;
                    mPosY += dy;*/
                }

                // Calculate the distance moved
                mLastTouchX = x;
                mLastTouchY = y;
                // Remember this touch position for the next move event

                //Log.d("Pomera", "ACTION_MOVE");
                return false;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(motionEvent, mActivePointerId);
                lastSpacing = spacing(motionEvent);
                //final int pointerIndex =
                //MotionEventCompat.findPointerIndex(motionEvent, mActivePointerId);
                //final float x = MotionEventCompat.getX(motionEvent, pointerIndex);
                //final float y = MotionEventCompat.getY(motionEvent, pointerIndex);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                canResize = true;
                //Log.i("POINTER UP", "Pozvan");
                break;


        }
        return true;
    }


    /**
     102.
     * Determine the space between the first two fingers
     103.
     */

    private float spacing(MotionEvent event) {

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        y1 = event.getY(0);
        y2 = event.getY(1);

        return (float) Math.sqrt(x * x + y * y);

    }


    private void midPoint(PointF point, MotionEvent event) {

        float x = event.getX(0) + event.getX(1);

        float y = event.getY(0) + event.getY(1);

        point.set(x / 2, y / 2);

    }

    private float rotation(MotionEvent event) {

        double delta_x = (event.getX(0) - event.getX(1));

        double delta_y = (event.getY(0) - event.getY(1));

        double radians = Math.atan2(delta_y, delta_x);

        return (float) Math.toDegrees(radians);

    }

    private static class ViewHolderResAct{
        TextView res_name;
        TextView time;
        TextView duration;
        LinearLayout layout;
    }

    public ReservationRowAdapter(ArrayList<Reservation> data, Context context, ListView lv1){
        super(context, R.layout.reservation_row,data);
        this.dataSet = data;
        this.mContext = context;
        red_color = ContextCompat.getColor(getContext(), R.color.busyRes);
        green_color = ContextCompat.getColor(getContext(), R.color.freeRes);
        testList = lv1;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        Reservation reservation = getItem(position);
        final ViewHolderResAct viewHolderResAct;
        final View result;

        if(convertView==null){
            viewHolderResAct = new ViewHolderResAct();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.reservation_row, parent, false);
            viewHolderResAct.res_name = (TextView) convertView.findViewById(R.id.reservationText);
            viewHolderResAct.time = (TextView) convertView.findViewById(R.id.timeText);
            viewHolderResAct.duration = (TextView) convertView.findViewById(R.id.durationText);
            viewHolderResAct.layout = (LinearLayout)convertView.findViewById(R.id.matke);

            if(!reservation.isReserved()) {
                //Log.i("OFFSET", String.valueOf((int)(60*reservation.getDurationMinutes()/45)));
                //resizeOffset = (int)(60*reservation.getDurationMinutes()/45);
            }

            result = convertView;
            convertView.setTag(viewHolderResAct);
        }
        else{
            viewHolderResAct = (ViewHolderResAct) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolderResAct.time.setText(reservation.getStartTime());
        viewHolderResAct.duration.setText(reservation.getDuration());
        viewHolderResAct.res_name.setText(reservation.getSubject());
        //viewHolderResAct.layout.setOnTouchListener(new MyScaleGestures(mContext));
        //viewHolderResAct.layout.setOnTouchListener(this);

        /*viewHolderResAct.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent testIntent = new Intent(getContext(), ResizableRectangleActivity.class);
                getContext().startActivity(testIntent);
            }
        });*/

        if(reservation.isReserved()){
            viewHolderResAct.res_name.setBackgroundColor(red_color);
        }
        else{
            viewHolderResAct.res_name.setBackgroundColor(green_color);
        }

        ViewGroup.LayoutParams params = viewHolderResAct.layout.getLayoutParams();

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int)(60*reservation.getDurationMinutes()/45), mContext.getResources().getDisplayMetrics());
        params.height= height;

        return convertView;
    }


}
