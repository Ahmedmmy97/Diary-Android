package com.a33y.jo.diary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ahmed on 23/8/2018.
 */

public class Adapter extends RecyclerView.Adapter<ViewHolder> implements DataListener,CustomClickListener {
    Context c;
    List<Note> notes;
    DataListener dataListener;
    CustomClickListener onClickListener;

    public Adapter(Context c, List<Note> notes, DataListener dataListener, CustomClickListener onClickListener) {
        this.c = c;
        this.notes = notes;
        this.dataListener = dataListener;
        this.onClickListener= onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item,parent,false);
        return new ViewHolder(v,this,c,this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
             holder.Assign_values(notes.get(position));

    }

    @Override
    public int getItemCount() {
        return notes!=null?notes.size():0;
    }

    @Override
    public void OnNotesRecieved() {

    }

    @Override
    public void OnNoteAdded(Note note) {

    }

    @Override
    public void OnNoteDeleted(Note note) {
          if(dataListener!=null)
              dataListener.OnNoteDeleted(note);
    }

    @Override
    public void OnNoteUpdated(Note note) {

    }

    @Override
    public void OnClick(View view , Note note) {
        if(onClickListener!=null)
            onClickListener.OnClick(view,note);
    }
}
