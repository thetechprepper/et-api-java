package com.thetechprepper.emcommtools.api.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.thetechprepper.emcommtools.api.model.position.GpsPosition;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GeoUtilsTest {

    @Test
    public void testDistance() {
        GpsPosition wickenburg = new GpsPosition()
            .withLat(33.8944)
            .withLon(-112.8603);

        GpsPosition buckeye = new GpsPosition()
            .withLat(33.518337)
            .withLon(-112.691376);

        Double distanceInMiles = GeoUtils.distance(wickenburg.getLat(), wickenburg.getLon(),
                buckeye.getLat(), buckeye.getLon(), "M");
        assertEquals(Math.round(distanceInMiles), 28);
    }

    /*
    @Test
    public void testBearing() {
        GpsPosition kt1run = new GpsPosition()
            .withLat(33.83334741709874)
            .withLon(-111.9439728157338);

        GpsPosition w7mgw = new GpsPosition()
            .withLat(33.8260)
            .withLon(-112.0268);

        int bearingDegrees = GeoUtils.bearing(kt1run.getLat(), kt1run.getLon(),
                w7mgw.getLat(), w7mgw.getLon());
        assertEquals(169, bearingDegrees);
    }
    */
}
