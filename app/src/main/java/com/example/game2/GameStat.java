package com.example.game2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GameStat {
    private long startTime;
    private long duration;
    private int score;

    public GameStat(long startTime, long duration, int score) {
        this.startTime = startTime;
        this.duration = duration;
        this.score = score;
    }
    public String getFormattedDuration() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;

        return String.format(Locale.getDefault(), "%d minutes %d seconds", minutes, seconds);
    }
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String startTimeString = dateFormat.format(new Date(startTime));
        long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(duration); // Convert duration to seconds
        String formattedDuration = getFormattedDuration();

        return "Start Time: " + startTimeString +
                //"\nDuration: " + formattedDuration +
                "\nScore: " + score;

    }


    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public int getScore() {
        return score;
    }

}