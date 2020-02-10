package com.yuvi.metar.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.yuvi.metar.data.Metar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Metar.class}, version = 1, exportSchema = false)
@TypeConverters({ DateTimeTypeConverter.class })
public abstract class MetarDatabase extends RoomDatabase {

    public abstract MetarDao metarDao();

    private static volatile MetarDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MetarDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MetarDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MetarDatabase.class, "metar_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
