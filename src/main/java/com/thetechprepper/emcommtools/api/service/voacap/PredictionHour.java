package com.thetechprepper.emcommtools.api.service.voacap;

import java.util.LinkedHashMap;
import java.util.Map;

public class PredictionHour {
    // Ordered map: frequency (MHz) to reliability (0.0–1.0)
    private final Map<Double, Double> freqRel;

    public PredictionHour() {
        this.freqRel = new LinkedHashMap<>();
    }

    public void addFrequency(double frequency, double reliability) {
        if (reliability < 0.0 || reliability > 1.0) {
            throw new IllegalArgumentException("Reliability must be between 0.0 and 1.0");
        }
        freqRel.put(frequency, reliability);
    }

    public Double getReliability(double frequency) {
        return freqRel.get(frequency);
    }

    public Map<Double, Double> getFreqRel() {
        return freqRel;
    }

    public void printFrequencies() {
        freqRel.forEach((freq, rel) -> {
            System.out.printf("%5.1f MHz = %.1f%%%n", freq, rel * 100);
        });
    }
}
