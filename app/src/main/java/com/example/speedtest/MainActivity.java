package com.example.speedtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jignesh13.speedometer.SpeedoMeterView;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

import com.jignesh13.speedometer.SpeedoMeterView;

//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//}

public class MainActivity extends AppCompatActivity {

    SpeedoMeterView speedoMeterView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        speedoMeterView = findViewById(R.id.speedometerview);

    }

    public void butonTiklandi(View view) {
        new SpeedTestTask().execute();
    }


    public class SpeedTestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                @Override
                public void onCompletion(SpeedTestReport report) {

                    final int deger = (int) report.getTransferRateBit().intValue()/100000;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // speedoMeterView.setSpeed(deger, true);
                            speedoMeterView.setSpeed(deger,true);//speed set 0 to 140

                            textView.setText(deger+ " Mbps");
                        }
                    });
                    // called when download/upload is finished
                    Log.v("speedtest", "[COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("speedtest", "[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());
                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    System.out.println("onError called: " + errorMessage + "speedTestError: " + speedTestError);
                    // called when a download/upload error occur
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {

                    final int deger = (int) report.getTransferRateBit().intValue()/100000;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // speedoMeterView.setSpeed(deger, true);
                            speedoMeterView.setSpeed(deger,true);//speed set 0 to 140

                            textView.setText(deger+ " Mbps");
                        }
                    });

                    // called to notify download/upload progress
                    Log.v("speedtest", "[PROGRESS] progress : " + percent + "%");
                    Log.v("speedtest", "[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                    Log.v("speedtest", "[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
                }
            });

            speedTestSocket.startDownload("http://mirror.internode.on.net/pub/speed/SpeedTest_16MB");

            return null;
        }
    }
}

