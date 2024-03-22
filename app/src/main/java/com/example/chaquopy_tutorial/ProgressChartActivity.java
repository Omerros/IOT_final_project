package com.example.chaquopy_tutorial;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProgressChartActivity extends AppCompatActivity {

    private BarChart barChart;
    private ImageView profileImage;
    private TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_chart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        barChart = findViewById(R.id.barChart);

        String imageUri = getIntent().getStringExtra("profileImage");
        String name = getIntent().getStringExtra("profileName");

        Glide.with(this).load(imageUri).into(profileImage);
        profileName.setText(name);

        Map<String, Integer> stepsData = new HashMap<>();
        stepsData.put("16/03/2024", 8000);
        stepsData.put("17/03/2024", 12000);
        stepsData.put("18/03/2024", 15000);
        stepsData.put("19/03/2024", 9000);
        stepsData.put("20/03/2024", 11000);
        stepsData.put("21/03/2024", 5000);
        stepsData.put("22/03/2024", 13000);

        setupBarChart(stepsData);
    }

    private void setupBarChart(Map<String, Integer> stepsData) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int todayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_WEEK, i - todayIndex);
            String date = String.format(Locale.getDefault(), "%02d/%02d/%d",
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR));

            int steps = stepsData.getOrDefault(date, 0);
            entries.add(new BarEntry(i, steps));
            xAxisLabels.add(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            calendar.setTimeInMillis(System.currentTimeMillis());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Steps");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(10f);
        BarData barData = new BarData(dataSet);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setDrawGridLines(false);

        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setDrawGridLines(true);

        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private String getDayOfWeek(int day) {
        switch (day) {
            case Calendar.SUNDAY:
                return "Sun";
            case Calendar.MONDAY:
                return "Mon";
            case Calendar.TUESDAY:
                return "Tue";
            case Calendar.WEDNESDAY:
                return "Wed";
            case Calendar.THURSDAY:
                return "Thu";
            case Calendar.FRIDAY:
                return "Fri";
            case Calendar.SATURDAY:
                return "Sat";
            default:
                return "";
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity when the back button is pressed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
