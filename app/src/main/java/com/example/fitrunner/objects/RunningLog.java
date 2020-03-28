package com.example.fitrunner.objects;

public class RunningLog {
    String speed;
    String pace;
    String time;
    String distance;
    String dateTime;

    public RunningLog() {
    }

    public RunningLog(String speed, String pace, String time, String distance, String dateTime) {
        this.speed = speed;
        this.pace = pace;
        this.time = time;
        this.distance = distance;
        this.dateTime = dateTime;
    }

    public String getSpeed() {
        return speed;
    }

    public String getPace() {
        return pace;
    }

    public String getTime() {
        return time;
    }

    public String getDistance() {
        return distance;
    }

    public String getDateTime() {
        return dateTime;
    }
}
