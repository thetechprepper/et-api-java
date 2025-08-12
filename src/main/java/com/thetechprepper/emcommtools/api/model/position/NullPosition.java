package com.thetechprepper.emcommtools.api.model.position;

import io.swagger.v3.oas.annotations.media.Schema;

public class NullPosition extends Position {
    private Double lat;
    private Double lon;
    private String gridSquare;

    public static final String SOURCE = "None";

    protected NullPosition() {
        // prevent direct instantiation
    }

    public static NullPosition NO_POSITION = new NullPosition()
        .withLat(0D)
        .withLon(0D)
        .withGridSquare("");

    public NullPosition withLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public NullPosition withLon(Double lon) {
        this.lon = lon;
        return this;
    }

    public NullPosition withGridSquare(String gridSquare) {
        this.gridSquare = gridSquare;
        return this;
    }

    @Override
    @Schema(description = "Source of the position data", example = "None", required = true)
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

    @Override
    public String toString() {
        return "NullPosition [lat=" + lat + ", lon=" + lon + ", gridSquare=" + gridSquare + ", getSource()="
                + getSource() + "]";
    }
}