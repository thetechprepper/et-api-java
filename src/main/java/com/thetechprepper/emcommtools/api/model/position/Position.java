package com.thetechprepper.emcommtools.api.model.position;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Base class for positions")
public abstract class Position {

    @Schema(description = "Source of position", example = "GPS", required = true)
    public abstract String getSource();

    @Schema(description = "Latitude in decimal degrees", example = "33.896601333", required = true)
    public abstract Double getLat();

    @Schema(description = "Longitude in decimal degrees", example = "-112.043120667", required = true)
    public abstract Double getLon();

    @Schema(description = "Maidenhead grid square corresponding to the position", example = "DM33vo")
    public abstract String getGridSquare();
}