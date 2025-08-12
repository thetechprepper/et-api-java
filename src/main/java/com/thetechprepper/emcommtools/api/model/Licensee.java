package com.thetechprepper.emcommtools.api.model;

public class Licensee {

    private String callsign;
    private String firstName;
    private String city;
    private String zip;
    private String state;
    private String grid;
    private Double distance;
    private Integer bearing;
    private Double lat;
    private Double lon;
    private Double alt;

    protected Licensee () {
    }

    public static Licensee newInstance() {
        return new Licensee();
    }

    public String getCallsign() {
        return callsign;
    }

    public Licensee withCallsign(String callsign) {
        this.callsign = callsign;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public Licensee withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Licensee withCity(String city) {
        this.city = city;
        return this;
    }

    public String getZip() {
        return zip;
    }

    public Licensee withZip(String zip) {
        this.zip = zip;
        return this;
    }

    public String getState() {
        return state;
    }

    public Licensee withState(String state) {
        this.state = state;
        return this;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getDistance() {
        return distance;
    }

    public Licensee withDistance(Double distance) {
        this.distance = distance;
        return this;
    }

    public Integer getBearing() {
        return bearing;
    }

    public void setBearing(Integer bearing) {
        this.bearing = bearing;
    }

    public void setGrid(String grid) {
        this.grid = grid;
    }

    public String getGrid() {
        return grid;
    }

    public Licensee withGrid(String grid) {
        this.grid = grid;
        return this;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLat() {
        return lat;
    }

    public Licensee withLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLon() {
        return lon;
    }

    public Licensee withLon(Double lon) {
        this.lon = lon;
        return this;
    }

    public Double getAlt() {
        return alt;
    }

    public void setAlt(Double alt) {
        this.alt = alt;
    }

    public Licensee withAlt(Double alt) {
        this.alt = alt;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Licensee{");
        sb.append("callsign='").append(callsign).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", zip='").append(zip).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", grid='").append(grid).append('\'');
        sb.append(", distance=").append(distance).append('\'');
        sb.append(", bearing=").append(bearing).append('\'');
        sb.append(", alt=").append(alt);
        sb.append('}');
        return sb.toString();
    }
}
