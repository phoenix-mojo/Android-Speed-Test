package com.example.speedtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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
    TextView textView2;
    Button triggerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        speedoMeterView = findViewById(R.id.speedometerview);
        triggerButton = findViewById(R.id.button);

    }

    public void TestTriggerButton(View view) {
        new SpeedTestTask().execute();
    }


    public class SpeedTestTask extends AsyncTask<Void, Void, String> {

        public Boolean testRunning = false;
        public int numberOfIterations = 3;  // Number of tests
        public int intervalTests = 10;  // Interval between tests in seconds

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
                                textView.setText("TestMode: " + testMode + "Progress: " + (int) progressPercent + "%" + ", SpeedMbps: " + deger + ", ProcessedFileSizeMb: " + processedFileSize + ", TotalFileSizeMb: " + totalFileSize + ", ElapsedTimeSec: " + elapsedTimeSec);
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
                                textView.setText("TestMode: " + testMode + ", Progress: " + (int) percent + "%" + ", SpeedMbps: " + deger + ", ProcessedFileSizeMb: " + processedFileSize + ", TotalFileSizeMb: " + totalFileSize + ", ElapsedTimeSec: " + elapsedTimeSec);
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

                // speedTestSocket.startUpload("https://testmy.net", 100000000);
                speedTestSocket.startDownload("https://ipv4.bouygues.testdebit.info/50M/50M.iso");

                testRunning = true;

                while(testRunning);

                System.out.println("======================Finishing iteration: " + i + " ======================");

                if (i < numberOfIterations) {
                    System.out.println("Sleeping for 10 seconds before triggering the next iteration");
                    waitForSeconds(intervalTests);
                }
            }

            return null;
        }
    }
}

