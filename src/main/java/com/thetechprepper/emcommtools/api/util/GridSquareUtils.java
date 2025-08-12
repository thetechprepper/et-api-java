package com.thetechprepper.emcommtools.api.util;

import com.thetechprepper.emcommtools.api.model.position.GpsPosition;

/**
 * A utility class for dealing with the Maidenhead Locator System.
 */
public class GridSquareUtils {

    public static String toMaidenhead(final GpsPosition position) {
        return toMaidenhead(position.getLat(), position.getLon());
    }

    /**
     * Converts latitude and longitude to a 6-digit maidenhead grid square.
     *
     * @param lat latitude as decimal degrees. Should be negative if you're West.
     * @param lon longitude as decimal degrees. Should be negative if you're South of the equator.
     * @return 6-digit grid square
     */
    public static String toMaidenhead(final Double lat, final Double lon) {
        StringBuilder gridSquare = new StringBuilder();

        // Normalize coordinates: shift origin from (-180,-90) to (0,0)
        double adjLon = lon + 180.0;
        double adjLat = lat + 90.0;

        // Field (20° × 10°)
        int L1 = (int) Math.floor(adjLon / 20.0);
        int L2 = (int) Math.floor(adjLat / 10.0);
        char field1 = (char) ('A' + L1);
        char field2 = (char) ('A' + L2);

        // Square (2° × 1°)
        int N1 = (int) Math.floor((adjLon % 20.0) / 2.0);
        int N2 = (int) Math.floor(adjLat % 10.0);
        char square1 = (char) ('0' + N1);
        char square2 = (char) ('0' + N2);

        // Subsquare (both axes: 24 subdivisions -> 'a'..'x')
        int L3 = (int) Math.floor(((adjLon % 2.0) / 2.0) * 24.0);
        int L4 = (int) Math.floor((adjLat % 1.0) * 24.0);
        char subsquare1 = (char) ('a' + L3);
        char subsquare2 = (char) ('a' + L4);

        return gridSquare
            .append(field1)
            .append(field2)
            .append(square1)
            .append(square2)
            .append(subsquare1)
            .append(subsquare2).toString();
    }

    /**
     * Calculates the approximate center of maidenhead grid as latitude and longitude.
     * 
     * @param gridSquare a 4-digit or 6-digit grid square
     * @return a latitude, longitude tuple
     */
    public static double[] maidenheadToLatLon(final String gridSquare) {
        if (gridSquare == null) {
            throw new IllegalArgumentException("Grid cannot be null");
        }

        String grid = gridSquare.trim().toUpperCase();
        if (grid.length() != 4 && grid.length() != 6) {
            throw new IllegalArgumentException("Grid must be 4 or 6 characters");
        }   

        double lon = (grid.charAt(0) - 'A') * 20 - 180;
        double lat = (grid.charAt(1) - 'A') * 10 - 90;

        lon += Character.getNumericValue(grid.charAt(2)) * 2;
        lat += Character.getNumericValue(grid.charAt(3)) * 1;

        if (grid.length() == 6) {
            lon += (grid.charAt(4) - 'A') * (2.0 / 24.0);
            lat += (grid.charAt(5) - 'A') * (1.0 / 24.0);
        }

        // Center of the square
        if (grid.length() == 6) {
            lon += (2.0 / 24.0) / 2.0;
            lat += (1.0 / 24.0) / 2.0;
        } else {
            lon += 1.0;
            lat += 0.5;
        }

        return new double[] {
            Math.round(lat * 1_000_000d) / 1_000_000d,
            Math.round(lon * 1_000_000d) / 1_000_000d
        };
    }
}