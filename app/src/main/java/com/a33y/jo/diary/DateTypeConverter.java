package com.a33y.jo.diary;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by ahmed on 24/8/2018.
 */

public class DateTypeConverter {

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();
    }
}
