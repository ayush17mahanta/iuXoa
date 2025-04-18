package com.iuxoa.aerocommand;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class GraphDialogFragment extends DialogFragment {

    private LineChart chartTemp, chartBattery, chartHumidity, chartPressure, chartAltitude, chartWind;
    private LineDataSet dsTemp, dsBattery, dsHumidity, dsPressure, dsAltitude, dsWind;
    private LineData ldTemp, ldBattery, ldHumidity, ldPressure, ldAltitude, ldWind;
    private int dataIndex = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        View view = inflater.inflate(R.layout.dialog_graphs, container, false);

        initCharts(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private void initCharts(View view) {
        chartTemp = view.findViewById(R.id.chartTemp);
        chartBattery = view.findViewById(R.id.chartBattery);
        chartHumidity = view.findViewById(R.id.chartHumidity);
        chartPressure = view.findViewById(R.id.chartPressure);
        chartAltitude = view.findViewById(R.id.chartAltitude);
        chartWind = view.findViewById(R.id.chartWind);

        dsTemp = createDataSet("Temperature (°C)", Color.RED);
        dsBattery = createDataSet("Battery (V)", Color.GREEN);
        dsHumidity = createDataSet("Humidity (%)", Color.BLUE);
        dsPressure = createDataSet("Pressure (hPa)", Color.MAGENTA);
        dsAltitude = createDataSet("Altitude (m)", Color.CYAN);
        dsWind = createDataSet("Wind Speed (m/s)", Color.DKGRAY);

        ldTemp = new LineData(dsTemp);
        ldBattery = new LineData(dsBattery);
        ldHumidity = new LineData(dsHumidity);
        ldPressure = new LineData(dsPressure);
        ldAltitude = new LineData(dsAltitude);
        ldWind = new LineData(dsWind);

        configureChart(chartTemp, ldTemp);
        configureChart(chartBattery, ldBattery);
        configureChart(chartHumidity, ldHumidity);
        configureChart(chartPressure, ldPressure);
        configureChart(chartAltitude, ldAltitude);
        configureChart(chartWind, ldWind);
    }

    private LineDataSet createDataSet(String label, int color) {
        LineDataSet ds = new LineDataSet(new ArrayList<>(), label);
        ds.setColor(color);
        ds.setLineWidth(2f);
        ds.setDrawCircles(false);
        ds.setDrawValues(false);
        ds.setMode(LineDataSet.Mode.LINEAR);
        return ds;
    }

    private void configureChart(LineChart chart, LineData data) {
        chart.setData(data);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setVisibleXRangeMaximum(50);
        chart.setNoDataText("Waiting for data...");

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setTextSize(10f);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setTextColor(Color.DKGRAY);
        yAxisLeft.setTextSize(10f);
        yAxisLeft.setDrawGridLines(true);
        yAxisLeft.setGridColor(Color.LTGRAY);

        chart.getAxisRight().setEnabled(false);

        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.DKGRAY);
        legend.setTextSize(12f);

        chart.invalidate();
    }

    public void updateChartData(float temp, float battery, float humidity, float pressure, float altitude, float windSpeed) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                dsTemp.addEntry(new Entry(dataIndex, temp));
                dsBattery.addEntry(new Entry(dataIndex, battery));
                dsHumidity.addEntry(new Entry(dataIndex, humidity));
                dsPressure.addEntry(new Entry(dataIndex, pressure));
                dsAltitude.addEntry(new Entry(dataIndex, altitude));
                dsWind.addEntry(new Entry(dataIndex, windSpeed));

                ldTemp.notifyDataChanged();
                ldBattery.notifyDataChanged();
                ldHumidity.notifyDataChanged();
                ldPressure.notifyDataChanged();
                ldAltitude.notifyDataChanged();
                ldWind.notifyDataChanged();

                chartTemp.notifyDataSetChanged();
                chartBattery.notifyDataSetChanged();
                chartHumidity.notifyDataSetChanged();
                chartPressure.notifyDataSetChanged();
                chartAltitude.notifyDataSetChanged();
                chartWind.notifyDataSetChanged();

                chartTemp.moveViewToX(dataIndex);
                chartBattery.moveViewToX(dataIndex);
                chartHumidity.moveViewToX(dataIndex);
                chartPressure.moveViewToX(dataIndex);
                chartAltitude.moveViewToX(dataIndex);
                chartWind.moveViewToX(dataIndex);

                dataIndex++;
            });
        }
    }
}
