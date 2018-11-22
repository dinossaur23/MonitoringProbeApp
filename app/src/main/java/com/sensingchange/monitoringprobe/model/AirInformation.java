package com.sensingchange.monitoringprobe.model;

import com.google.gson.annotations.SerializedName;

public class AirInformation {
    @SerializedName("timestamp")
    private String timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    @SerializedName("last_air_humidity")
    private Humidity last_air_humidity;

    public Humidity getAirHumidity() {
        return last_air_humidity;
    }

    @SerializedName("last_air_temperature")
    private Humidity last_air_temperature;

    public Humidity getAirTemperature() {
        return last_air_temperature;
    }

    @SerializedName("last_air_luminosity")
    private Humidity last_air_luminosity;

    public Humidity getAirLuminosity() {
        return last_air_luminosity;
    }

    @SerializedName("last_soil_humidity")
    private Humidity last_soil_humidity;

    public Humidity getSoilHumidity() {
        return last_soil_humidity;
    }

    @SerializedName("last_soil_temperature")
    private Humidity last_soil_temperature;

    public Humidity getSoilTemperature() {
        return last_soil_temperature;
    }

    @SerializedName("last_soil_capacity")
    private Humidity last_soil_capacity;

    public Humidity getSoilCapacity() {
        return last_soil_capacity;
    }

    @SerializedName("last_weather_raindrop")
    private Humidity last_weather_raindrop;

    public Humidity getWeatherRainfall() {
        return last_weather_raindrop;
    }


    @SerializedName("last_weather_wind_direction")
    private Humidity last_weather_wind_direction;

    public Humidity getWeatherWindDirection() {
        return last_weather_wind_direction;
    }

    @SerializedName("last_weather_wind_speed")
    private Humidity last_weather_wind_speed;

    public Humidity getWeatherWindSpeed() {
        return last_weather_wind_speed;
    }

    @SerializedName("last_geolocation")
    private Humidity last_geolocation;

    public Humidity getGeolocation() {
        return last_geolocation;
    }

    public AirInformation(String timestamp, Humidity last_air_humidity, Humidity last_air_temperature,
                          Humidity last_air_luminosity, Humidity last_soil_humidity, Humidity last_soil_temperature,
                          Humidity last_soil_capacity, Humidity last_weather_raindrop, Humidity last_weather_wind_direction,
                          Humidity last_weather_wind_speed, Humidity last_geolocation) {
        this.timestamp = timestamp;
        this.last_air_humidity = last_air_humidity;
        this.last_air_temperature = last_air_temperature;
        this.last_air_luminosity = last_air_luminosity;
        this.last_soil_humidity = last_soil_humidity;
        this.last_soil_temperature = last_soil_temperature;
        this.last_soil_capacity = last_soil_capacity;
        this.last_weather_raindrop = last_weather_raindrop;
        this.last_weather_wind_direction = last_weather_wind_direction;
        this.last_weather_wind_speed = last_weather_wind_speed;
        this.last_geolocation= last_geolocation;
    }
}