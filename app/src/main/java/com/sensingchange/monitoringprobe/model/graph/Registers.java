package com.sensingchange.monitoringprobe.model.graph;

import com.google.gson.annotations.SerializedName;

public class Registers {
    @SerializedName("day")
    private String day;
    @SerializedName("hour")
    private String hour;
    @SerializedName("value")
    private String value;

    public Registers(String day, String hour, String value) {
        this.day = day;
        this.hour = hour;
        this.value = value;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
