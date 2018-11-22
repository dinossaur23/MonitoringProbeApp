package com.sensingchange.monitoringprobe.model;

import com.google.gson.annotations.SerializedName;

public class Humidity {
    @SerializedName("unity")
    String unity;
    @SerializedName("value")
    String value;
    @SerializedName("unity_type")
    String unity_type;
    @SerializedName("value_per_hour")
    String value_per_hour;
    @SerializedName("value_per_day")
    String value_per_day;
    @SerializedName("direction")
    String direction;
    @SerializedName("average_value")
    String average_value;
    @SerializedName("max_value")
    String max_value;
    @SerializedName("latitude")
    Double latitude;
    @SerializedName("longitude")
    Double longitude;

    public String getValue() {
        return value;
    }

    public String getUnity() {
        return unity;
    }

    public String getUnityType() {
        return unity_type;
    }

    public String getValuePerHour() {
        return value_per_hour;
    }

    public String getValuePerDay() {
        return value_per_day;
    }

    public String getDirection() {
        return direction;
    }

    public String getAverageValue() {
        return average_value;
    }

    public String getMaxValue() {
        return max_value;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
