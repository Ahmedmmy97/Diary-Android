package com.a33y.jo.diary;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.AlarmManager.RTC_WAKEUP;

public class MainActivity extends AppCompatActivity implements DataListener,OnCalendarPageChangeListener,CustomClickListener {
    RecyclerView recyclerView;
    CalendarView calenderView;
    FloatingActionButton add;
    RecyclerView.LayoutManager layoutManager;
    Adapter adapter;
    NotesDatabase notesDatabase;
    TextView emptyEvents;
    LinearLayout mainLayout;
    Button mEvents;
    Button settings;
    FragmentManager fm;
    Bundle b;
    Long timeFromNotification;
    Note noteFromNotif;
    SharedPreferences prefs;
    AdView mAdView;

    public LinearLayout getMainLayout() {
        return mainLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycle);
        calenderView = findViewById(R.id.calendarView);
        emptyEvents = findViewById(R.id.emptyEvents);
        mainLayout = findViewById(R.id.mainLayout);
        mEvents = findViewById(R.id.mnthEvents);
        settings = findViewById(R.id.settings);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        layoutManager= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        notesDatabase = NotesDatabase.getItemDatabase(this);
        prefs = this.getSharedPreferences(
                "com.a33y.jo.diary", Context.MODE_PRIVATE);
        Note.setDataListener(this);
       // RefreshData(false);
        boolean cameFromNotification=false;
        if (getIntent().getExtras() != null) {
           b= getIntent().getExtras();
            cameFromNotification = b.getBoolean("fromNotification");
        }
        Intent intent = new Intent(this, MemoriesService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        if(!cameFromNotification && !isMyServiceRunning(MemoriesService.class))
        alarm.setInexactRepeating(RTC_WAKEUP,
                SystemClock.elapsedRealtime() ,
                AlarmManager.INTERVAL_DAY, pintent);

        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                AddDialog addDialog = AddDialog.newInstance(calenderView.getFirstSelectedDate().getTime(),AddDialog.TYPE_ADD);
                addDialog.show(fm, "Add_dialog");
                addDialog.setDataListener(MainActivity.this);
            }
        });
        mEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChange();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsFrag.class));
            }
        });
        calenderView.showCurrentMonthPage();
        Calendar c = Calendar.getInstance();
        if(cameFromNotification) {
            noteFromNotif = (Note) b.getSerializable("note");
            c.setTime(noteFromNotif.getDate());
            try {
                calenderView.setDate(c);
            } catch (OutOfDateRangeException e) {
                e.printStackTrace();
            }

        }

        adapter = new Adapter(this,Note.findbyday(calenderView.getFirstSelectedDate().getTime()),this,this);
        if(adapter.getItemCount()==0)
            emptyEvents.setVisibility(View.VISIBLE);
        else
            emptyEvents.setVisibility(View.GONE);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        calenderView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                Calendar clickedDayCalendar = eventDay.getCalendar();
                adapter.notes =  Note.findbyday(clickedDayCalendar.getTime());
                adapter.notifyDataSetChanged();
                if(adapter.getItemCount()==0)
                    emptyEvents.setVisibility(View.VISIBLE);
                else
                    emptyEvents.setVisibility(View.GONE);
            }
        });

        calenderView.setOnForwardPageChangeListener(this);
        calenderView.setOnPreviousPageChangeListener(this);
        AddEvent(Note.notes);
        if(cameFromNotification)
            OnClick(new View(this),noteFromNotif);

        if(prefs.getBoolean("firstTime",true)){
           addAutoStartup();
           prefs.edit().putBoolean("firstTime",false).apply();
        }
    }

    private void addAutoStartup() {

        try {
           final Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Ask for permission");
                dialog.setMessage("Please enable Permissions so that app notification can work probably without issues!");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(intent);
                    }
                });
AlertDialog alert = dialog.create();
alert.setCancelable(false);
alert.show();
            }
        } catch (Exception e) {
            Log.e("exc" , String.valueOf(e));
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void AddEvent(List<Note> notes){
        List<EventDay> events = new ArrayList<>();
        for(Note n :Note.getNotes(this)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(n.getDate());
            events.add(new EventDay(calendar, R.drawable.sample_icon_1));
        }
            calenderView.setEvents(events);

    }
   public void RefreshData(final boolean update){

       new Thread(new Runnable() {
           @Override
           public void run() {
               if(!update)
                   Note.setNotes(notesDatabase.itemDao().getAll(),MainActivity.this);
               else
                   Note.updateNotes(notesDatabase.itemDao().getAll());
           }
       }).start();
   }
    @Override
    public void OnNotesRecieved() {
        adapter.notes =  Note.findbyday(calenderView.getFirstSelectedDate().getTime());
        adapter.notifyDataSetChanged();
        if(adapter.getItemCount()==0)
            emptyEvents.setVisibility(View.VISIBLE);
        else
            emptyEvents.setVisibility(View.GONE);
        AddEvent(Note.notes);
    }

    @Override
    public void OnNoteAdded(Note note) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter.notes.add(note);
        Note.getNotes(this).add(note);
        adapter.notifyItemInserted(adapter.getItemCount()-1);
        recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
        emptyEvents.setVisibility(View.GONE);
        RefreshData(false);
        Snackbar snackbar = Snackbar.make(mainLayout,"Note Added Successfully",Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
        AddEvent(Note.notes);
    }

    @Override
    public void OnNoteDeleted(Note note) {
        int pos = adapter.notes.indexOf(note);
        adapter.notes.remove(note);
        Note.notes.remove(note);
        adapter.notifyItemRemoved(pos);
        //recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
        if(adapter.notes.size()==0)
        emptyEvents.setVisibility(View.VISIBLE);
        Snackbar snackbar = Snackbar.make(mainLayout,"Note Deleted Successfully",Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
        //RefreshData(true);
        AddEvent(Note.notes);
    }

    @Override
    public void OnNoteUpdated(Note note) {
        adapter.notifyItemChanged(adapter.notes.indexOf(note));
    }



    @Override
    public void onChange() {
        calenderView.clear();
        adapter.notes =  Note.findbymonth(calenderView.getCurrentPageDate().getTime());
        adapter.notifyDataSetChanged();
        if(adapter.getItemCount()==0)
            emptyEvents.setVisibility(View.VISIBLE);
        else
            emptyEvents.setVisibility(View.GONE);
    }

    @Override
    public void OnClick(View view,Note note) {
        if(note.isSecured()) {
            fm = getSupportFragmentManager();
            PassDialog passDialog = PassDialog.newInstance(note);
            passDialog.show(fm, "Pass_dialog");
        }else {
            fm = getSupportFragmentManager();
            NoteFrag noteFrag = NoteFrag.newInstance(note);
            noteFrag.show(fm, "Note_dialog");
            noteFrag.setDataListener(this);
        }
    }
}
