package com.a33y.jo.diary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsFrag extends AppCompatPreferenceActivity  {
    private static final String TAG = SettingsFrag.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Settings");

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();


    }
    public static class MainPreferenceFragment extends PreferenceFragment {
        SharedPreferences prefs;
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            prefs = getActivity().getSharedPreferences(
                    "com.a33y.jo.diary", Context.MODE_PRIVATE);
            // gallery EditText change listener


            // notification preference change listener
            final SwitchPreference notificationpref = (SwitchPreference)findPreference("notification");
            if(prefs.getBoolean("notify",true))
                notificationpref.setChecked(true);
            else
                notificationpref.setChecked(false);
            notificationpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                 @Override
                 public boolean onPreferenceChange(Preference preference, Object o) {
                     if(notificationpref.isChecked()) {
                         prefs.edit().putBoolean("notify",false).apply();
                     }else {
                         prefs.edit().putBoolean("notify",true).apply();
                     }
                     Log.i("service","CheckService - "+String.valueOf(prefs.getBoolean("notify",true)));
                 return true;
                 }
             });
            // feedback preference click listener
            Preference myPref = findPreference("clr");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),R.style.Theme_AlertDialog);
                   // dialog.setTitle("Are You sure ?");
                    dialog.setMessage("Do you want to delete all saved notes?");
                    dialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           final NotesDatabase notesDatabase = NotesDatabase.getItemDatabase(getActivity());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    notesDatabase.itemDao().deleteAll();
                                    Note.setNotes(notesDatabase.itemDao().getAll(),getActivity());
                                    Intent intent = new Intent(getActivity(),MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }).start();


                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    TextView title = new TextView(getActivity());
                    title.setText("Are You sure ?");
// You Can Customise your Title here
                    title.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    title.setPadding(10, 10, 10, 10);
                    title.setGravity(Gravity.CENTER);
                    title.setTextColor(Color.WHITE);
                    title.setTextSize(20);

                    dialog.setCustomTitle(title);
                    AlertDialog alert = dialog.create();

                    alert.show();

                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
