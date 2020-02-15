package com.a33y.jo.diary;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class SplashActivity extends Activity implements DataListener
{
    NotesDatabase notesDatabase;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        notesDatabase = NotesDatabase.getItemDatabase(this);
        Note.setDataListener(this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                    Note.setNotes(notesDatabase.itemDao().getAll(),SplashActivity.this);

            }
        }).start();

    }






    @Override
    public void OnNotesRecieved() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }

    @Override
    public void OnNoteAdded(Note note) {

    }

    @Override
    public void OnNoteDeleted(Note note) {

    }

    @Override
    public void OnNoteUpdated(Note note) {

    }
}

