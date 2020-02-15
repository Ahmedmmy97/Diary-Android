package com.a33y.jo.diary;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteFrag extends DialogFragment implements DataListener {

    Note note;
    TextView date;
    TextView location;
    TextView title;
    TextView details;
    ImageButton edit;
    DataListener dataListener;

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    public NoteFrag() {
        // Required empty public constructor
    }
    public static NoteFrag newInstance(Note note){
        NoteFrag frag = new NoteFrag();
        Bundle args = new Bundle();
        args.putSerializable("note", note);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.Dialog_Animation_full;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.from(getContext()).inflate(R.layout.note_frag,container,false);
        note = note = (Note)getArguments().getSerializable("note");
        date = v.findViewById(R.id.date);
        location = v.findViewById(R.id.location);
        details = v.findViewById(R.id.subject);
        title = v.findViewById(R.id.title);
        edit = v.findViewById(R.id.edit);
        assign_values();
        return v;
    }
    private void assign_values(){
        date.setText(new SimpleDateFormat("dd/MM/yyyy").format(note.getDate()));
        location.setText(note.getLocation());
        title.setText(note.getTitle());
        details.setText(note.getSubject());
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                AddDialog addDialog = AddDialog.newInstance(note,AddDialog.TYPE_EDIT);
                addDialog.show(fm, "Add_dialog");
                addDialog.setDataListener(NoteFrag.this);

            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorText)));
        }
    }

    @Override
    public void OnNotesRecieved() {

    }

    @Override
    public void OnNoteAdded(Note note) {

    }

    @Override
    public void OnNoteDeleted(Note note) {

    }

    @Override
    public void OnNoteUpdated(Note note) {

         assign_values();
        Snackbar snackbar = Snackbar.make(getView(),"Note Updated Successfully",Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
         if(dataListener!=null)
             dataListener.OnNoteUpdated(note);
    }
}
