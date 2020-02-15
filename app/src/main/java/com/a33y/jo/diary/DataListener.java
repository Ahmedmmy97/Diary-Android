package com.a33y.jo.diary;

/**
 * Created by ahmed on 24/8/2018.
 */

public  interface DataListener{
    void OnNotesRecieved();
    void OnNoteAdded(Note note);
    void OnNoteDeleted(Note note);
    void OnNoteUpdated(Note note);

}