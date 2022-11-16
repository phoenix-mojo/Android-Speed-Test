package com.example.speedtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

public class TestSummary extends AppCompatActivity {

    TableLayout summaryTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        summaryTable = (TableLayout) findViewById(R.id.summary);

        Intent intent = getIntent();
        TestResults results = (TestResults) intent.getSerializableExtra("RESULTS");
        Display(results);
    }

    public TableRow prepareRow(int iteration, String mode, int speed, int timeTaken, String result)
    {
        TableRow resultRow = new TableRow(this);

        TextView iterationView = new TextView(this);
        iterationView.setText(Integer.toString(iteration));
        iterationView.setTextColor(Color.WHITE);
        iterationView.setGravity(Gravity.CENTER);
        resultRow.addView(iterationView);

        TextView modeView = new TextView(this);
        modeView.setText(mode);
        modeView.setTextColor(Color.WHITE);
        modeView.setGravity(Gravity.CENTER);
        resultRow.addView(modeView);

        TextView speedView = new TextView(this);
        speedView.setText(Integer.toString(speed));
        speedView.setTextColor(Color.WHITE);
        speedView.setGravity(Gravity.CENTER);
        resultRow.addView(speedView);

        TextView timeTakenView = new TextView(this);
        timeTakenView.setText(Integer.toString(timeTaken));
        timeTakenView.setTextColor(Color.WHITE);
        timeTakenView.setGravity(Gravity.CENTER);
        resultRow.addView(timeTakenView);

        TextView resultView = new TextView(this);
        resultView.setText(result);
        resultView.setTextColor(Color.WHITE);
        resultView.setGravity(Gravity.CENTER);
        resultRow.addView(resultView);

        return resultRow;
    }

    public void Display(TestResults results)
    {
        System.out.println("Size of results: " + results.Results.size() + " TotalTimeSec: " + results.TotalTimeSec);

        for (int i = 0; i < results.Results.size(); i++)
        {
            int iteration = results.Results.get(i).iterationNumber;
            String mode = results.Results.get(i).testMode;
            int speed = results.Results.get(i).speed;
            int time = results.Results.get(i).elapsedTimeSec;
            String result = results.Results.get(i).status;

            System.out.println("Iteration: "  + iteration + " Mode: " + mode + " Speed: " + speed + " elapsedTimeSec: " + time + " Result: " + result);
            summaryTable.addView(prepareRow(iteration, mode, speed, time, result));
        }
    }
}