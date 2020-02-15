package com.a33y.jo.diary;

import android.app.Activity;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ahmed on 23/8/2018.
 */
@Entity(tableName = "Notes")
public class Note extends Object implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "title")
    private String title ;
    @ColumnInfo(name = "date")
    private Date date ;
    @ColumnInfo(name = "subject")
    private String subject;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "isSecured")
    private boolean isSecured;
    @ColumnInfo(name = "location")
    private String location;
    public static DataListener dataListener;
    public static List<Note> notes = new ArrayList<>();

    public static DataListener getDataListener() {
        return dataListener;
    }

    public static void setDataListener(DataListener dataListener) {
        Note.dataListener = dataListener;
    }

    public Note()
    {

    }
    @Ignore
    public Note( String title, Date date, String subject, String password, Boolean isSecured, String location) {
        this.title = title;
        this.date = date;
        this.subject = subject;
        this.password = password;
        this.isSecured = isSecured;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSecured() {
        return isSecured;
    }

    public void setSecured(boolean secured) {
        isSecured = secured;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public static List<Note> getNotes(Context c) {
        return notes;
    }

    public static void setNotes(List<Note> notes,Activity activity) {
        Note.notes = notes;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(dataListener!=null)
                    dataListener.OnNotesRecieved();
            }
        });

    }
    public static void updateNotes(List<Note> notes) {
        Note.notes = notes;
    }
    public static void deleteAll(){

    }
    public static List<Note> findbyday(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        List<Note> notesByDay = new ArrayList<>();
             for(Note n : notes){
                 if(sdf.format(n.getDate()).equals(sdf.format(date))){
                     notesByDay.add(n);
                 }
             }
             return notesByDay;
    }
    public static List<Note> findbymonth(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        List<Note> notesByMonth = new ArrayList<>();
        for(Note n : notes){
            if(sdf.format(n.getDate()).equals(sdf.format(date))){
                notesByMonth.add(n);
            }
        }

        return notesByMonth;
    }

}
