package com.example.speedtest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SpeedoMeterView speedoMeterView;
    TextView textView;
    Spinner packetSize;
    Spinner testMode;
    Button triggerButton;
    Button triggerButton2;

    EditText numberOfTests;
    TextView intervalTests;
    TestResults results;

    String csvFilePath;

    public final String TEST_MODE_UPLOAD = "Upload";
    public final String TEST_MODE_DOWNLOAD = "Download";
    public final String TEST_MODE_UPLOAD_DOWNLOAD = "Upload/Download";
    public final String TEST_RESULT_PASSED = "PASS";
    public final String TEST_RESULT_FAILED = "FAIL";

    final String[] packetSizes = {"1 MB", "5 MB", "10 MB", "50 MB", "100 MB"};
    final String[] testModes = {TEST_MODE_DOWNLOAD, TEST_MODE_UPLOAD, TEST_MODE_UPLOAD_DOWNLOAD};
    final String[] tableColumns = {"TimeStamp", "Iteration", "Mode", "Speed(Mbps)", "TestTime(s)"};

    public void SetSpinnerDropdown(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
    }

    public String getCurrentTimeStamp(String format)
    {
        String dateTime;
        Calendar calendar;
        SimpleDateFormat simpleDateFormat;

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat(format);
        dateTime = simpleDateFormat.format(calendar.getTime());

        return dateTime;
    }
    public String getCurrentLogTimeStamp()
    {
        return getCurrentTimeStamp("MM/dd/yyyy HH:mm:ss");
    }

    public String getCurrentFileTimeStamp()
    {
        return getCurrentTimeStamp("MM_dd_yyyy_HH_mm_ss");
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

        results = new TestResults();

        SetSpinnerDropdown(packetSize, packetSizes);
        SetSpinnerDropdown(testMode, testModes);

    }

    public void TestTriggerButton(View view) {
        results = new TestResults();
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        String csvFileName = getCurrentFileTimeStamp();
        csvFilePath = String.format("%s/%s.csv", directory, csvFileName);

        new SpeedTestTask().execute();
    }

    public void TestTriggerButton2(View view){

        Intent intent = new Intent(this, TestSummary.class);
        Bundle tableColumnsBundle = new Bundle();

        tableColumnsBundle.putStringArray("TABLE_COLUMNS", tableColumns);

        intent.putExtra("RESULTS", results);
        intent.putExtras(tableColumnsBundle);

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
            throw new java.lang.Error("Packet size should be in MB or GB");
        }

        return size * multiplier;
    }

    public class SpeedTestTask extends AsyncTask<Void, Void, String> {

        public Boolean testRunning = false;
        public int fileUploadSizeMb = getFileUploadSizeMb(); // Upload file size in Megabits (Mb)
        public int numberOfIterations = Integer.parseInt(numberOfTests.getText().toString());  // Number of tests
        public int sleepTimeSec = Integer.parseInt(intervalTests.getText().toString());  // Interval between tests in seconds
        public String inputTestMode = testMode.getSelectedItem().toString();  // Input test mode

        String formatSpeedTestMode(SpeedTestMode mode) {
            String result = mode.toString();
            result = result.substring(0, 1).toUpperCase() + result.substring(1).toLowerCase();

            return result;
        }

        int getInputTestModeEnum()
        {
            if (inputTestMode.equalsIgnoreCase(TEST_MODE_UPLOAD))
            {
                return 0;
            }
            else if (inputTestMode.equalsIgnoreCase(TEST_MODE_DOWNLOAD))
            {
                return 1;
            }
            else if (inputTestMode.equalsIgnoreCase(TEST_MODE_UPLOAD_DOWNLOAD))
            {
                return 2;
            }
            else
            {
                throw new java.lang.Error("Unrecognized input test mode detected");
            }
        }

        void runTestUpload(SpeedTestSocket speedTestSocket)
        {
            speedTestSocket.startUpload("https://testmy.net", fileUploadSizeMb * 1000000);
            testRunning = true;
            while(testRunning);
        }

        String getDownloadUrl()
        {
            String url = null;

            switch(fileUploadSizeMb)
            {
                case 1:
                    url = "https://bouygues.testdebit.info/100M/100M.iso";
                    break;
                case 5:
                    url = "https://bouygues.testdebit.info/5M/5M.iso";
                    break;
                case 10:
                    url = "https://bouygues.testdebit.info/100M/100M.iso";
                    break;
                case 50:
                    url = "https://bouygues.testdebit.info/100M/100M.iso";
                    break;
                case 100:
                    url = "https://bouygues.testdebit.info/100M/100M.iso";
                    break;
                default:
                    throw new java.lang.Error("Unexpected download packed size detected");
            }

            return url;
        }

        void runTestDownload(SpeedTestSocket speedTestSocket)
        {
            String downloadUrl = getDownloadUrl();
            speedTestSocket.startDownload(downloadUrl);
            testRunning = true;
            while(testRunning);
        }

        void runTest(SpeedTestSocket speedTestSocket) {
            int inputTestModeEnum = getInputTestModeEnum();

            switch(inputTestModeEnum)
            {
                case 0:
                    runTestUpload(speedTestSocket);
                    break;
                case 1:
                    runTestDownload(speedTestSocket);
                    break;
                case 2:
                    runTestUpload(speedTestSocket);
                    waitForSeconds(sleepTimeSec);
                    runTestDownload(speedTestSocket);
                    break;
                default:
                    throw new java.lang.Error("Unrecognized input test mode detected");
            }
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

        String[] getResultRow(TestResult result, int resultSize)
        {
            String[] resultRow = new String[resultSize];
            if (resultSize <= 0)
            {
                return resultRow;
            }

            resultRow = new String[resultSize];

            resultRow[0] = result.timeStamp;
            resultRow[1] = Integer.toString(result.iterationNumber);
            resultRow[2] = result.testMode;
            resultRow[3] = Integer.toString(result.speed);
            resultRow[4] = Integer.toString(result.elapsedTimeSec);

            return  resultRow;
        }

        List<String[]> formatResults() {
            List<String[]> resultList = new ArrayList<String[]>();
            TestResult result;

            resultList.add(tableColumns);

            for (int i = 0; i < results.Results.size(); i++)
            {
                result = results.Results.get(i);

                resultList.add(getResultRow(result, tableColumns.length));
            }

            return resultList;
        }

        void writeResultsToCsv() {
            try {
                File file = new File(csvFilePath);
                FileWriter outputFile = new FileWriter(file);
                CSVWriter csvWriter = new CSVWriter(outputFile);
                System.out.println("Writing csv to: " + csvFilePath);
                csvWriter.writeAll(formatResults());
                csvWriter.close();
            }
            catch (IOException e) {
                e.printStackTrace();
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

                        TestResult result = new TestResult();
                        result.iterationNumber = finalI;
                        result.elapsedTimeSec = elapsedTimeSec;
                        result.testMode = testMode;
                        result.status = TEST_RESULT_PASSED;
                        result.speed = deger;
                        result.timeStamp = getCurrentLogTimeStamp();
                        results.Results.add(result);
                        results.TotalTimeSec += elapsedTimeSec;

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

                        TestResult result = new TestResult();
                        result.iterationNumber = finalI;
                        result.timeStamp = getCurrentLogTimeStamp();
                        result.status = TEST_RESULT_FAILED;
                        results.Results.add(result);

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

                runTest(speedTestSocket);

                speedTestSocket = null;

                System.out.println("======================Finishing iteration: " + i + " ======================");

                if (i < numberOfIterations) {
                    System.out.println("Sleeping for " +  sleepTimeSec + " seconds before triggering the next iteration");
                    waitForSeconds(sleepTimeSec);
                }



            }

            writeResultsToCsv();

            return null;
        }
    }
}

