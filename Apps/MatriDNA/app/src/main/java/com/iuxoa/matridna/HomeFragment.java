package com.iuxoa.matridna;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private BarChart barChart;
    private LineChart lineChart;
    private PieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Charts
        barChart = view.findViewById(R.id.barChart);
        lineChart = view.findViewById(R.id.lineChart);
        pieChart = view.findViewById(R.id.pieChart);

        setupBarChart();
        setupLineChart();
        setupPieChart();

        return view;
    }

    private void setupBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 85)); // Health Score
        entries.add(new BarEntry(1, 70)); // Energy Level
        entries.add(new BarEntry(2, 65)); // Immunity
        entries.add(new BarEntry(3, 75)); // Metabolism Rate
        entries.add(new BarEntry(4, 60)); // Hydration Level
        entries.add(new BarEntry(5, 80)); // Heart Health
        entries.add(new BarEntry(6, 55)); // Stress Level

        BarDataSet dataSet = new BarDataSet(entries, "Health Metrics");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setBarBorderWidth(1f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.animateY(1500);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextSize(12f);

        barChart.getAxisRight().setEnabled(false);
        barChart.invalidate();
    }

    private void setupLineChart() {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 28));
        entries.add(new Entry(2, 30));
        entries.add(new Entry(3, 27));
        entries.add(new Entry(4, 29));
        entries.add(new Entry(5, 26));
        entries.add(new Entry(6, 31));

        LineDataSet dataSet = new LineDataSet(entries, "Cycle Length");
        dataSet.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        dataSet.setValueTextSize(14f);
        dataSet.setCircleRadius(6f);
        dataSet.setCircleColor(Color.RED);
        dataSet.setLineWidth(3f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(ColorTemplate.COLORFUL_COLORS[1]);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.animateX(1500);
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }

    private void setupPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(6, "Deep Sleep"));
        entries.add(new PieEntry(2, "Light Sleep"));
        entries.add(new PieEntry(1, "Awake"));

        PieDataSet dataSet = new PieDataSet(entries, "Sleep Stages");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setHoleRadius(45f);
        pieChart.animateY(1500);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setTextSize(12f);
        legend.setFormSize(10f);

        Description description = new Description();
        description.setText("Sleep Pattern");
        description.setTextSize(14f);
        pieChart.setDescription(description);
        pieChart.invalidate();
    }
}
