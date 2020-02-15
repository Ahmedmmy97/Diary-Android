package com.a33y.jo.diary;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFrag extends Fragment implements DataListener,OnCalendarPageChangeListener,CustomClickListener{
    RecyclerView recyclerView;
    CalendarView calenderView;
    FloatingActionButton add;
    RecyclerView.LayoutManager layoutManager;
    Adapter adapter;
    NotesDatabase notesDatabase;
    TextView emptyEvents;
    LinearLayout mainLayout;
    Button mEvents;
    Button settings;
    FragmentManager fm;
    public MainFrag() {
        // Required empty public constructor
    }
    public static MainFrag newInstance(){
        return  new MainFrag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.from(getContext()).inflate(R.layout.activity_main,container,false);
        recyclerView = v.findViewById(R.id.recycle);
        calenderView = v.findViewById(R.id.calendarView);
        emptyEvents = v.findViewById(R.id.emptyEvents);
        mainLayout = v.findViewById(R.id.mainLayout);
        mEvents = v.findViewById(R.id.mnthEvents);
        settings = v.findViewById(R.id.settings);
        layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        notesDatabase = NotesDatabase.getItemDatabase(getContext());
        Note.setDataListener(this);
        // RefreshData(false);

        add = v.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                AddDialog addDialog = AddDialog.newInstance(calenderView.getFirstSelectedDate().getTime(),AddDialog.TYPE_ADD);
                addDialog.show(fm, "Add_dialog");
                addDialog.setDataListener(MainFrag.this);
            }
        });
        mEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChange();
            }
        });
        calenderView.showCurrentMonthPage();
        try {
            calenderView.setDate(new Date());
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
        adapter = new Adapter(getContext(),Note.findbyday(calenderView.getFirstSelectedDate().getTime()),this,this);
        if(adapter.getItemCount()==0)
            emptyEvents.setVisibility(View.VISIBLE);
        else
            emptyEvents.setVisibility(View.GONE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        calenderView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                Calendar clickedDayCalendar = eventDay.getCalendar();
                adapter.notes =  Note.findbyday(clickedDayCalendar.getTime());
                adapter.notifyDataSetChanged();
                if(adapter.getItemCount()==0)
                    emptyEvents.setVisibility(View.VISIBLE);
                else
                    emptyEvents.setVisibility(View.GONE);
            }
        });
        calenderView.setOnForwardPageChangeListener(this);
        calenderView.setOnPreviousPageChangeListener(this);
        AddEvent(Note.notes);

        return  v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //calenderView.setVisibility(View.VISIBLE);
        getView().invalidate();
        getView().refreshDrawableState();
    }

    public void AddEvent(List<Note> notes){
        List<EventDay> events = new ArrayList<>();
        for(Note n :Note.getNotes(getContext())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(n.getDate());
            events.add(new EventDay(calendar, R.drawable.sample_icon_1));
        }
        calenderView.setEvents(events);

    }
    public void RefreshData(final boolean update){

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!update)
                    Note.setNotes(notesDatabase.itemDao().getAll(),getActivity());
                else
                    Note.updateNotes(notesDatabase.itemDao().getAll());
            }
        }).start();
    }
    @Override
    public void OnNotesRecieved(){
        adapter.notes =  Note.findbyday(calenderView.getFirstSelectedDate().getTime());
        adapter.notifyDataSetChanged();
        if(adapter.getItemCount()==0)
            emptyEvents.setVisibility(View.VISIBLE);
        else
            emptyEvents.setVisibility(View.GONE);
        AddEvent(Note.notes);

     }

    @Override
    public void OnNoteAdded(Note note) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter.notes.add(note);
        Note.getNotes(getContext()).add(note);
        adapter.notifyItemInserted(adapter.getItemCount()-1);
        recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
        emptyEvents.setVisibility(View.GONE);
        RefreshData(false);
        Snackbar snackbar = Snackbar.make(mainLayout,"Note Added Successfully",Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
        AddEvent(Note.notes);
    }

    @Override
    public void OnNoteDeleted(Note note) {
        int pos = adapter.notes.indexOf(note);
        adapter.notes.remove(note);
        Note.notes.remove(note);
        adapter.notifyItemRemoved(pos);
        //recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
        if(adapter.notes.size()==0)
            emptyEvents.setVisibility(View.VISIBLE);
        Snackbar snackbar = Snackbar.make(mainLayout,"Note Deleted Successfully",Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
        //RefreshData(true);
        AddEvent(Note.notes);
    }

    @Override
    public void OnNoteUpdated(Note note) {

    }

    @Override
    public void onChange() {
        calenderView.clear();
        adapter.notes =  Note.findbymonth(calenderView.getCurrentPageDate().getTime());
        adapter.notifyDataSetChanged();
        if(adapter.getItemCount()==0)
            emptyEvents.setVisibility(View.VISIBLE);
        else
            emptyEvents.setVisibility(View.GONE);
    }


    @Override
    public void OnClick(View v, Note note) {
        if(note.isSecured()) {
            fm = getFragmentManager();
            PassDialog passDialog = PassDialog.newInstance(note);
            passDialog.show(fm, "Pass_dialog");
        }else {
            fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.animator.enter_anim_right,R.animator.exit_anim_left,R.animator.enter_anim_left,R.animator.exit_anim_right);
            ft.replace(R.id.frag,NoteFrag.newInstance(note),"Note").commit();
        }
    }
}
