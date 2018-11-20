package com.sensingchange.monitoringprobe.model.graph;

import java.util.List;

public class AirData {
    private List<Registers> humidities;
    private List<Registers> temperatures;
    private List<Registers> luminosities;

    public List<Registers> getHumidities() {
        return humidities;
    }

    public void setHumidities(List<Registers> humidities) {
        this.humidities = humidities;
    }

    public List<Registers> getTemperatures() {
        return temperatures;
    }

    public void setTemperatures(List<Registers> temperatures) {
        this.temperatures = temperatures;
    }

    public List<Registers> getLuminosities() {
        return luminosities;
    }

    public void setLuminosities(List<Registers> luminosities) {
        this.luminosities = luminosities;
    }
}
