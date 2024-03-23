package com.example.chaquopy_tutorial;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class ProgressChartActivity extends AppCompatActivity {

    private BarChart barChart;
    private ImageView profileImage;
    private TextView profileName;
    private int targetSteps = 10000; // Example target steps
    private Map<String, Integer> stepsData = new TreeMap<>(); // Use TreeMap instead of HashMap

    private int currentWeek = 1; // Start with the current week as week 1
    private Map<Integer, List<Integer>> weeklyData = new TreeMap<>(); // Use TreeMap instead of HashMap
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_chart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        barChart = findViewById(R.id.barChart);

        ImageButton btnPrev = findViewById(R.id.btnPrev);
        ImageButton btnNext = findViewById(R.id.btnNext);
        TextView tvCurrentWeek = findViewById(R.id.tvCurrentTimeFrame);

        // Populate stepsData with your data
        populateStepsData();

        // Separate the stepsData into weekly data
        try {
            separateWeeklyData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Update the chart with the current week's data
        updateChart(tvCurrentWeek);

        btnPrev.setOnClickListener(view -> {
            if (currentWeek < weeklyData.size()) {
                currentWeek++;
                updateChart(tvCurrentWeek);
            }
        });

        btnNext.setOnClickListener(view -> {
            if (currentWeek > 1) {
                currentWeek--;
                updateChart(tvCurrentWeek);
            }
        });
    }

    private void updateChart(TextView tvCurrentWeek) {
        try {
            List<Integer> stepsOfWeek = weeklyData.get(currentWeek);
            List<String> weekDays = getWeekDays(); // Get the week days here
            if (stepsOfWeek != null) {
                setupBarChartWeekly(stepsOfWeek, weekDays); // Pass weekDays as a parameter
            }
            if (currentWeek == 1) {
                tvCurrentWeek.setText("Current Week");
            } else {
                // Get the first and last date of the week
                List<String> sortedDates = new ArrayList<>(stepsData.keySet());
                Collections.sort(sortedDates); // Sort the dates in ascending order
                Collections.reverse(sortedDates); // Reverse the order of the sorted dates

                String firstDateOfWeek = sortedDates.get(Math.min(currentWeek * 7 - 1, sortedDates.size() - 1));

                String lastDateOfWeek = sortedDates.get((currentWeek - 1) * 7);
                tvCurrentWeek.setText(firstDateOfWeek + " - " + lastDateOfWeek);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the exception or notify the user
        }
    }



    private void setupBarChartWeekly(List<Integer> stepsOfWeek, List<String> weekDays) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < stepsOfWeek.size(); i++) {
            entries.add(new BarEntry(i, stepsOfWeek.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Steps");
        dataSet.setColor(Color.BLUE);
        BarData barData = new BarData(dataSet);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekDays));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void separateWeeklyData() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        // Get the week number of the last date in the stepsData
        String lastDate = new ArrayList<>(stepsData.keySet()).get(stepsData.size() - 1);
        calendar.setTime(dateFormat.parse(lastDate));
        int lastWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

        for (Map.Entry<String, Integer> entry : stepsData.entrySet()) {
            calendar.setTime(dateFormat.parse(entry.getKey()));
            int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
            int weekKey = lastWeekOfYear - weekOfYear + 1;

            List<Integer> stepsOfWeek = weeklyData.containsKey(weekKey) ?
                    new ArrayList<>(weeklyData.get(weekKey)) : Arrays.asList(0, 0, 0, 0, 0, 0, 0);

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Convert to 0-based index
            stepsOfWeek.set(dayOfWeek, entry.getValue());
            weeklyData.put(weekKey, stepsOfWeek);
        }
    }


    private List<String> getWeekDays() throws ParseException {
        List<String> sortedDates = new ArrayList<>(stepsData.keySet());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateFormat.parse(sortedDates.get(0)));
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Generate the list of week days starting from the first day
        String[] allDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        List<String> weekDays = new ArrayList<>();
        for (int i = firstDayOfWeek - 1; i < allDays.length; i++) {
            weekDays.add(allDays[i]);
        }
        for (int i = 0; i < firstDayOfWeek - 1; i++) {
            weekDays.add(allDays[i]);
        }

        return weekDays;
    }

    private void populateStepsData() {
        // Populate your stepsData here
        // Example:
        stepsData.put("11/03/2024", 13500);
        stepsData.put("14/03/2024", 12000);
        stepsData.put("15/03/2024", 11500);
        stepsData.put("16/03/2024", 11000);
        stepsData.put("19/03/2024", 9500);
        stepsData.put("20/03/2024", 9000);

        // Add more data...

        // Fill in the missing dates and ensure the range starts with Sunday and ends with Saturday
        fillMissingDates();
        ensureWeekStartsAndEnds();
    }


    private void fillMissingDates() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(stepsData.keySet().iterator().next(), formatter);
        LocalDate endDate = LocalDate.parse(stepsData.keySet().iterator().next(), formatter);

        for (String date : stepsData.keySet()) {
            LocalDate localDate = LocalDate.parse(date, formatter);
            if (localDate.isBefore(startDate)) {
                startDate = localDate;
            }
            if (localDate.isAfter(endDate)) {
                endDate = localDate;
            }
        }

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            String dateString = currentDate.format(formatter);
            stepsData.putIfAbsent(dateString, 0);
            currentDate = currentDate.plusDays(1);
        }
    }

    private void ensureWeekStartsAndEnds() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate firstDate = LocalDate.parse(stepsData.keySet().iterator().next(), formatter);
        LocalDate lastDate = LocalDate.parse(stepsData.keySet().iterator().next(), formatter);

        for (String date : stepsData.keySet()) {
            LocalDate localDate = LocalDate.parse(date, formatter);
            if (localDate.isBefore(firstDate)) {
                firstDate = localDate;
            }
            if (localDate.isAfter(lastDate)) {
                lastDate = localDate;
            }
        }

        while (firstDate.getDayOfWeek().getValue() != 7) {
            firstDate = firstDate.minusDays(1);
        }

        while (lastDate.getDayOfWeek().getValue() != 6) {
            lastDate = lastDate.plusDays(1);
        }

        LocalDate currentDate = firstDate;
        while (!currentDate.isAfter(lastDate)) {
            String dateString = currentDate.format(formatter);
            stepsData.putIfAbsent(dateString, 0);
            currentDate = currentDate.plusDays(1);
        }
    }
    private String getWeekOfYearAndYear(String date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateFormat.parse(date));
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        return "Week " + weekOfYear + " of " + year;
    }
    private int getCurrentWeekNumber() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.WEEK_OF_YEAR);
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

