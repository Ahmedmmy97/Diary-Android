package com.a33y.jo.diary;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class Main extends AppCompatActivity {
    FrameLayout frag;
    MainFrag mainFrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        frag= findViewById(R.id.frag);
        mainFrag = MainFrag.newInstance();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.frag,mainFrag, "Main").commit();

    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag("Note");
        if(fragment!=null && fragment.isVisible()) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.animator.enter_anim_left,R.animator.exit_anim_right,R.animator.enter_anim_right,R.animator.exit_anim_left);
            ft.replace(R.id.frag,mainFrag, "Main").commit();

        }else {
            super.onBackPressed();
        }
    }
}
