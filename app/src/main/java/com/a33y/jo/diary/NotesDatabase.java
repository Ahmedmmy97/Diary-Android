package com.a33y.jo.diary;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Created by ahmed on 12/8/2018.
 */
@Database(entities = {Note.class}, version = 1,exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract  class NotesDatabase extends RoomDatabase {
    public abstract NotesDao itemDao();
    private static NotesDatabase instance;

    public static NotesDatabase getItemDatabase(Context c){
        if (instance == null) {
            instance =
                    Room.databaseBuilder(c.getApplicationContext(), NotesDatabase.class, "notes-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .build();
        }
        return instance;
    }
    public static void destroyInstance() {
        instance = null;
    }
}
