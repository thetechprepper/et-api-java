package com.thetechprepper.emcommtools.api.util;

public final class BandUtils {

    public static final String UNKNOWN_BAND = "unknown";

    private BandUtils() {
    }

    public static String getBand(final double hz) {
        double mhz = hz / 1_000_000.0;

        if (mhz >= 1.8   && mhz < 2.0)     return "160m";
        if (mhz >= 3.5   && mhz < 4.0)     return "80m";
        if (mhz >= 5.25  && mhz < 5.45)    return "60m";
        if (mhz >= 7.0   && mhz < 7.3)     return "40m";
        if (mhz >= 10.1  && mhz < 10.15)   return "30m";
        if (mhz >= 14.0  && mhz < 14.35)   return "20m";
        if (mhz >= 18.068 && mhz < 18.168) return "17m";
        if (mhz >= 21.0  && mhz < 21.45)   return "15m";
        if (mhz >= 24.89 && mhz < 24.99)   return "12m";
        if (mhz >= 28.0  && mhz < 29.7)    return "10m";
        if (mhz >= 50.0  && mhz < 54.0)    return "6m";
        if (mhz >= 144.0 && mhz < 148.0)   return "2m";
        if (mhz >= 222.0 && mhz < 225.0)   return "1.25m";
        if (mhz >= 420.0 && mhz < 450.0)   return "70cm";

        return "unknown";
    }
}
