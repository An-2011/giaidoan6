package com.example.ungdungcoxuongkhop;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {
    private TextView totalUsers, totalDoctors, totalAppointments;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        totalUsers = findViewById(R.id.total_users);
        totalDoctors = findViewById(R.id.total_doctors);
        totalAppointments = findViewById(R.id.total_appointments);
        barChart = findViewById(R.id.barChart);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        fetchStatistics();
    }

    private void fetchStatistics() {
        String url = "http://172.22.144.1/ungdung_api/getStatistics.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        int users = response.getInt("total_users");
                        int doctors = response.getInt("total_doctors");
                        int appointments = response.getInt("total_appointments");

                        totalUsers.setText("👥 Người dùng: " + users);
                        totalDoctors.setText("🩺 Bác sĩ: " + doctors);
                        totalAppointments.setText("📅 Lịch khám: " + appointments);

                        updateBarChart(users, doctors, appointments);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi phân tích dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show()
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void updateBarChart(int totalUsers, int totalDoctors, int totalAppointments) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, totalUsers));
        entries.add(new BarEntry(1f, totalDoctors));
        entries.add(new BarEntry(2f, totalAppointments));

        BarDataSet dataSet = new BarDataSet(entries, "Số lượng");
        dataSet.setColors(
                ContextCompat.getColor(this, R.color.teal_700),
                ContextCompat.getColor(this, R.color.purple_500),
                ContextCompat.getColor(this, R.color.purple_200)
        );
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(14f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.setDrawValueAboveBar(true);
        barChart.setTouchEnabled(false);
        barChart.getDescription().setEnabled(false);

        // Trục X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Người dùng", "Bác sĩ", "Lịch khám"}));
        xAxis.setTextSize(12f);

        // Trục Y
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(12f);
        barChart.getAxisRight().setEnabled(false);

        // Legend
        Legend legend = barChart.getLegend();
        legend.setTextSize(14f);
        legend.setFormSize(14f);

        barChart.invalidate(); // Vẽ lại
    }
}
