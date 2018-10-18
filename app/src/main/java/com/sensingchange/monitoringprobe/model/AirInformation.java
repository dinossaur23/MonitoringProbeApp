package com.sensingchange.monitoringprobe.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class AirInformation {
    @SerializedName("timestamp")
    private String timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    @SerializedName("last_humidity")
    private Humidity last_humidity;

    public Humidity getHumidity() {
        return last_humidity;
    }

    public AirInformation(String timestamp, Humidity last_humidity) {
        this.timestamp = timestamp;
        this.last_humidity = last_humidity;
    }
}