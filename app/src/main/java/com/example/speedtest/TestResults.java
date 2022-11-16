package com.example.speedtest;

import java.io.Serializable;
import java.util.ArrayList;

public class TestResults implements Serializable {
    public int TotalTimeSec;
    public ArrayList<TestResult> Results;

    TestResults()
    {
        TotalTimeSec = 0;
        Results = new ArrayList<>();
    }
}
