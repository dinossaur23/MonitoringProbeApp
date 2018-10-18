package com.sensingchange.monitoringprobe.model;

import com.google.gson.annotations.SerializedName;

public class Humidity {
    @SerializedName("unity")
    String unity;
    @SerializedName("value")
    String value;

    public String getHumidityValue() {
        return value;
    }
}