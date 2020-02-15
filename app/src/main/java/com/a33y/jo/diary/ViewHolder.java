package com.a33y.jo.diary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by ahmed on 23/8/2018.
 */

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
    TextView day;
    TextView dayWeek;
    TextView title;
    TextView location;
    ImageView secured;
    boolean longPressed;
    Drawable itemColor;
    LinearLayout layout;
    Note note;
    DataListener dataListener;
    Context c;
    CustomClickListener onClickListener;
    FragmentManager fm;

    public ViewHolder(View itemView, DataListener dataListener, Context c, CustomClickListener onClickListener) {
        super(itemView);
        Bind_Views(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        this.dataListener=dataListener;
        this.c=c;
        this.onClickListener= onClickListener;
    }


    private void Bind_Views(View view){
        day = view.findViewById(R.id.day);
        dayWeek = view.findViewById(R.id.dayWeek);
        title = view.findViewById(R.id.title);
        location = view.findViewById(R.id.location);
        secured = view.findViewById(R.id.secured);
        layout = view.findViewById(R.id.linearLayout1);
    }
    public void Assign_values(Note note){
        title.setText(note.getTitle());
        location.setText(note.getLocation());
        secured.setImageResource(R.drawable.ic_lock_black_24dp);
        //secured.setOnClickListener(null);
        if (note.isSecured())
            secured.setVisibility(View.VISIBLE);
        else
            secured.setVisibility(View.GONE);
        day.setText(new SimpleDateFormat("dd").format(note.getDate()));
        dayWeek.setText(new SimpleDateFormat("EEE").format(note.getDate()));

        longPressed = false;
        itemColor = itemView.getBackground();
        this.note = note;
        if(!longPressed)
        layout.setBackgroundColor(c.getResources().getColor(R.color.color1));
    }

    @Override
    public void onClick(View view) {
    if(longPressed){
        layout.setBackgroundColor(c.getResources().getColor(R.color.color1));
        secured.setImageResource(R.drawable.ic_lock_black_24dp);
        secured.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View parent = (View) view.getParent().getParent().getParent();
                parent.performClick();
            }
        });
        if (note.isSecured())
            secured.setVisibility(View.VISIBLE);
        else
            secured.setVisibility(View.GONE);
        longPressed = !longPressed;
        return;
    }
    if(onClickListener!=null)
        onClickListener.OnClick(view,note);
   /* if(note.isSecured())
         fm = ((AppCompatActivity)c).getSupportFragmentManager();
        PassDialog passDialog = PassDialog.newInstance(note);
        passDialog.show(fm, "Pass_dialog");*/


    }

    @Override
    public boolean onLongClick(View view) {
        if(!longPressed) {
            layout.setBackgroundColor(c.getResources().getColor(R.color.colorDelete));
            secured.setImageResource(R.drawable.ic_delete_black_24dp);
            secured.setClickable(true);
            secured.setFocusable(true);
            secured.setVisibility(View.VISIBLE);
            secured.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                  new Thread(new Runnable() {
                      @Override
                      public void run() {
                          NotesDatabase.getItemDatabase(view.getContext()).itemDao().delete(note);
                          ((Activity)view.getContext()).runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  if(dataListener!=null)
                                      dataListener.OnNoteDeleted(note);
                              }
                          });
                      }
                  }).start();
                }
            });
        }else {
            layout.setBackgroundColor(c.getResources().getColor(R.color.color1));
            secured.setImageResource(R.drawable.ic_lock_black_24dp);
            secured.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View parent = (View) view.getParent().getParent().getParent();
                    parent.performClick();
                }
            });
            if (note.isSecured())
                secured.setVisibility(View.VISIBLE);
            else
                secured.setVisibility(View.GONE);
        }
        longPressed = !longPressed;
        return true;
    }
}
