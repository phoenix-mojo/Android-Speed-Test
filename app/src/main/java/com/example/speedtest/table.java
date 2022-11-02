package com.example.speedtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TableRow;

public class table extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);


        for (int i = 0; i < 4; i++) {

            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new  TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            String newString;
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if(extras == null) {
                    newString= null;
                } else {
                    newString= extras.getString("Mode");
                }
            } else {
                newString= (String) savedInstanceState.getSerializable("Mode");
            }

        }
    }
}


//textView.setText("TestMode: " + testMode + "Progress: " + (int) progressPercent + "%" + ", SpeedMbps: " + deger + ", ProcessedFileSizeMb: " + processedFileSize + ", TotalFileSizeMb: " + totalFileSize + ", ElapsedTimeSec: " + elapsedTimeSec);