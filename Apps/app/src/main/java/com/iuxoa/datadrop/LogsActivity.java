package com.iuxoa.datadrop;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.iuxoa.datadrop.R;

public class LogsActivity extends AppCompatActivity {

    TextView logTextView;
    LocalLogDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        logTextView = findViewById(R.id.logTextView);
        dbHelper = new LocalLogDbHelper(this);

        displayLogs();
    }

    private void displayLogs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("logs", null, null, null, null, null, "timestamp DESC");

        StringBuilder logBuilder = new StringBuilder();
        while (cursor.moveToNext()) {
            String domain = cursor.getString(cursor.getColumnIndexOrThrow("domain"));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
            logBuilder.append(timestamp).append(" — ").append(domain).append("\n");
        }

        cursor.close();
        logTextView.setText(logBuilder.toString().isEmpty() ? "No logs available." : logBuilder.toString());
    }
}
