package com.snorax;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private TextView tvTimeSaved;
    private Button btnShare;
    private SharedPreferences analyticsPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        // Initialize Views
        pieChart = findViewById(R.id.pieChart);
        tvTimeSaved = findViewById(R.id.tvTimeSaved);
        btnShare = findViewById(R.id.btnShare);

        // Initialize SharedPreferences
        analyticsPreferences = getSharedPreferences("analytics_prefs", MODE_PRIVATE);

        // Load and display analytics data
        loadAnalyticsData();

        // Set up the Share button
        btnShare.setOnClickListener(v -> shareAnalytics());
    }

    private void loadAnalyticsData() {
        // Load analytics data from SharedPreferences
        int muteCount = analyticsPreferences.getInt("mute_count", 0);
        int unmuteCount = analyticsPreferences.getInt("unmute_count", 0);
        int vibrateCount = analyticsPreferences.getInt("vibrate_count", 0);
        long timeSaved = analyticsPreferences.getLong("time_saved", 0);

        // Create AnalyticsData object
        AnalyticsData analyticsData = new AnalyticsData(muteCount, unmuteCount, vibrateCount, timeSaved);

        // Display analytics data
        displayPieChart(analyticsData);
        displayTimeSaved(analyticsData);
    }

    private void displayPieChart(AnalyticsData analyticsData) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(analyticsData.getMuteCount(), "Muted"));
        entries.add(new PieEntry(analyticsData.getUnmuteCount(), "Unmuted"));
        entries.add(new PieEntry(analyticsData.getVibrateCount(), "Vibrate"));

        PieDataSet dataSet = new PieDataSet(entries, "Notification Actions");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Notification Analytics");
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void displayTimeSaved(AnalyticsData analyticsData) {
        long timeSaved = analyticsData.getTimeSaved();
        tvTimeSaved.setText("Time Saved: " + timeSaved + " minutes");
    }

    private void shareAnalytics() {
        // Load analytics data
        int muteCount = analyticsPreferences.getInt("mute_count", 0);
        int unmuteCount = analyticsPreferences.getInt("unmute_count", 0);
        int vibrateCount = analyticsPreferences.getInt("vibrate_count", 0);
        long timeSaved = analyticsPreferences.getLong("time_saved", 0);

        // Create the share message
        String shareMessage = "Check out my notification management stats with SnoraX!\n\n" +
                "Muted Notifications: " + muteCount + "\n" +
                "Unmuted Notifications: " + unmuteCount + "\n" +
                "Vibrate Notifications: " + vibrateCount + "\n" +
                "Total Time Saved: " + timeSaved + " minutes\n\n" +
                "Download SnoraX now to manage your notifications like a pro!";

        // Create the share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        // Start the share activity
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}