package com.thetechprepper.emcommtools.api.model;

public class NWSZoneCounty {

    private String state;

    private String county;

    private Double lat;

    private Double lon;

    private String name;

    private String zone;

    protected NWSZoneCounty() {
    }

    public static NWSZoneCounty newInstance() {
        return new NWSZoneCounty();
    }

    public NWSZoneCounty withState(String state) {
        this.state = state;
        return this;
    }

    public NWSZoneCounty withCounty(String county) {
        this.county = county;
        return this;
    }

    public NWSZoneCounty withLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public NWSZoneCounty withLon(Double lon) {
        this.lon = lon;
        return this;
    }

    public NWSZoneCounty withName(String name) {
        this.name = name;
        return this;
    }

    public NWSZoneCounty withZone(String zone) {
        this.zone = zone;
        return this;
    }

    // Getters

    public String getState() {
        return state;
    }

    public String getCounty() {
        return county;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getName() {
        return name;
    }

    public String getZone() {
        return zone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NWSZoneCounty{");
        sb.append("state='").append(state).append('\'');
        sb.append(", county='").append(county).append('\'');
        sb.append(", lat='").append(lat).append('\'');
        sb.append(", lon=").append(lon);
        sb.append(", name='").append(name).append('\'');
        sb.append(", zone='").append(zone).append('\'');
        sb.append(", url='").append(getUrl()).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getUrl() {
        StringBuilder sb = new StringBuilder();
	sb.append("https://tgftp.nws.noaa.gov/data/forecasts/state/");
	sb.append(state.toLowerCase());
	sb.append("/");
	sb.append(state.toLowerCase());
	sb.append("z");
	sb.append(zone);
	sb.append(".txt");
        return sb.toString();
    }

}
