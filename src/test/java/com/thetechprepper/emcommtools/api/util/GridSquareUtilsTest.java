package com.thetechprepper.emcommtools.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GridSquareUtilsTest {

    @Test
    public void testCalcForBerlin() {
        Double lat = 52.52437;
        Double lon = 13.41053;
        assertEquals("JO62qm", GridSquareUtils.toMaidenhead(lat,lon));
    }

    @Test
    public void testCalcForNewRiver() {
        Double lat = 33.915869;
        Double lon = -112.135991;
        assertEquals("DM33wv", GridSquareUtils.toMaidenhead(lat,lon));
    }

    @Test
    public void testCalcForPaloAlto() {
        Double lat = 37.413708;
        Double lon = -122.1073236;
        assertEquals("CM87wj", GridSquareUtils.toMaidenhead(lat, lon));
    }

    @Test
    public void testMaidenheadToLatLonForPaloAltoWith6Digit() {

        // CM87wj is roughly 5' longitude x 2.5' latitude (~2 km resolution)
        // Center coordinates for CM87wj (approximate expected center)
        double expectedLat = 37.413708;
        double expectedLon = -122.107324;

        // Tolerances for ~2 km precision (smaller than 4-digit grid)
        double latTolerance = 0.018;  // ~2 km latitude
        double lonTolerance = 0.018;  // ~2 km longitude

        double[] latLon = GridSquareUtils.maidenheadToLatLon("CM87wj");

        assertEquals(expectedLat, latLon[0], latTolerance);
        assertEquals(expectedLon, latLon[1], lonTolerance);
    }

    @Test
    public void testMaidenheadToLatLonForPaloAltoWith4Digit() {

         // CM87 is 2° wide x 1° tall; center should be 37.5, -123.0
        Double expectedLat = 37.5;
        Double expectedLon = -123.0;

        // Tolerances for ~10–12 km
        double latTolerance = 0.09;  // ~10 km
        double lonTolerance = 0.11;  // ~12 km

        double[] latLon = GridSquareUtils.maidenheadToLatLon("CM87");
        assertEquals(expectedLat, latLon[0], latTolerance);
        assertEquals(expectedLon, latLon[1], lonTolerance);
    }
}
