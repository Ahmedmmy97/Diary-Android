package com.a33y.jo.diary;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by ahmed on 12/8/2018.
 */

@Dao
public interface NotesDao {
    @Query("SELECT * FROM Notes")
    List<Note> getAll();

    @Query("SELECT * FROM Notes WHERE id IN (:noteIds)")
    List<Note> loadAllByIds(int[] noteIds);

    @Query("SELECT * FROM Notes WHERE date LIKE :date")
    List<Note> findbyday(Date date);

    @Query("SELECT * FROM Notes WHERE strftime('%m', date) =:month AND strftime('%Y', date) =:year")
    List<Note> findByMonth(int month,int year);

    @Query("DELETE FROM Notes")
    void deleteAll();

    @Update
    void update(Note note);

    @Insert
    void insertAll(List<Note> notes);

    @Insert
    void insert(Note note);

    @Delete
    void delete(Note note);
}