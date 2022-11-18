package com.example.speedtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestSummary extends AppCompatActivity {

    TableLayout summaryTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        summaryTable = (TableLayout) findViewById(R.id.summary);

        Intent intent = getIntent();
        TestResults results = (TestResults) intent.getSerializableExtra("RESULTS");
        String[] tableColumns = (String[]) intent.getExtras().getStringArray("TABLE_RESULTS");

        Display(tableColumns, results);
    }

    public TableRow prepareRow(int iteration, String mode, int speed, int timeTaken, Boolean passed, String timeStamp)
    {
        TableRow resultRow = new TableRow(this);
        int textColor = passed ? Color.GREEN : Color.RED;

        TextView dateView = new TextView(this);
        dateView.setText(timeStamp);
        dateView.setTextColor(textColor);
        dateView.setGravity(Gravity.CENTER);
        resultRow.addView(dateView);

        TextView iterationView = new TextView(this);
        iterationView.setText(Integer.toString(iteration));
        iterationView.setTextColor(textColor);
        iterationView.setGravity(Gravity.CENTER);
        resultRow.addView(iterationView);

        TextView modeView = new TextView(this);
        modeView.setText(mode);
        modeView.setTextColor(textColor);
        modeView.setGravity(Gravity.CENTER);
        resultRow.addView(modeView);

        TextView speedView = new TextView(this);
        speedView.setText(Integer.toString(speed));
        speedView.setTextColor(textColor);
        speedView.setGravity(Gravity.CENTER);
        resultRow.addView(speedView);

        TextView timeTakenView = new TextView(this);
        timeTakenView.setText(Integer.toString(timeTaken));
        timeTakenView.setTextColor(textColor);
        timeTakenView.setGravity(Gravity.CENTER);
        resultRow.addView(timeTakenView);

        return resultRow;
    }

    public void Display(String[] tableColumns, TestResults results)
    {
        System.out.println("Size of results: " + results.Results.size() + " TotalTimeSec: " + results.TotalTimeSec);

        for (int i = 0; i < results.Results.size(); i++)
        {
            int iteration = results.Results.get(i).iterationNumber;
            String mode = results.Results.get(i).testMode;
            int speed = results.Results.get(i).speed;
            int time = results.Results.get(i).elapsedTimeSec;
            String status = results.Results.get(i).status;
            Boolean passed = status.equalsIgnoreCase("PASS");
            String timeStamp = results.Results.get(i).timeStamp;

            System.out.println("TimeStamp: " + timeStamp + " Iteration: "  + iteration + " Mode: " + mode + " Speed: " + speed + " elapsedTimeSec: " + time + " Passed: " + passed);
            summaryTable.addView(prepareRow(iteration, mode, speed, time, passed, timeStamp));
        }
    }
}