package com.thetechprepper.emcommtools.api.model.position;

import com.thetechprepper.emcommtools.api.util.GridSquareUtils;

import io.swagger.v3.oas.annotations.media.Schema;

public class MaidenheadPosition extends Position {

    private Double lat;
    private Double lon;
    private String gridSquare;

    public static final String SOURCE = "Maidenhead";

    protected MaidenheadPosition() {
        // prevent direct instantiation
    }

    public static MaidenheadPosition newInstance(final String gridSquare) {

        double[] latLon = GridSquareUtils.maidenheadToLatLon(gridSquare);
        return new MaidenheadPosition()
            .withGridSquare(gridSquare)
            .withLat(latLon[0])
            .withLon(latLon[1]);
    }

    public MaidenheadPosition withLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public MaidenheadPosition withLon(Double lon) {
        this.lon = lon;
        return this;
    }

    public MaidenheadPosition withGridSquare(String gridSquare) {
        this.gridSquare = gridSquare;
        return this;
    }

    @Override
    @Schema(description = "Source of the position data", example = "Maidenhead", required = true)
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
        return "MaidenheadPosition [lat=" + lat + ", lon=" + lon + ", gridSquare=" + gridSquare + ", getSource()="
                + getSource() + "]";
    }
}