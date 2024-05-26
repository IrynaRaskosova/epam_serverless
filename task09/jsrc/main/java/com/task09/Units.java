package com.task09;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Units {

    private String time;
    @JsonProperty("temperature_2m")
    private String temperature2m;

    // Getters and setters

    @Override
    public String toString() {
        return "Units{" +
                "time='" + time + '\'' +
                ", temperature2m='" + temperature2m + '\'' +
                '}';
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature2m() {
        return temperature2m;
    }

    public void setTemperature2m(String temperature2m) {
        this.temperature2m = temperature2m;
    }

}
