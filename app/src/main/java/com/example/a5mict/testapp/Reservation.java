package com.example.a5mict.testapp;


import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by dev01 on 20-9-2017.
 */

public class Reservation {
    private long id;
    private String organizer;
    private Date start;
    private Date stop;
    private String subject;

    private boolean isReserved;

    public Reservation(Date start, Date stop, String subject){
        this.start = start;
        this.stop = stop;
        this.subject = subject;
        isReserved = false;
    }

    public String getOrganizer(){
        int index = organizer.indexOf('|');
        return organizer.substring(0,index);
    }
    public String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return  sdf.format(start);
    }
    public Date getStartDate(){
        return start;
    }
    public Date getStopDate(){
        return stop;
    }
    public void setStopDate(Date stop){
        this.stop=stop;
    }
    public String getStartEndTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        String start_time = sdf.format(start);
        String end_time = sdf.format(stop);
        return start_time + " - " + end_time;
    }

    public void setStartDate(Date start) {
        this.start = start;
    }

    public String getStartTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        return sdf.format(start);
    }

    public String getDuration(){
        /*if(!this.isReserved){
            return "";
        }*/
        long duration  = stop.getTime() - start.getTime();
        long diffInMinutes = Math.round(duration/(60000.0));
        if(diffInMinutes < 60)
            return String.valueOf(diffInMinutes)+" min";
        else{
            long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
            String add_min = "";
            if(diffInMinutes % 60 !=0)
                add_min = " "+String.valueOf(diffInMinutes % 60)+"min";
            return String.valueOf(diffInHours)+"h" +add_min;
        }
    }

    public long getDurationMinutes(){
        long duration  = stop.getTime() - start.getTime();
        long diffInMinutes = Math.round(duration/(60000.0));
        return diffInMinutes;
    }

    public boolean isReserved(){
        return isReserved;
    }

    public void setReserved(){
        this.isReserved = true;
    }

    public String getSubject(){
        return subject;
    }

    public void setSubject(String subject){
        this.subject = subject;
    }

}
