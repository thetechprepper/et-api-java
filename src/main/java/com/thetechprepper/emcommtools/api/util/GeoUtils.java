package com.thetechprepper.emcommtools.api.util;

public class GeoUtils {

    /**
     * Original source taken from https://www.geodatasource.com/developers/java.
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @param unit
     * @return
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    /**
     * Bearing calculation adapted from
     *  https://gis.stackexchange.com/questions/252672/calculate-bearing-between-two-decimal-gps-coordinates-arduino-c
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return Bearing in degrees
     */
    public static int bearing(double lat1, double lon1, double lat2, double lon2) {

        double teta1 = Math.toRadians(lat1);
        double teta2 = Math.toRadians(lat2);
        double delta1 = Math.toRadians(lat2-lat1);
        double delta2 = Math.toRadians(lon2-lon1);

        double y = Math.sin(delta2) * Math.cos(teta2);
        double x = Math.cos(teta1)*Math.sin(teta2) - Math.sin(teta1)*Math.cos(teta2)*Math.cos(delta2);
        double bearingRadians = Math.atan2(y,x);
        double bearingDegrees = Math.toDegrees(bearingRadians);
        return ((int)bearingDegrees + 360) % 360;
    }
}
