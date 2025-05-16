package com.iuxoa.datadrop;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {BrowsingEntry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BrowsingDao browsingDao();

    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "datadrop_db").build();
        }
        return INSTANCE;
    }
}
