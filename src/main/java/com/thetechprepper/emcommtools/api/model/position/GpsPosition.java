package com.thetechprepper.emcommtools.api.model.position;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public class GpsPosition extends Position {

    private Double alt;
    private Double lat;
    private Double lon;
    private Double speed;
    private LocalDateTime time;
    private String mode;
    private String gridSquare;

    public static final String SOURCE = "GPS";

    public static GpsPosition NO_GPS = new GpsPosition()
        .withLat(0D)
        .withLon(0D)
        .withAlt(0D)
        .withSpeed(0D)
        .withTime(null)
        .withMode("")
        .withGridSquare("");

    public GpsPosition withAlt(Double alt) {
        this.alt = alt;
        return this;
    }

    public GpsPosition withLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public GpsPosition withLon(Double lon) {
        this.lon = lon;
        return this;
    }

    public GpsPosition withSpeed(Double speed) {
        this.speed = speed;
        return this;
    }

    public GpsPosition withTime(LocalDateTime time) {
        this.time = time;
        return this;
    }

    public GpsPosition withMode(String mode) {
        this.mode = mode;
        return this;
    }

    public GpsPosition withGridSquare(String gridSquare) {
        this.gridSquare = gridSquare;
        return this;
    }

    @Override
    @Schema(description = "Source of the position data", example = "GPS", required = true)
    public String getSource() {
        return SOURCE;
    }

    @Override
    public Double getLat() {
        return lat;
    }

    @Override
    public Double getLon() {
        return lon;
    }

    @Override
    public String getGridSquare() {
        return gridSquare;
    }

    public Double getAlt() {
        return alt;
    }

    public Double getSpeed() {
        return speed;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return "GpsPosition [alt=" + alt + ", lat=" + lat + ", lon=" + lon + ", speed=" + speed + ", time=" + time
                + ", mode=" + mode + ", gridSquare=" + gridSquare + ", source=" + getSource() + "]";
    }


}