package com.example.a5mict.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ReservationActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    ArrayList<Reservation> res_today;
    ArrayList<Reservation> res_tommorow;
    ArrayList<Reservation> res_next;

    ListView listView_res_today;
    ListView listView_res_tomorrow;
    ListView listView_res_next;

    private String room_name;
    private String room_id;

    int grayCircleWidth = 0;

    ArrayList<Reservation> ourReservations;
    SharedPreferences prefs;

    private static ReservationRowAdapter res_adapter;

    DrawView ourView;
    RelativeLayout vp;
    BitmapDrawable bd;
    int ballWidth = 0;
    final int[] edgeOfTop = {0};
    final int[] selfEdgeUp = {0};
    final int[] selfEdgeDown = {0};
    final int[] edgeOfBottom = {0};

    int itemStep = 0;

    RelativeLayout buttonsForChanges;
    ImageButton cancelChanges, submitChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_reservation);

        //mToolbar = (Toolbar) findViewById(R.id.nav_action);
        //setSupportActionBar(mToolbar);

        prefs = getSharedPreferences("Info", MODE_PRIVATE);

        bd=(BitmapDrawable) this.getResources().getDrawable(R.drawable.gray_circle_small);
        ballWidth = bd.getBitmap().getWidth();
        TabHost host = (TabHost)findViewById(R.id.tabHost_res);
        host.setup();

        Drawable d = getResources().getDrawable(R.drawable.gray_circle_small);
        grayCircleWidth = d.getIntrinsicWidth() / 2;
        //Tab1
        TabHost.TabSpec spec = host.newTabSpec("Today");
        spec.setContent(R.id.tab1_res);
        spec.setIndicator("Today");
        host.addTab(spec);
        //Tab2
        spec = host.newTabSpec("Tomorrow");
        spec.setContent(R.id.tab2_res);
        spec.setIndicator("Tomorrow");
        host.addTab(spec);
        //Tab3
        spec = host.newTabSpec("Next");
        spec.setContent(R.id.tab3_res);
        spec.setIndicator("Next");
        host.addTab(spec);

        Intent intent = getIntent();
        room_id = String.valueOf(intent.getLongExtra("id",37));
        room_name = intent.getStringExtra("name");
        //getSupportActionBar().setTitle("Reserve "+room_name);

        ourReservations = new ArrayList<Reservation>();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND,0);
        Calendar c1 =(Calendar)c.clone();
        Calendar c2 = (Calendar)c.clone();
        c1.set(Calendar.HOUR_OF_DAY,8);
        c1.set(Calendar.MINUTE,30);
        c2.set(Calendar.HOUR_OF_DAY,11);
        c2.set(Calendar.MINUTE,0);
        Reservation tmp = new Reservation(c1.getTime(),c2.getTime(),"Anything" );
        tmp.setReserved();
        ourReservations.add(tmp);
        c1.set(Calendar.HOUR_OF_DAY, 12);
        c1.set(Calendar.MINUTE, 30);

        c2.set(Calendar.HOUR_OF_DAY, 13);
        c2.set(Calendar.MINUTE, 15);
        Reservation tmp1 = new Reservation(c1.getTime(),c2.getTime(),"Some description");
        tmp1.setReserved();
        ourReservations.add(tmp1);

        itemStep = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, this.getResources().getDisplayMetrics());

        //set listeneres
        res_today = new ArrayList<Reservation>();
        listView_res_today = (ListView)findViewById(R.id.list_res_1);
        /*listView_res_today.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setOnTouchListener(new MyScaleGestures(ReservationActivity.this));
            }
        });*/


        listView_res_today.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, final int i, long l) {

                selfEdgeUp[0] = (int) view.getY() - ballWidth / 2;
                selfEdgeDown[0] = (int) view.getY() + view.getHeight() - ballWidth / 2;

                prefs.edit().putInt("selfEdgeUp", selfEdgeUp[0]).apply();
                prefs.edit().putInt("selfEdgeDown", selfEdgeDown[0]).apply();

                final Reservation thisReservation = (Reservation) listView_res_today.getAdapter().getItem(i);

                TabHost tabHost = (TabHost) findViewById(R.id.tabHost_res);

                Display mdisp = getWindowManager().getDefaultDisplay();
                int maxY= mdisp.getHeight();

                int pos = listView_res_today.pointToPosition((int)view.getX(), (int)view.getY());
                for (int p = pos-1; p >= 0; p--)
                {
                    Reservation reservation = (Reservation) listView_res_today.getAdapter().getItem(p);
                    if(reservation.isReserved())
                    {
                        RelativeLayout relativeLayout = (RelativeLayout) getViewByPosition(p, listView_res_today);
                        int y = (int) relativeLayout.getY();
                        edgeOfTop[0] = y + relativeLayout.getHeight() - ballWidth/2;
                        prefs.edit().putInt("edgeOfTop", edgeOfTop[0]).apply();
                        break;
                    }
                    else
                    {
                        edgeOfTop[0] = - ballWidth/2;
                        prefs.edit().putInt("edgeOfTop", edgeOfTop[0]).apply();
                    }
                }

                for (int index = pos+1; index < listView_res_today.getCount(); index++)
                {
                    Reservation reservation = (Reservation) listView_res_today.getAdapter().getItem(index);
                    if(reservation.isReserved())
                    {
                        RelativeLayout relativeLayout = (RelativeLayout) getViewByPosition(index, listView_res_today);
                        int y = (int) relativeLayout.getY();
                        edgeOfBottom[0] = y - ballWidth/2;
                        prefs.edit().putInt("edgeOfBottom", edgeOfBottom[0]).apply();
                        break;
                    }
                    else
                    {
                        edgeOfBottom[0] = maxY - ballWidth/2 - tabHost.getTabWidget().getHeight() - 5;
                        prefs.edit().putInt("edgeOfBottom", edgeOfBottom[0]).apply();
                    }
                }

                RelativeLayout relativeLayout = (RelativeLayout) view;
                LinearLayout linearLayout = (LinearLayout) relativeLayout.getChildAt(0);
                final LinearLayout resLayout = (LinearLayout) linearLayout.getChildAt(0);
                final TextView[] textView = {(TextView) linearLayout.getChildAt(1)};
                vp = (RelativeLayout) view.getParent().getParent();

                Log.i("Da li si uso", String.valueOf(vp));
                int[] coords = {0,0};
                view.getLocationOnScreen(coords);
                int absoluteTop = coords[1];
                int absoluteBottom = coords[1] + view.getHeight();
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) resLayout.getLayoutParams();
                ArrayList<Point> points = new ArrayList<Point>();
                Point point1 = new Point((int) view.getX() + resLayout.getWidth() - lp.leftMargin, (int) view.getY() - grayCircleWidth);
                Point point2 = new Point((int) view.getX() + resLayout.getWidth() - lp.leftMargin, (int) view.getY() + view.getHeight() - grayCircleWidth);
                final Point point3 = new Point((int) view.getX() + view.getWidth() - grayCircleWidth, (int) view.getY() - grayCircleWidth);
                final Point point4 = new Point((int) view.getX() + view.getWidth() - grayCircleWidth, (int) view.getY() + view.getHeight()- grayCircleWidth);

                points.add(point1);
                points.add(point2);
                points.add(point3);
                points.add(point4);

                LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = null;
                v = inflater.inflate(R.layout.buttons_layout, null);

                buttonsForChanges = (RelativeLayout) v.findViewById(R.id.buttonsForChanges);
                cancelChanges = (ImageButton) v.findViewById(R.id.cancelChanges);
                submitChanges = (ImageButton) v.findViewById(R.id.submitChanges);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cancelChanges.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                params.setMargins(5,5,5,5);
                cancelChanges.setLayoutParams(params);

                params = (RelativeLayout.LayoutParams) submitChanges.getLayoutParams();
                params.addRule(RelativeLayout.START_OF, R.id.cancelChanges);
                params.setMargins(5,5,5,5);
                submitChanges.setLayoutParams(params);

                buttonsForChanges.setVisibility(View.VISIBLE);
                buttonsForChanges.bringToFront();

                cancelChanges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vp.removeView(ourView);
                        buttonsForChanges.setVisibility(View.GONE);
                    }
                });

                submitChanges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View buttonView) {

                        //Log.i("ourView", String.valueOf(ourView.point3.y));
                        //Log.i("ourView", String.valueOf(ourView.point4.y));

                        int diffBetBalls = 0;
                        int offset = 0;
                        int posOfTopBall = listView_res_today.pointToPosition(ourView.point4.x, ourView.point4.y + ballWidth / 2);// position for list item where is top resize ball
                        int posOfBottomBall = listView_res_today.pointToPosition(ourView.point3.x, ourView.point3.y + ballWidth / 4); // position for list item where is bottom resize ball

                        if(posOfTopBall == -1)
                        {
                            posOfTopBall = listView_res_today.pointToPosition(ourView.point4.x, ourView.point4.y + ballWidth / 2 + 5);
                        }
                        if(posOfBottomBall == -1)
                        {
                            posOfBottomBall = listView_res_today.pointToPosition(ourView.point3.x, ourView.point3.y + ballWidth / 4 + 5);
                        }

                        Log.i("Err", "Top: " + String.valueOf(ourView.point4.y));
                        Log.i("Err", "Bottom: " + String.valueOf(ourView.point3.y));

                        Reservation topReservation = (Reservation) listView_res_today.getAdapter().getItem(posOfTopBall);   // Rezervacija do koje dolazi gornja kuglica
                        Reservation bottomReservation = (Reservation) listView_res_today.getAdapter().getItem(posOfBottomBall); // Rezervacija do koje dolazi donja kuglica

                        int durInMinutesTop = (int) topReservation.getDurationMinutes(); // Duzina trajanja gornje rezervacije
                        int durInMinutesBottom = (int) bottomReservation.getDurationMinutes(); // Duzina trajanja donje rezervacije

                        int yOfTopRelative = 0;
                        int yOfBottomRelative = 0;

                        RelativeLayout relativeForTop = (RelativeLayout) getViewByPosition(posOfTopBall, listView_res_today);   // Relative layout za gornji element
                        RelativeLayout relativeForBottom = (RelativeLayout) getViewByPosition(posOfBottomBall, listView_res_today); // Relative layout za donji element

                        yOfTopRelative = (int) relativeForTop.getY() - ballWidth / 2; // Koordinata Y za pojedinacne elemente
                        yOfBottomRelative = (int) relativeForBottom.getY() - ballWidth / 2;

                        Log.i("Proba", String.valueOf(ourView.point3.y) + " " + String.valueOf(yOfBottomRelative));

                        int stepOfTop = durInMinutesTop / 15;   // Broj step-ova za termin
                        int stepOfBottom = durInMinutesBottom / 15;

                        // Ako je pozicija i gornjeg i donjeg elementa jednaka izabranom elementu onda se nista ne desava
                        if (posOfTopBall == i && posOfBottomBall == i) {
                            vp.removeView(ourView);
                            buttonsForChanges.setVisibility(View.GONE);
                            return;
                        }
                        // Ako je pozicija gornjeg elementa jednaka izabranom elementu onda se obradjuje samo donji
                        else if(posOfTopBall == i)
                        {
                            if(posOfBottomBall - i >= 2)
                                deleteBetItems(i+1, posOfBottomBall, true); // deleteBetItems funkcija brise sve medju rezervacije i spaja sa trenutno izabranom

                            // Ako je duzina termina 15 minuta onda se taj element brise i samo se dodaje njegova duzina izabranoj rezervaciji
                            if (durInMinutesBottom <= 15) {
                                res_today.remove(posOfBottomBall);
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.SECOND, 0);
                                Calendar c1 = (Calendar) c.clone();
                                c1.set(Calendar.HOUR_OF_DAY, bottomReservation.getStartDate().getHours());
                                c1.set(Calendar.MINUTE, bottomReservation.getStartDate().getMinutes() + 15);
                                thisReservation.setStopDate(c1.getTime());
                                bottomReservation.setStartDate(c1.getTime());
                            }
                            // Ako nije onda se obradjuje standardno po step-ovima
                            else
                            {
                                for (int k = 1; k <= stepOfBottom; k++) {
                                    if (ourView.point3.y < yOfBottomRelative + k * itemStep) {
                                        LinearLayout linearOfBottom = (LinearLayout) relativeForBottom.getChildAt(0);
                                        if (k == stepOfBottom) {
                                            linearOfBottom.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), itemStep));
                                            diffBetBalls += (k - 1) * itemStep;
                                            offset = (k - 1) * 15;
                                        } else {
                                            linearOfBottom.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), (stepOfBottom - k) * itemStep));
                                            diffBetBalls += k * itemStep;
                                            offset = k * 15;
                                        }

                                        Calendar c = Calendar.getInstance();
                                        c.set(Calendar.SECOND, 0);
                                        Calendar c1 = (Calendar) c.clone();
                                        c1.set(Calendar.HOUR_OF_DAY, bottomReservation.getStartDate().getHours());
                                        c1.set(Calendar.MINUTE, bottomReservation.getStartDate().getMinutes() + offset);
                                        thisReservation.setStopDate(c1.getTime());
                                        bottomReservation.setStartDate(c1.getTime());
                                        Log.i("test", String.valueOf(c1.getTime()));

                                        linearOfBottom.requestLayout();

                                        break;
                                    }
                                }
                            }
                        }
                        // Ako je pozicija donjeg elementa jednaka izabranom elementu onda se obradjuje samo gornji
                        else if(posOfBottomBall == i)
                        {
                            if(i - posOfTopBall >= 2)
                                deleteBetItems(i-1, posOfTopBall, false);

                            if (durInMinutesTop <= 15) {
                                res_today.remove(posOfTopBall);
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.SECOND, 0);
                                Calendar c1 = (Calendar) c.clone();
                                c1.set(Calendar.HOUR_OF_DAY, thisReservation.getStartDate().getHours());
                                c1.set(Calendar.MINUTE, thisReservation.getStartDate().getMinutes() - 15);
                                topReservation.setStopDate(c1.getTime());
                                thisReservation.setStartDate(c1.getTime());
                            }
                            else {
                                for (int k = 1; k <= stepOfTop; k++) {
                                    if (ourView.point4.y != yOfTopRelative) {
                                        if (ourView.point4.y < yOfTopRelative + k * itemStep) {

                                            LinearLayout linearOfTop = (LinearLayout) relativeForTop.getChildAt(0);
                                            if (k - 1 == 0) {
                                                linearOfTop.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), itemStep));
                                                diffBetBalls += (stepOfTop - 1) * itemStep;
                                                offset = (stepOfTop - 1) * 15;
                                            } else if (k == stepOfTop) {
                                                linearOfTop.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), (k - 1) * itemStep));
                                                diffBetBalls += itemStep;
                                                offset = 15;
                                            } else {
                                                linearOfTop.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), k * itemStep));
                                                diffBetBalls += (stepOfTop - k) * itemStep;
                                                offset = (stepOfTop - k) * 15;
                                            }

                                            Calendar calendar = Calendar.getInstance();
                                            calendar.set(Calendar.SECOND, 0);
                                            Calendar calendar1 = (Calendar) calendar.clone();
                                            calendar1.set(Calendar.HOUR_OF_DAY, thisReservation.getStartDate().getHours());
                                            calendar1.set(Calendar.MINUTE, thisReservation.getStartDate().getMinutes() - offset);
                                            topReservation.setStopDate(calendar1.getTime());
                                            thisReservation.setStartDate(calendar1.getTime());
                                            Log.i("test", String.valueOf(calendar1.getTime()));
                                            //res_adapter.notifyDataSetChanged();
                                            linearOfTop.requestLayout();

                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        // Ako su pozicije oba elementa razlicite od trenutno izabranog, tj obe kuglice su pomerene
                        else {

                            if(posOfBottomBall - i >= 2)
                                deleteBetItems(i+1, posOfBottomBall, true);

                            if(i - posOfTopBall >= 2)
                                deleteBetItems(i-1, posOfTopBall, false);

                            if (durInMinutesTop <= 15) {
                                res_today.remove(posOfTopBall);
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.SECOND, 0);
                                Calendar c1 = (Calendar) c.clone();
                                c1.set(Calendar.HOUR_OF_DAY, thisReservation.getStartDate().getHours());
                                c1.set(Calendar.MINUTE, thisReservation.getStartDate().getMinutes() - 15);
                                topReservation.setStopDate(c1.getTime());
                                thisReservation.setStartDate(c1.getTime());

                                if (durInMinutesBottom > 15) {
                                    for (int k = 1; k <= stepOfBottom; k++) {
                                        if (ourView.point3.y < yOfBottomRelative + k * itemStep) {
                                            LinearLayout linearOfBottom = (LinearLayout) relativeForBottom.getChildAt(0);
                                            if (k == stepOfBottom) {
                                                linearOfBottom.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), itemStep));
                                                diffBetBalls += (k - 1) * itemStep;
                                                offset = (k - 1) * 15;
                                            } else {
                                                linearOfBottom.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), (stepOfBottom - k) * itemStep));
                                                diffBetBalls += k * itemStep;
                                                offset = k * 15;
                                            }

                                            Calendar cal = Calendar.getInstance();
                                            cal.set(Calendar.SECOND, 0);
                                            Calendar cal2 = (Calendar) cal.clone();
                                            cal2.set(Calendar.HOUR_OF_DAY, bottomReservation.getStartDate().getHours());
                                            cal2.set(Calendar.MINUTE, bottomReservation.getStartDate().getMinutes() + offset);
                                            thisReservation.setStopDate(cal2.getTime());
                                            bottomReservation.setStartDate(cal2.getTime());
                                            Log.i("test", String.valueOf(cal2.getTime()));

                                            linearOfBottom.requestLayout();

                                            break;
                                        }
                                    }
                                }
                            }

                            if (durInMinutesBottom <= 15) {
                                res_today.remove(posOfBottomBall);
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.SECOND, 0);
                                Calendar c1 = (Calendar) c.clone();
                                c1.set(Calendar.HOUR_OF_DAY, bottomReservation.getStartDate().getHours());
                                c1.set(Calendar.MINUTE, bottomReservation.getStartDate().getMinutes() + 15);
                                thisReservation.setStopDate(c1.getTime());
                                bottomReservation.setStartDate(c1.getTime());

                                if (durInMinutesTop > 15) {
                                    for (int k = 1; k <= stepOfTop; k++) {
                                        if (ourView.point4.y != yOfTopRelative) {
                                            if (ourView.point4.y < yOfTopRelative + k * itemStep) {

                                                LinearLayout linearOfTop = (LinearLayout) relativeForTop.getChildAt(0);
                                                if (k - 1 == 0) {
                                                    linearOfTop.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), itemStep));
                                                    diffBetBalls += (stepOfTop - 1) * itemStep;
                                                    offset = (stepOfTop - 1) * 15;
                                                } else if (k == stepOfTop) {
                                                    linearOfTop.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), (k - 1) * itemStep));
                                                    diffBetBalls += itemStep;
                                                    offset = 15;
                                                } else {
                                                    linearOfTop.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), k * itemStep));
                                                    diffBetBalls += (stepOfTop - k) * itemStep;
                                                    offset = (stepOfTop - k) * 15;
                                                }

                                                Calendar c2 = Calendar.getInstance();
                                                c2.set(Calendar.SECOND, 0);
                                                Calendar c3 = (Calendar) c2.clone();
                                                c3.set(Calendar.HOUR_OF_DAY, thisReservation.getStartDate().getHours());
                                                c3.set(Calendar.MINUTE, thisReservation.getStartDate().getMinutes() - offset);
                                                topReservation.setStopDate(c3.getTime());
                                                thisReservation.setStartDate(c3.getTime());
                                                Log.i("test", String.valueOf(c3.getTime()));
                                                //res_adapter.notifyDataSetChanged();
                                                linearOfTop.requestLayout();

                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            if (durInMinutesTop > 15 && durInMinutesBottom > 15) {
                                for (int k = 1; k <= stepOfTop; k++) {
                                    if (ourView.point4.y != yOfTopRelative) {
                                        if (ourView.point4.y < yOfTopRelative + k * itemStep) {

                                            LinearLayout linearOfTop = (LinearLayout) relativeForTop.getChildAt(0);
                                            if (k - 1 == 0) {
                                                linearOfTop.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), itemStep));
                                                diffBetBalls += (stepOfTop - 1) * itemStep;
                                                offset = (stepOfTop - 1) * 15;
                                            } else if (k == stepOfTop) {
                                                linearOfTop.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), (k - 1) * itemStep));
                                                diffBetBalls += itemStep;
                                                offset = 15;
                                            } else {
                                                linearOfTop.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), k * itemStep));
                                                diffBetBalls += (stepOfTop - k) * itemStep;
                                                offset = (stepOfTop - k) * 15;
                                            }

                                            Calendar calendar = Calendar.getInstance();
                                            calendar.set(Calendar.SECOND, 0);
                                            Calendar calendar1 = (Calendar) calendar.clone();
                                            calendar1.set(Calendar.HOUR_OF_DAY, thisReservation.getStartDate().getHours());
                                            calendar1.set(Calendar.MINUTE, thisReservation.getStartDate().getMinutes() - offset);
                                            topReservation.setStopDate(calendar1.getTime());
                                            thisReservation.setStartDate(calendar1.getTime());
                                            Log.i("test", String.valueOf(calendar1.getTime()));
                                            //res_adapter.notifyDataSetChanged();
                                            linearOfTop.requestLayout();

                                            break;
                                        }
                                    }
                                }

                                for (int k = 1; k <= stepOfBottom; k++) {
                                    if (ourView.point3.y < yOfBottomRelative + k * itemStep) {
                                        LinearLayout linearOfBottom = (LinearLayout) relativeForBottom.getChildAt(0);
                                        if (k == stepOfBottom) {
                                            linearOfBottom.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), itemStep));
                                            diffBetBalls += (k - 1) * itemStep;
                                            offset = (k - 1) * 15;
                                        } else {
                                            linearOfBottom.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), (stepOfBottom - k) * itemStep));
                                            diffBetBalls += k * itemStep;
                                            offset = k * 15;
                                        }

                                        Calendar c = Calendar.getInstance();
                                        c.set(Calendar.SECOND, 0);
                                        Calendar c1 = (Calendar) c.clone();
                                        c1.set(Calendar.HOUR_OF_DAY, bottomReservation.getStartDate().getHours());
                                        c1.set(Calendar.MINUTE, bottomReservation.getStartDate().getMinutes() + offset);
                                        thisReservation.setStopDate(c1.getTime());
                                        bottomReservation.setStartDate(c1.getTime());
                                        Log.i("test", String.valueOf(c1.getTime()));

                                        linearOfBottom.requestLayout();

                                        break;
                                    }
                                }
                            }
                        }
                        RelativeLayout rl = (RelativeLayout) view;
                        LinearLayout linear1 = (LinearLayout) rl.getChildAt(0);

                        linear1.setLayoutParams(new RelativeLayout.LayoutParams(view.getWidth(), view.getHeight() + diffBetBalls));
                        linear1.requestLayout();

                        vp.removeView(ourView);
                        buttonsForChanges.setVisibility(View.GONE);

                        res_adapter.notifyDataSetChanged();
                    }
                });

                ourView = new DrawView(ReservationActivity.this, points, relativeLayout.getWidth(), relativeLayout.getHeight());
                ourView.bringToFront();
                //ourView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                vp.addView(ourView,  new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                vp.addView(v);
                //setContentView(v);
                return true;
            }
        });
        res_tommorow = new ArrayList<Reservation>();
        listView_res_tomorrow = (ListView)findViewById(R.id.list_res_2);
        res_next = new ArrayList<Reservation>();
        listView_res_next = (ListView)findViewById(R.id.list_res_3);

        res_today = fillReservationList(c, ourReservations);
        setReservationList(this.listView_res_today, res_today, c);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //fetching
        try{


            /*SoapConnector soapConn_res1 = new SoapConnector();
            soapConn_res1.setDelegate(this);
            soapConn_res1.setTag("Today");
            soapConn_res1.execute("GetReservationsForRoomAndDate", "roomId", room_id,"date", formatter.format(Calendar.getInstance().getTime()));

            SoapConnector soapConn_res2 = new SoapConnector();
            soapConn_res2.setDelegate(this);
            soapConn_res2.setTag("Tomorrow");
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.HOUR,24);
            soapConn_res2.execute("GetReservationsForRoomAndDate", "roomId", room_id,"date", formatter.format(tomorrow.getTime()));

            SoapConnector soapConn_res3 = new SoapConnector();
            soapConn_res3.setDelegate(this);
            soapConn_res3.setTag("Next");
            Calendar next = Calendar.getInstance();
            next.add(Calendar.HOUR,48);
            soapConn_res3.execute("GetReservationsForRoomAndDate", "roomId", room_id,"date", formatter.format(next.getTime()));*/
        }
        catch (Exception e){
            Log.d("Http transfer",e.getMessage());
        }

        try {
            listView_res_today.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Reservation reservation = res_today.get(position);
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.HOUR,2);
                    if(reservation.isReserved() || reservation.getStopDate().before(c.getTime()))
                        return;


                    /*Intent intent = new Intent(ReservationActivity.this, ReserveScreenActivity.class);
                    intent.putExtra("startTime", reservation.getStartDate());
                    intent.putExtra("stopTime", reservation.getStopDate());
                    intent.putExtra("room_name", room_name);
                    intent.putExtra("room_id", room_id.toString());
                    intent.putExtra("maxTime", getLastResDate(reservation,res_today));
                    startActivity(intent);*/
                    //Snackbar.make(view, room.getName() + " will be open soon",Snackbar.LENGTH_LONG).setAction("No Action",null).show();
                }
            });
        }
        catch (Exception e){
            Log.d("Ex",e.getMessage());
        }
        listView_res_tomorrow.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Reservation reservation = res_tommorow.get(position);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.HOUR,2);
                if(reservation.isReserved() || reservation.getStopDate().before(c.getTime()))
                    return;
                /*Intent intent = new Intent(ReservationActivity.this, ReserveScreenActivity.class);
                intent.putExtra("startTime", reservation.getStartDate());
                intent.putExtra("stopTime", reservation.getStopDate());
                intent.putExtra("room_name",room_name);
                intent.putExtra("room_id",room_id.toString());
                intent.putExtra("maxTime", getLastResDate(reservation,res_tommorow));
                startActivity(intent);*/
                //Snackbar.make(view, room.getName() + " will be open soon",Snackbar.LENGTH_LONG).setAction("No Action",null).show();
            }
        });
        listView_res_next.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Reservation reservation = res_next.get(position);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.HOUR,2);
                if(reservation.isReserved() || reservation.getStopDate().before(c.getTime()))
                    return;
                /*Intent intent = new Intent(ReservationActivity.this, ReserveScreenActivity.class);
                intent.putExtra("startTime", reservation.getStartDate());
                intent.putExtra("stopTime", reservation.getStopDate());
                intent.putExtra("room_name",room_name);
                intent.putExtra("room_id",room_id.toString());
                intent.putExtra("maxTime", getLastResDate(reservation,res_next));
                startActivity(intent);*/
                //Snackbar.make(view, room.getName() + " will be open soon",Snackbar.LENGTH_LONG).setAction("No Action",null).show();
            }
        });
    }

    public void deleteBetItems(int start, int end, boolean whatWay) {
        // start index is the first index for deleting item
        // whatWay represent the way for deleting, up or down - true for down, false for up

        if (whatWay)
        {
            Reservation currentReservation = (Reservation) listView_res_today.getAdapter().getItem(start - 1);
            Reservation firstForDeleting = (Reservation) listView_res_today.getAdapter().getItem(start);
            //Reservation bottomReservation = (Reservation) listView_res_today.getAdapter().getItem(end);

            for(int i = start; i < end; i++)
            {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.SECOND, 0);
                Calendar c1 = (Calendar) c.clone();
                c1.set(Calendar.HOUR_OF_DAY, firstForDeleting.getStartDate().getHours());
                c1.set(Calendar.MINUTE, firstForDeleting.getStartDate().getMinutes() + (int) firstForDeleting.getDurationMinutes());
                currentReservation.setStopDate(c1.getTime());
                res_today.remove(i);
            }
        }
        else {
            Reservation currentReservation = (Reservation) listView_res_today.getAdapter().getItem(start + 1);
            Reservation firstForDeleting = (Reservation) listView_res_today.getAdapter().getItem(start);
            //Reservation bottomReservation = (Reservation) listView_res_today.getAdapter().getItem(end);

            for (int i = start; i > end; i--) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, 0);
                Calendar calendar1 = (Calendar) calendar.clone();
                calendar1.set(Calendar.HOUR_OF_DAY, firstForDeleting.getStartDate().getHours());
                calendar1.set(Calendar.MINUTE, firstForDeleting.getStartDate().getMinutes());
                currentReservation.setStartDate(calendar1.getTime());
                res_today.remove(i);
            }
        }
    }

    public View getViewByPosition(int pos, ListView listView) {
        try {
            final int firstListItemPosition = listView
                    .getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition
                    + listView.getChildCount() - 1;

            if (pos < firstListItemPosition || pos > lastListItemPosition) {
                //This may occure using Android Monkey, else will work otherwise
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                final int childIndex = pos - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date getLastResDate(Reservation reservation,ArrayList<Reservation> list){
        int index = list.indexOf(reservation);
        for(int i=index+1;i<list.size();i++){
            if(list.get(i).isReserved()){
                return list.get(i).getStartDate();
            }
        }
        return list.get(list.size()-1).getStopDate();
    }

    public void setReservationList(ListView lv1, ArrayList<Reservation> resList, Calendar c){
        res_adapter = new ReservationRowAdapter( resList,getApplicationContext(), lv1);
        lv1.setAdapter(res_adapter);
        lv1.setSelection(getCurrentTimePosition(resList,c));
    }

    private int getCurrentTimePosition(ArrayList<Reservation> list,Calendar c){
        int result=0;
        for(Reservation r: list){
            if(r.getStopDate().before(c.getTime())){
                result++;
            }
            else
                break;
        }
        return result;
    }




    public ArrayList<Reservation>  fillReservationList(Calendar day, ArrayList<Reservation> result){
        day.set(Calendar.HOUR_OF_DAY, 6);
        day.set(Calendar.MINUTE, 30);
        day.set(Calendar.SECOND,0);

        Calendar step_cal = (Calendar) day.clone();
        step_cal.add(Calendar.MINUTE,45);

        Calendar end_cal = (Calendar) day.clone();
        end_cal.set(Calendar.HOUR_OF_DAY, 17);
        end_cal.set(Calendar.MINUTE, 30);

        ArrayList<Reservation> res_list = new ArrayList<Reservation>();

        if(result.size()==0){
            while (step_cal.getTime().before(end_cal.getTime()) || step_cal.getTime().equals(end_cal.getTime()) ){
                Reservation newRes = new Reservation(day.getTime(),step_cal.getTime(),"");
                res_list.add(newRes);
                step_cal.add(Calendar.MINUTE,45);
                day.add(Calendar.MINUTE,45);
            }
            // add last small meeting
            if(res_list.size()>0) {
                Reservation last_one_res = res_list.get(res_list.size() - 1);
                long diff_res = end_cal.getTime().getTime() - last_one_res.getStopDate().getTime();
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff_res);
                if (diffInMinutes > 0) {
                    res_list.add(new Reservation(last_one_res.getStopDate(), end_cal.getTime(), ""));
                }
            }
        }
        else {
            for (int i = 0; i < result.size(); i++) {
                Reservation real_res = result.get(i);
                if (real_res.getStartDate().after(day.getTime()) && TimeUnit.MILLISECONDS.toMinutes(real_res.getStartDate().getTime() - day.getTime().getTime())>15) {
                    //create free items
                    while (step_cal.getTime().before(real_res.getStartDate()) || step_cal.getTime().equals(real_res.getStartDate())) {
                        Reservation newRes = new Reservation(day.getTime(), step_cal.getTime(), "");
                        res_list.add(newRes);
                        step_cal.add(Calendar.MINUTE, 45);
                        day.add(Calendar.MINUTE,45);
                    }
                    //adding small meeting
                    if(res_list.size()>0) {
                        Reservation last_res = res_list.get(res_list.size() - 1);
                        long diff_res = real_res.getStartDate().getTime() - last_res.getStopDate().getTime();
                        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff_res);
                        if (diffInMinutes > 0) {
                            res_list.add(new Reservation(last_res.getStopDate(), real_res.getStartDate(), ""));
                        }
                    }
                }
                day.setTime(real_res.getStopDate());
                step_cal.setTime(real_res.getStopDate());
                step_cal.add(Calendar.MINUTE,45);

                res_list.add(real_res);
            }
            //add other possible meetings
            Reservation last_res = res_list.get(res_list.size()-1);
            day.setTime(last_res.getStopDate());
            step_cal.setTime(last_res.getStopDate());
            step_cal.add(Calendar.MINUTE,45);
            while (step_cal.getTime().before(end_cal.getTime()) || step_cal.getTime().equals(end_cal.getTime()) ){
                Reservation newRes = new Reservation(day.getTime(),step_cal.getTime(),"");
                res_list.add(newRes);
                step_cal.add(Calendar.MINUTE,45);
                day.add(Calendar.MINUTE,45);
            }
            // add last small meeting
            if(res_list.size()>0) {
                Reservation last_one_res = res_list.get(res_list.size() - 1);
                long diff_res = end_cal.getTime().getTime() - last_one_res.getStopDate().getTime();
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff_res);
                if (diffInMinutes > 0) {
                    res_list.add(new Reservation(last_one_res.getStopDate(), end_cal.getTime(), ""));
                }
            }
        }
        return res_list;
    }


}
