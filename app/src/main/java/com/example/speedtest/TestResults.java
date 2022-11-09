package com.example.speedtest;

import java.util.ArrayList;

public class TestResults {
    public int TotalTimeSec;
    public ArrayList<TestResult> Results;

    TestResults()
    {
        TotalTimeSec = 0;
        Results = new ArrayList<>();
    }
}
