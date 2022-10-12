package com.example.speedtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.graphics.Color;

import com.jignesh13.speedometer.SpeedoMeterView;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import fr.bmartel.speedtest.model.SpeedTestMode;

import com.jignesh13.speedometer.SpeedoMeterView;

public class MainActivity extends AppCompatActivity {

    SpeedoMeterView speedoMeterView;
    TextView textView;
    Button triggerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        speedoMeterView = findViewById(R.id.speedometerview);
        triggerButton = findViewById(R.id.button);

    }

    public void TestTriggerButton(View view) {
        new SpeedTestTask().execute();
    }


    public class SpeedTestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                String formatSpeedTestMode(SpeedTestMode mode) {
                    String result = mode.toString();
                    result = result.substring(0,1).toUpperCase() + result.substring(1).toLowerCase();

                    return result;
                }

                @Override
                public void onCompletion(SpeedTestReport report) {

                    final int deger = (int) report.getTransferRateBit().intValue()/1000000;
                    final int processedFileSize = (int) report.getTemporaryPacketSize()/1000000;
                    final int totalFileSize = (int) report.getTotalPacketSize()/1000000;
                    final int elapsedTimeSec = (int) ((report.getReportTime() - report.getStartTime()) / 1000000000);
                    final String testMode = formatSpeedTestMode(report.getSpeedTestMode());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // speedoMeterView.setSpeed(deger, true);
                            speedoMeterView.setSpeed(0,true);//speed set 0 to 140
                            speedoMeterView.setNeedlecolor(Color.GREEN);
                            textView.setText("TestMode: " + testMode + ", Progress: 100%" + ", SpeedMbps: " + deger+  ", ProcessedFileSizeMb: " + processedFileSize + ", TotalFileSizeMb: " + totalFileSize + ", ElapsedTimeSec: " + elapsedTimeSec);
                            triggerButton.setText("Test completed");
                            triggerButton.setTextColor(Color.BLUE);
                            triggerButton.setBackgroundColor(Color.GREEN);
                            triggerButton.setEnabled(true);
                        }
                    });
                    // called when download/upload is finished
                    Log.v("speedtest", "[COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("speedtest", "[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());
                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    System.out.println("onError called: " + errorMessage + "speedTestError: " + speedTestError);
                    speedoMeterView.setNeedlecolor(Color.RED);
                    triggerButton.setText("Test failed");
                    triggerButton.setBackgroundColor(Color.RED);
                    triggerButton.setEnabled(true);
                    // called when a download/upload error occur
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {

                    final int deger = (int) report.getTransferRateBit().intValue()/1000000;
                    final int processedFileSize = (int) report.getTemporaryPacketSize()/1000000;
                    final int totalFileSize = (int) report.getTotalPacketSize()/1000000;
                    final int elapsedTimeSec = (int) ((report.getReportTime() - report.getStartTime()) / 1000000000);
                    final String testMode = formatSpeedTestMode(report.getSpeedTestMode());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // speedoMeterView.setSpeed(deger, true);
                            speedoMeterView.setSpeed(deger,true);
                            textView.setText("TestMode: " + testMode + ", Progress: " + (int) percent + "%" + ", SpeedMbps: " + deger+  ", ProcessedFileSizeMb: " + processedFileSize + ", TotalFileSizeMb: " + totalFileSize + ", ElapsedTimeSec: " + elapsedTimeSec);
                            speedoMeterView.setNeedlecolor(Color.YELLOW);
                            triggerButton.setText("Test in progress");
                            triggerButton.setBackgroundColor(Color.LTGRAY);
                            triggerButton.setTextColor(Color.DKGRAY);
                            triggerButton.setEnabled(false);
                        }
                    });

                    // called to notify download/upload progress
                    Log.v("speedtest", "[PROGRESS] progress : " + percent + "%");
                    Log.v("speedtest", "[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("speedtest", "[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
                }
            });

            speedTestSocket.startUpload("https://testmy.net", 100000000);
           // speedTestSocket.startDownload("https://bouygues.testdebit.info/100M.iso");

            return null;
        }
    }
}

