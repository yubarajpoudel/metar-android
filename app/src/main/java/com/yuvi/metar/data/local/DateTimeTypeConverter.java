package com.yuvi.metar.data.local;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateTimeTypeConverter {
    public DateTimeTypeConverter() {

    }

    @TypeConverter
    public static Date stringToDate(String date) {
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            return dateFormat.parse(date);
        } catch (Exception e) { e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String dateToString(Date date) {
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            return dateFormat.format(date);
        } catch (Exception e) { e.printStackTrace();
            return null;
        }
    }
 }
