package com.example.speedtest;

import java.io.Serializable;

public class TestResult implements Serializable {
    public int iterationNumber;
    public int elapsedTimeSec;
    public int speed;
    public String testMode;
    public String status;
};

