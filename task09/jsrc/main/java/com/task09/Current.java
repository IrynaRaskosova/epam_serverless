package com.task09;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Current {
    private String time;
    private int interval;
    @JsonProperty("temperature_2m")
    private double temperature2m;

    @JsonProperty("wind_speed_10m")
    private double windSpeed10m;

    @Override
    public String toString() {
        return "Current{" +
                "time='" + time + '\'' +
                ", interval=" + interval +
                ", temperature2m=" + temperature2m +
                ", windSpeed10m=" + windSpeed10m +
                '}';
    }
// Getters and setters

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public double getTemperature2m() {
        return temperature2m;
    }

    public void setTemperature2m(double temperature2m) {
        this.temperature2m = temperature2m;
    }

    public double getWindSpeed10m() {
        return windSpeed10m;
    }

    public void setWindSpeed10m(double windSpeed10m) {
        this.windSpeed10m = windSpeed10m;
    }
}
