package com.a33y.jo.diary;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ahmed on 27/8/2018.
 */

public class PassDialog extends DialogFragment {
    ImageButton ok;
    ImageButton cancel;
    Note note;
    EditText pass;
    public static PassDialog newInstance(Note note) {
        PassDialog frag = new PassDialog();
        Bundle args = new Bundle();
        args.putSerializable("note", note);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pass_dialog, null);
        alertDialogBuilder.setView(view);
        note = (Note)getArguments().getSerializable("note");
        getViews(view);
        TextView title = new TextView(getContext());
        title.setText("Enter Password");
// You Can Customise your Title here
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pass_dialog,container,false);
    }

    private  void getViews(View v)
    {


        // Create your application here
        pass = v.findViewById(R.id.pass);
        ok = v.findViewById(R.id.ok);
        cancel = v.findViewById(R.id.cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pass.getText().toString().equals(note.getPassword())) {
                    dismiss();
                    FragmentManager fm = getFragmentManager();
                    NoteFrag noteFrag = NoteFrag.newInstance(note);
                    noteFrag.show(fm, "Note_dialog");
                }
                else {
                    Toast.makeText(getContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
