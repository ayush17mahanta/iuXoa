package com.iuxoa.marki.model;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Project.class}, version = 1, exportSchema = false)
public abstract class ProjectDatabase extends RoomDatabase {

    private static ProjectDatabase instance;

    public abstract ProjectDao projectDao();  // Accessor for ProjectDao

    public static synchronized ProjectDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            ProjectDatabase.class, "project_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
