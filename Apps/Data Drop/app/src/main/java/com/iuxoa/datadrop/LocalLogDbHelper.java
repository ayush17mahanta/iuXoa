package com.iuxoa.datadrop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LocalLogDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "domain_logs.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_LOGS = "logs";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DOMAIN = "domain";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public LocalLogDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGS_TABLE = "CREATE TABLE " + TABLE_LOGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DOMAIN + " TEXT,"
                + COLUMN_TIMESTAMP + " INTEGER" + ")";
        db.execSQL(CREATE_LOGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For now, just drop and recreate the table if database version changes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        onCreate(db);
    }

    // Insert a new domain log entry
    public void insertLog(String domain, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DOMAIN, domain);
        values.put(COLUMN_TIMESTAMP, timestamp);
        db.insert(TABLE_LOGS, null, values);
        db.close();
    }

    // Get all logs as a List of DomainLog objects
    public List<DomainLog> getAllLogs() {
        List<DomainLog> logs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_LOGS + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String domain = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOMAIN));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));

                logs.add(new DomainLog(id, domain, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return logs;
    }

    // Optional: Delete all logs
    public void clearLogs() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGS, null, null);
        db.close();
    }

    // Optional: DomainLog data class for ease of use
    public static class DomainLog {
        public int id;
        public String domain;
        public long timestamp;

        public DomainLog(int id, String domain, long timestamp) {
            this.id = id;
            this.domain = domain;
            this.timestamp = timestamp;
        }
    }
}
