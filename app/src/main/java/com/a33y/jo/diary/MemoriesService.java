package com.a33y.jo.diary;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.applandeo.materialcalendarview.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ahmed on 31/8/2018.
 */

public class MemoriesService extends Service {

    private static int NOTIFICATION_ID = 100;
    SharedPreferences prefs;


    public MemoriesService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
         prefs = this.getSharedPreferences(
                "com.a33y.jo.diary", Context.MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                List<Note> notes = NotesDatabase.getItemDatabase(getApplicationContext()).itemDao().getAll();

                for(Note n : notes){
                    Calendar noteCal = Calendar.getInstance();
                    noteCal.setTime(n.getDate());
                    if(noteCal.get(Calendar.DAY_OF_MONTH)==calendar.get(Calendar.DAY_OF_MONTH)
                            && noteCal.get(Calendar.MONTH)==calendar.get(Calendar.MONTH)&&noteCal.get(Calendar.YEAR)<calendar.get(Calendar.YEAR)){
                        long time = prefs.getLong(String.valueOf(n.getId()),0);
                        Calendar calendar1 = Calendar.getInstance();
                        calendar.setTime(new Date(time));
                        prefs = getApplicationContext().getSharedPreferences(
                                "com.a33y.jo.diary", Context.MODE_MULTI_PROCESS);
                        Log.i("service","CheckService - "+String.valueOf(prefs.getBoolean("notify",true)));
                        if(time==0 || !(calendar1.get(Calendar.DAY_OF_MONTH)==calendar.get(Calendar.DAY_OF_MONTH)
                                && calendar1.get(Calendar.MONTH)==calendar.get(Calendar.MONTH)&&calendar1.get(Calendar.YEAR)==calendar.get(Calendar.YEAR)))
                        if(prefs.getBoolean("notify",true)) {
                            send_notification(n);
                        }
                    }
                }
            }
        }).start();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void send_notification(Note note)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fromNotification", true);
        intent.putExtra("note",note);
        Calendar current = Calendar.getInstance();
        Calendar noteCal = Calendar.getInstance();
        noteCal.setTime(note.getDate());
        int diff = current.get(Calendar.YEAR)-noteCal.get(Calendar.YEAR);
        Notification.Builder nBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.sample_icon_1)
                .setContentTitle(note.getTitle())
                .setContentText(note.isSecured()?"You have a Secured Memory From "+diff+ (diff>1?" Years":" Year")
                        :diff+(diff>1?" Years":"Year")+" Memory.")
                .setStyle(new Notification.BigTextStyle().bigText(note.isSecured()?"You have a Secured Memory From "+diff+ (diff>1?" Years":" Year")
                        :diff+(diff>1?" Years":"Year")+" Memory."));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //stackBuilder.AddParentStack();
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(note.getId(), PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notifmanager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        nBuilder.setDefaults(Notification.DEFAULT_SOUND);
        Uri customSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.twirl);
        nBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        //nBuilder.setSound(customSoundUri);
        nBuilder.setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = String.valueOf(note.getId());
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Reminder Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build();
            channel.enableLights(true);
            //channel.setLightColor(Color.RED);
            //channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel.enableVibration(true);

            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),audioAttributes);
            notifmanager.createNotificationChannel(channel);
            nBuilder.setChannelId(channelId);
        }
        notifmanager.notify(note.getId(), nBuilder.build());
        Calendar calendar = Calendar.getInstance();
        prefs.edit().putLong(String.valueOf(note.getId()), calendar.getTimeInMillis()).apply();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
