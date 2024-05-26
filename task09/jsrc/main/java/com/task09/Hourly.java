package com.task09;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Hourly {
    private List<String> time = new ArrayList<>();

    @JsonProperty("temperature_2m")
    private List<Double> temperature2m = new ArrayList<>();

    @Override
    public String toString() {
        return "Hourly{" +
                "time=" + time +
                ", temperature2m=" + temperature2m +
                '}';
    }
// Getters and setters

    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }

    public List<Double> getTemperature2m() {
        return temperature2m;
    }

    public void setTemperature2m(List<Double> temperature2m) {
        this.temperature2m = temperature2m;
    }

}
