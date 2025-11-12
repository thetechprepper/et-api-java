package com.thetechprepper.emcommtools.api.model;

public class VoacapRequest {

    private String txLatLon;
    private String rxLatLon;
    private int power;
    private String mode;

    public String getTxLatLon() {
        return txLatLon;
    }

    public void setTxLatLon(String txLatLon) {
        this.txLatLon = txLatLon;
    }

    public String getRxLatLon() {
        return rxLatLon;
    }

    public void setRxLatLon(String rxLatLon) {
        this.rxLatLon = rxLatLon;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
