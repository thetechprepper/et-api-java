package com.thetechprepper.emcommtools.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WinlinkRmsChannel {

    @JsonProperty("base_callsign")
    private String baseCallsign;

    private String callsign;

    private Double lat;

    private Double lon;

    private String mode;

    private Integer modeCode;

    private Double freq;

    protected WinlinkRmsChannel() {
    }

    public static WinlinkRmsChannel newInstance() {
        return new WinlinkRmsChannel();
    }

    public WinlinkRmsChannel withBaseCallsign(String baseCallsign) {
        this.baseCallsign = baseCallsign;
        return this;
    }

    public WinlinkRmsChannel withCallsign(String callsign) {
        this.callsign = callsign;
        return this;
    }

    public WinlinkRmsChannel withLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public WinlinkRmsChannel withLon(Double lon) {
        this.lon = lon;
        return this;
    }

    public WinlinkRmsChannel withMode(String mode) {
        this.mode = mode;
        return this;
    }

    public WinlinkRmsChannel withModeCode(Integer modeCode) {
        this.modeCode = modeCode;
        return this;
    }

    public WinlinkRmsChannel withFreq(Double freq) {
        this.freq = freq;
        return this;
    }

    // Getters

    public String getBaseCallsign() {
        return baseCallsign;
    }

    public String getCallsign() {
        return callsign;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getMode() {
        return mode;
    }

    public Integer getModeCode() {
        return modeCode;
    }

    public Double getFreq() {
        return freq;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WinlinkRmsChannel{");
        sb.append("baseCallsign='").append(baseCallsign).append('\'');
        sb.append(", callsign='").append(callsign).append('\'');
        sb.append(", lat='").append(lat).append('\'');
        sb.append(", lon=").append(lon);
        sb.append(", mode='").append(mode).append('\'');
        sb.append(", modeCode='").append(modeCode).append('\'');
        sb.append(", freq='").append(freq).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
