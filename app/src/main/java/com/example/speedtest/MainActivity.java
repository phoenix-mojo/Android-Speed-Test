package com.example.speedtest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.graphics.Color;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import fr.bmartel.speedtest.model.SpeedTestMode;

import com.jignesh13.speedometer.SpeedoMeterView;

public class MainActivity extends AppCompatActivity {

    SpeedoMeterView speedoMeterView;
    TextView textView;
    Spinner packetSize;
    Spinner testMode;
    Button triggerButton;
    Button triggerButton2;

    EditText numberOfTests;
    TextView intervalTests;

    final String[] packetSizes = {"1 MB", "25 MB","50 MB", "75 MB", "100 MB"};
    final String[] testModes = {"Download", "Upload", "Upload & Download"};

    public void SetSpinnerDropdown(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        speedoMeterView = findViewById(R.id.speedometerview);
        triggerButton = findViewById(R.id.button);
        packetSize = findViewById(R.id.spinner1);
        testMode = findViewById(R.id.spinner2);
        numberOfTests = findViewById(R.id.editTextNumber);
        intervalTests = findViewById(R.id.editTextNumber2);
        triggerButton2 = findViewById(R.id.button2);

        SetSpinnerDropdown(packetSize, packetSizes);
        SetSpinnerDropdown(testMode, testModes);

    }

    public void TestTriggerButton(View view) {
        new SpeedTestTask().execute();
    }

    public void TestTriggerButton2(View view){

        Intent intent = new Intent(this, table.class);
        startActivity(intent);

    }


    public int getFileUploadSizeMb() {
        String packetSizeString = packetSize.getSelectedItem().toString();
        String[] tokens = packetSizeString.split(" ");
        int size = Integer.parseInt(tokens[0]);
        int multiplier = 0;

        if (tokens[1].equalsIgnoreCase("MB"))
        {
            multiplier = 1;
        }
        else if (tokens[1].equalsIgnoreCase("GB"))
        {
            multiplier = 1000;
        }
        else
        {
            throw new java.lang.Error("Packet size can only be in MB or GB");
        }

        return size * multiplier;
    }

    public class SpeedTestTask extends AsyncTask<Void, Void, String> {

        public Boolean testRunning = false;
        public int fileUploadSizeMb = getFileUploadSizeMb(); // Upload file size in Megabits (Mb)
        public int numberOfIterations = Integer.parseInt(numberOfTests.getText().toString());  // Number of tests
        public int sleepTimeSec = Integer.parseInt(intervalTests.getText().toString());  // Interval between tests in seconds

        String formatSpeedTestMode(SpeedTestMode mode) {
            String result = mode.toString();
            result = result.substring(0, 1).toUpperCase() + result.substring(1).toLowerCase();

            return result;
        }

        void waitForSeconds(int seconds) {
            int j = seconds + 1;
            while(j-- > 1)
            {
                System.out.println("Starting in seconds: " + j);
                try
                {
                    Thread.sleep( 1000 );
                }
                catch ( InterruptedException e )
                {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            for (int i = 1; i <= numberOfIterations; i++) {

                System.out.println("====================== Starting iteration: " + i+ " ======================");
                SpeedTestSocket speedTestSocket = new SpeedTestSocket();

                // add a listener to wait for speedtest completion and progress
                int finalI = i;
                speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                    @Override
                    public void onCompletion(SpeedTestReport report) {

                        final int deger = (int) report.getTransferRateBit().intValue() / 1000000;
                        final int processedFileSize = (int) report.getTemporaryPacketSize() / 1000000;
                        final int totalFileSize = (int) report.getTotalPacketSize() / 1000000;
                        final int elapsedTimeSec = (int) ((report.getReportTime() - report.getStartTime()) / 1000000000);
                        final String testMode = formatSpeedTestMode(report.getSpeedTestMode());
                        final int progressPercent = (int) report.getProgressPercent();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                speedoMeterView.setSpeed(0, true);//speed set 0 to 140
                                speedoMeterView.setNeedlecolor(Color.GREEN);
                                //textView.setText("TestMode: " + testMode + "Progress: " + (int) progressPercent + "%" + ", SpeedMbps: " + deger + ", ProcessedFileSizeMb: " + processedFileSize + ", TotalFileSizeMb: " + totalFileSize + ", ElapsedTimeSec: " + elapsedTimeSec);
                                triggerButton.setText("Test completed");
                                triggerButton.setTextColor(Color.BLUE);
                                triggerButton.setBackgroundColor(Color.GREEN);
                                triggerButton.setEnabled(true);
                            }
                        });

                        testRunning = false;
                    }

                    @Override
                    public void onError(SpeedTestError speedTestError, String errorMessage) {
                        System.out.println("onError called: " + errorMessage + "speedTestError: " + speedTestError);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                speedoMeterView.setNeedlecolor(Color.RED);
                                triggerButton.setText("Test failed");
                                triggerButton.setBackgroundColor(Color.RED);
                                triggerButton.setEnabled(true);
                            }
                        });

                        testRunning = false;
                    }

                    @Override
                    public void onProgress(float percent, SpeedTestReport report) {

                        final int deger = (int) report.getTransferRateBit().intValue() / 1000000;
                        final int processedFileSize = (int) report.getTemporaryPacketSize() / 1000000;
                        final int totalFileSize = (int) report.getTotalPacketSize() / 1000000;
                        final int elapsedTimeSec = (int) ((report.getReportTime() - report.getStartTime()) / 1000000000);
                        final String testMode = formatSpeedTestMode(report.getSpeedTestMode());




                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                speedoMeterView.setSpeed(deger, true);
                                //textView.setText("TestMode: " + testMode + ", Progress: " + (int) percent + "%" + ", SpeedMbps: " + deger + ", ProcessedFileSizeMb: " + processedFileSize + ", TotalFileSizeMb: " + totalFileSize + ", ElapsedTimeSec: " + elapsedTimeSec);
                                textView.setText(+ finalI +". " + testMode + ": " + deger + " Mbps" +"\nProgress: " + (int) percent + "%" + "\nElapsedTimeSec: " + elapsedTimeSec);
                                speedoMeterView.setNeedlecolor(Color.YELLOW);
                                triggerButton.setText("Test in progress");
                                triggerButton.setBackgroundColor(Color.LTGRAY);
                                triggerButton.setTextColor(Color.DKGRAY);
                                triggerButton.setEnabled(false);
                            }
                        });

                        testRunning = true;
                    }
                });

               
                speedTestSocket.startUpload("https://testmy.net", fileUploadSizeMb * 1000000);
                //speedTestSocket.startDownload("https://ipv4.bouygues.testdebit.info/50M/50M.iso");

                testRunning = true;

                while(testRunning);

                speedTestSocket = null;
                System.out.println("======================Finishing iteration: " + i + " ======================");

                if (i < numberOfIterations) {
                    System.out.println("Sleeping for " +  sleepTimeSec + " seconds before triggering the next iteration");
                    waitForSeconds(sleepTimeSec);
                }



            }

            return null;
        }
    }
}

