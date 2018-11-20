package com.sensingchange.monitoringprobe.model.graph;

import java.util.List;

public class SoilData {
    private List<Registers> humidities;
    private List<Registers> temperatures;
    private List<Registers> capacities;

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

    public List<Registers> getCapacities() {
        return capacities;
    }

    public void setCapacities(List<Registers> capacities) {
        this.capacities = capacities;
    }
}
