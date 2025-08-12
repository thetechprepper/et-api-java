package com.thetechprepper.emcommtools.api.model;

public class Zip2Geo {

    private String zip;
    private Double lat;
    private Double lon;
    private Double alt;

    protected Zip2Geo() {
    }

    public static Zip2Geo newInstance() {
        return new Zip2Geo();
    }

    public String getZip() {
        return zip;
    }

    public Zip2Geo withZip(String zip) {
        this.zip = zip;
        return this;
    }

    public Double getLat() {
        return lat;
    }

    public Zip2Geo withLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public Double getLon() {
        return lon;
    }

    public Zip2Geo withLon(Double lon) {
        this.lon = lon;
        return this;
    }

    public Double getAlt() {
        return alt;
    }

    public Zip2Geo withAlt(Double alt) {
        this.alt = alt;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Zip2Geo{");
        sb.append("zip='").append(zip).append('\'');
        sb.append(", lat=").append(lat);
        sb.append(", lon=").append(lon);
        sb.append(", alt=").append(alt);
        sb.append('}');
        return sb.toString();
    }
}
