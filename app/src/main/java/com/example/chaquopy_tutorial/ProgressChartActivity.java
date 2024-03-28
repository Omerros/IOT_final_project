package com.example.chaquopy_tutorial;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.util.LinkedHashMap;

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
    private Map<String, Integer> stepsData = new LinkedHashMap<>();

    private Map<String, Integer> stepsDataMonthly = new LinkedHashMap<>();
    private Map<Integer, List<Integer>> monthlyData = new LinkedHashMap<>();
    private Map<Integer, String> monthlyIndex = new LinkedHashMap<>();
    private Map<Integer, String> weeklyIndex = new LinkedHashMap<>();

    private int currentWeek = 1; // Start with the current week as week 1
    private int maxWeek = 1; // Start with the current week as week 1
    private int currentMonth = 1; // Start with the current week as week 1
    private Map<Integer, List<Integer>> weeklyData = new LinkedHashMap<>();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private String[] allDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private boolean isMonthlyView = false; // Flag to track the current view

    private int blue = Color.rgb(74, 144, 226); // #4A90E2
    private int green = Color.rgb(126, 211, 33); // #7ED321
    private int red = Color.rgb(208, 2, 27); // #D0021B

    int avgSteps;
    double avgKm;



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
        Button btnWeeklyView = findViewById(R.id.btnWeeklyView);
        Button btnMonthlyView = findViewById(R.id.btnMonthlyView);

        // Populate stepsData with your data
        populateStepsData();

        // Separate the stepsData into weekly data
        try {
            separateWeeklyData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            separateMonthlyData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Update the chart with the current week's data
        updateChart(tvCurrentWeek);

        btnPrev.setOnClickListener(view -> {
            if (isMonthlyView) {
                if (currentMonth > 1) {
                    currentMonth--;
                    updateChart(tvCurrentWeek);
                }
            } else {
                if (currentWeek > 1) {
                    currentWeek--;
                    updateChart(tvCurrentWeek);
                }
            }
        });

        btnNext.setOnClickListener(view -> {
            if (isMonthlyView) {
                if (currentMonth < monthlyData.size()) {
                    currentMonth++;
                    updateChart(tvCurrentWeek);
                }
            } else {
                if (currentWeek < weeklyData.size()) {
                    currentWeek++;
                    updateChart(tvCurrentWeek);
                }
            }
        });

        btnWeeklyView.setOnClickListener(view -> {
            if (isMonthlyView) {
                isMonthlyView = false;
                updateChart(tvCurrentWeek);
            }
        });

        btnMonthlyView.setOnClickListener(view -> {
            if (!isMonthlyView) {
                isMonthlyView = true;
                updateChart(tvCurrentWeek);
            }
        });
    }

    private void updateChart(TextView tvCurrentWeek) {
        if (isMonthlyView) {
            List<Integer> stepsOfMonth = monthlyData.get(currentMonth);
            List<String> monthDays = monthlyDates.get(currentMonth); // Use the list of dates for the X-axis labels
            if (stepsOfMonth != null) {
                setupBarChartMonthly(stepsOfMonth, monthDays); // Pass the list of dates as a parameter
            }

            tvCurrentWeek.setText(monthlyIndex.get(currentMonth));
        }
            // Update the chart to show monthly data
            // Example: setupBarChartMonthly(monthlyData);
         else {
            try {
                List<Integer> stepsOfWeek = weeklyData.get(currentWeek);
                List<String> weekDays = getWeekDays(); // Get the week days here
                if (stepsOfWeek != null) {
                    setupBarChartWeekly(stepsOfWeek, weekDays); // Pass weekDays as a parameter
                }
                // Get the first and last date of the week
                List<String> sortedDates = new ArrayList<>(stepsData.keySet());
                tvCurrentWeek.setText(weeklyIndex.get(currentWeek));

            } catch (ParseException e) {
                e.printStackTrace();
                // Handle the exception or notify the user
            }
        }
    }
    private void printMonthlyData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Monthly Data:\n");
        for (Map.Entry<Integer, List<Integer>> entry : monthlyData.entrySet()) {
            stringBuilder.append("Month Key: ").append(entry.getKey()).append(", Steps: ").append(entry.getValue()).append("\n");
        }
        Log.d("bolbol", stringBuilder.toString());
    }
    private void setupBarChartMonthly(List<Integer> stepsOfMonth, List<String> monthDays) {
        List<BarEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        String todayDate = dateFormat.format(Calendar.getInstance().getTime());

        for (int i = 0; i < stepsOfMonth.size(); i++) {
            entries.add(new BarEntry(i, stepsOfMonth.get(i)));

            if (monthDays.get(i).equals(todayDate)) {
                colors.add(blue); // Today's bar
            } else if (stepsOfMonth.get(i) >= targetSteps) {
                colors.add(green); // Above/equal target
            } else {
                colors.add(red); // Below target
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Monthly Steps");
        dataSet.setColors(colors);
        BarData barData = new BarData(dataSet);

        List<String> formattedLabels = new ArrayList<>();
        for (String date : monthDays) {
            // Format each date to remove the year
            String formattedDate = date.substring(0, date.length() - 5);
            formattedLabels.add(formattedDate);
        }
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(formattedLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelRotationAngle(325);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }




    private void setupBarChartWeekly(List<Integer> stepsOfWeek, List<String> weekDays) {
        List<BarEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        int dayOfWeekIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String dayOfWeekString = allDays[dayOfWeekIndex-1 ];
        int count = 0;
        int sum = 0;
        for (int i = 0; i < stepsOfWeek.size(); i++) {
            int stepsOfDay = stepsOfWeek.get(i);
            sum = sum + stepsOfDay;
            if (stepsOfDay>0){
                count = i;
            }
            entries.add(new BarEntry(i, stepsOfDay));
            Log.i("bolbol", dayOfWeekString);
            Log.i("bolbol", weekDays.get(i));

            if (dayOfWeekString.equals(weekDays.get(i)) && currentWeek == maxWeek) {
                colors.add(blue); // Today's bar
            } else if (stepsOfWeek.get(i) >= targetSteps) {
                colors.add(green); // Above/equal target
            } else {
                colors.add(red); // Below target
            }
        }
        avgSteps = sum/count;
        avgKm = Math.round(100*0.3*avgSteps)/ 100.0;

        BarDataSet dataSet = new BarDataSet(entries, "Steps");
        dataSet.setColors(colors);
        BarData barData = new BarData(dataSet);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekDays));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelRotationAngle(0);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void logStepsData() {
        for (Map.Entry<String, Integer> entry : stepsData.entrySet()) {
            Log.i("StepsData", "Date: " + entry.getKey() + ", Steps: " + entry.getValue());
        }
    }
    private void separateWeeklyData() throws ParseException {
        logStepsData();
        Calendar calendar = Calendar.getInstance();
        int count = 0;
        int weekKey = 1; // Start with week 1
        String firstDateWeek = "";
        String lastDateWeek;
        for (Map.Entry<String, Integer> entry : stepsData.entrySet()) {
            calendar.setTime(dateFormat.parse(entry.getKey()));
            List<Integer> stepsOfWeek = weeklyData.containsKey(weekKey) ?
                    new ArrayList<>(weeklyData.get(weekKey)) : Arrays.asList(0, 0, 0, 0, 0, 0, 0);

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Convert to 0-based index
            stepsOfWeek.set(dayOfWeek, entry.getValue());
            weeklyData.put(weekKey, stepsOfWeek);
            if (count == 0) {
                firstDateWeek = entry.getKey();
            }
            if (count == 6) {
                currentWeek = weekKey;
                maxWeek = weekKey;
                lastDateWeek = entry.getKey();
                weeklyIndex.put(weekKey, firstDateWeek + "-" + lastDateWeek);
                weekKey++; // Increment the weekKey only after a full week has been processed
                count = -1;
            }
            count++;
        }
    }

    private Map<Integer, List<String>> monthlyDates = new LinkedHashMap<>(); // Add this line at the beginning of the class

    private void separateMonthlyData() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        String lastDate = new ArrayList<>(stepsDataMonthly.keySet()).get(stepsDataMonthly.size() - 1);
        calendar.setTime(dateFormat.parse(lastDate));
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        for (Map.Entry<String, Integer> entry : stepsDataMonthly.entrySet()) {
            calendar.setTime(dateFormat.parse(entry.getKey()));
            int monthOfYear = calendar.get(Calendar.MONTH);
            int monthKey = monthOfYear + 1;

            int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            List<Integer> stepsOfMonth = monthlyData.containsKey(monthKey) ?
                    new ArrayList<>(monthlyData.get(monthKey)) : new ArrayList<>(Collections.nCopies(daysInMonth, 0));

            List<String> datesOfMonth = monthlyDates.containsKey(monthKey) ?
                    new ArrayList<>(monthlyDates.get(monthKey)) : new ArrayList<>(Collections.nCopies(daysInMonth, ""));

            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH) - 1; // Convert to 0-based index
            stepsOfMonth.set(dayOfMonth, entry.getValue());
            datesOfMonth.set(dayOfMonth, entry.getKey());
            monthlyData.put(monthKey, stepsOfMonth);
            monthlyDates.put(monthKey, datesOfMonth);

            monthlyIndex.put(monthKey, months[monthKey-1]);
            currentMonth = monthKey;
        }
    }




    private List<String> getWeekDays() throws ParseException {
        List<String> sortedDates = new ArrayList<>(stepsData.keySet());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateFormat.parse(sortedDates.get(0)));
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Generate the list of week days starting from the first day
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
        stepsData.put("06/01/2024", 9500);
        stepsData.put("07/01/2024", 9500);
        stepsData.put("11/01/2024", 9500);
        stepsData.put("11/02/2024", 9500);
        stepsData.put("11/03/2024", 13500);
        stepsData.put("14/03/2024", 12000);
        stepsData.put("15/03/2024", 11500);
        stepsData.put("16/03/2024", 11000);
        stepsData.put("19/03/2024", 9500);
        stepsData.put("20/03/2024", 9000);
        stepsData.put("24/03/2024", 11500);
        stepsData.put("25/03/2024", 11000);
        stepsData.put("26/03/2024", 9500);
        stepsData.put("27/03/2024", 9000);
        stepsData.put("28/03/2024", 3500);

        // Add more data...

        // Fill in the missing dates and ensure the range starts with Sunday and ends with Saturday
        fillMissingDates();
        ensureMonthStartsAndEnds();
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
    private void ensureMonthStartsAndEnds() {
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

        // Set firstDate to the first day of its month
        firstDate = firstDate.withDayOfMonth(1);

        // Set lastDate to the last day of its month
        lastDate = lastDate.withDayOfMonth(lastDate.lengthOfMonth());

        LocalDate currentDate = firstDate;
        while (!currentDate.isAfter(lastDate)) {
            String dateString = currentDate.format(formatter);
            // Put the original value if it exists, otherwise put 0
            stepsDataMonthly.put(dateString, stepsData.getOrDefault(dateString, 0));
            currentDate = currentDate.plusDays(1);
        }
    }


    private void ensureWeekStartsAndEnds() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Map<LocalDate, Integer> tempMap = new TreeMap<>();

        // Convert the string keys to LocalDate and populate the tempMap
        for (Map.Entry<String, Integer> entry : stepsData.entrySet()) {
            LocalDate date = LocalDate.parse(entry.getKey(), formatter);
            tempMap.put(date, entry.getValue());
        }

        LocalDate firstDate = tempMap.keySet().iterator().next();
        LocalDate lastDate = tempMap.keySet().iterator().next();

        for (LocalDate date : tempMap.keySet()) {
            if (date.isBefore(firstDate)) {
                firstDate = date;
            }
            if (date.isAfter(lastDate)) {
                lastDate = date;
            }
        }

        while (firstDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
            firstDate = firstDate.minusDays(1);
        }

        while (lastDate.getDayOfWeek() != DayOfWeek.SATURDAY) {
            lastDate = lastDate.plusDays(1);
        }

        LocalDate currentDate = firstDate;
        while (!currentDate.isAfter(lastDate)) {
            tempMap.putIfAbsent(currentDate, 0);
            currentDate = currentDate.plusDays(1);
        }

        // Convert the LocalDate keys back to String and update stepsData
        stepsData.clear();
        for (Map.Entry<LocalDate, Integer> entry : tempMap.entrySet()) {
            stepsData.put(entry.getKey().format(formatter), entry.getValue());
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


