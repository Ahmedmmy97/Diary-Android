package com.a33y.jo.diary;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ahmed on 24/8/2018.
 */

public class AddDialog extends DialogFragment {
    EditText title;
    TextView date;
    Date Selected_date;
    EditText location;
    EditText subject;
    CheckBox secured;
    EditText pass;
    LinearLayout passlayout;
    ImageButton add;
    ImageButton cancel;
    Note note;
    NotesDatabase notesDatabase;
    DataListener dataListener;
    public  static final String TYPE_EDIT = "Edit";
    public   static final String TYPE_ADD = "Add";
    String type;
    public void setNote(Note note) {
        this.note = note;
    }

    public DataListener getDataListener() {
        return dataListener;
    }

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    public static AddDialog newInstance(Date date,String type) {
        AddDialog frag = new AddDialog();
        Bundle args = new Bundle();
        args.putLong("date", date.getTime());
        args.putString("type", type);
        frag.setArguments(args);
        return frag;
    }
    public static AddDialog newInstance(Note note,String type) {
        AddDialog frag = new AddDialog();
        Bundle args = new Bundle();
        args.putSerializable("note", note);
        args.putString("type", type);
        frag.setArguments(args);
        return frag;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.add_dialog, null);
        alertDialogBuilder.setView(view);
        type= getArguments().getString("type");
        note = (Note) getArguments().getSerializable("note");
        Selected_date = new Date(getArguments().getLong("date"));

        getViews(view);
        assign_values(type);
        TextView title = new TextView(getContext());
// You Can Customise your Title here

        if(note==null)
            title.setText("Add Note");
        else
            title.setText("Edit Note");
        title.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        alertDialogBuilder.setCustomTitle(title);
        return alertDialogBuilder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.Dialog_Animation;

    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorText)));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_dialog,container,false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view

    }
    private  void getViews(View v)
    {


        // Create your application here
        notesDatabase = NotesDatabase.getItemDatabase(getContext());
        title = v.findViewById(R.id.title);
        location = v.findViewById(R.id.location);
        subject = v.findViewById(R.id.subject);
        pass = v.findViewById(R.id.pass);
        date = v.findViewById(R.id.date);
        secured = v.findViewById(R.id.securedChk);
        passlayout = v.findViewById(R.id.PassLayout);
        passlayout.setVisibility(View.GONE);
        add = v.findViewById(R.id.add);
        cancel = v.findViewById(R.id.cancel);

    }
    private void assign_values(String type){

        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        secured.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    passlayout.setVisibility(View.VISIBLE);
                else
                    passlayout.setVisibility(View.GONE);
            }
        });
        switch (type){
            case TYPE_EDIT:
                date.setText(simpleDateFormat.format(note.getDate()));
                location.setText(note.getLocation());
                title.setText(note.getTitle());
                subject.setText(note.getSubject());
                secured.setChecked(note.isSecured());
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UpdateNote();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                notesDatabase.itemDao().update(note);
                            }
                        }).start();


                        dismiss();
                        if(dataListener!=null)
                            dataListener.OnNoteUpdated(note);
                    }
                });
                break;
            case TYPE_ADD:
                date.setText(simpleDateFormat.format(Selected_date));
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Note note = CreateNote();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                notesDatabase.itemDao().insert(note);
                            }
                        }).start();


                        dismiss();
                        if(dataListener!=null)
                            dataListener.OnNoteAdded(note);
                    }
                });
        }




        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
    private Note CreateNote()
    {
        //string id = Guid.NewGuid().ToString("N");
        String Title = title.getText().toString();
        Date Date = Selected_date;
        String Subject = subject.getText().toString();
        String Password = pass.getText().toString();
        boolean isSecured = secured.isChecked();
        String Location = location.getText().toString();
        return new Note(Title,Date,Subject,Password,isSecured,Location);
    }
    private void UpdateNote()
    {
        //string id = Guid.NewGuid().ToString("N");
        String Title = title.getText().toString();
        String Subject = subject.getText().toString();
        String Password = pass.getText().toString();
        boolean isSecured = secured.isChecked();
        String Location = location.getText().toString();
        note.setTitle(Title);
        note.setSubject(Subject);
        note.setPassword(Password);
        note.setSecured(isSecured);
        note.setLocation(Location);
    }
}
