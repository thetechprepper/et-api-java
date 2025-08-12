package com.thetechprepper.emcommtools.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Aircraft {

    @JsonProperty("tail_number")
    private String tailNumber;

    private String make;

    private String model;

    private Integer year;

    @JsonProperty("owner_name")
    private String ownerName;

    private String city;

    private String state;

    private String icao24;

    @JsonProperty("registrant_type")
    private String registrantType;

    protected Aircraft() {
    }

    public static Aircraft newInstance() {
        return new Aircraft();
    }

    public Aircraft withTailNumber(String tailNumber) {
        this.tailNumber = tailNumber;
        return this;
    }

    public Aircraft withMake(String make) {
        this.make = make;
        return this;
    }

    public Aircraft withModel(String model) {
        this.model = model;
        return this;
    }

    public Aircraft withIcao24(String icao24) {
        this.icao24 = icao24;
        return this;
    }

    public Aircraft withYear(Integer year) {
        this.year = year;
        return this;
    }

    public Aircraft withOwnerName(String ownerName) {
        this.ownerName = ownerName;
        return this;
    }

    public Aircraft withCity(String city) {
        this.city = city;
        return this;
    }

    public Aircraft withState(String state) {
        this.state = state;
        return this;
    }

    public Aircraft withRegistrantType(String registrantType) {
        this.registrantType = registrantType;
        return this;
    }

    // Getters

    public String getTailNumber() {
        return tailNumber;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public Integer getYear() {
        return year;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getIcao24() {
        return icao24;
    }

    public String getRegistrantType() {
        return registrantType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Aircraft{");
        sb.append("tailNumber='").append(tailNumber).append('\'');
        sb.append(", make='").append(make).append('\'');
        sb.append(", model='").append(model).append('\'');
        sb.append(", year=").append(year);
        sb.append(", ownerName='").append(ownerName).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", icao24='").append(icao24).append('\'');
        sb.append(", registrantType='").append(registrantType).append('\'');
        sb.append('}');
        return sb.toString();
    }
}