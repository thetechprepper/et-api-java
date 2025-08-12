package com.thetechprepper.emcommtools.api.model;

import com.google.gson.annotations.SerializedName;

public class User {
    private String callsign;
    private String grid;
    @SerializedName("winlinkPasswd")
    private String winlinkPassword;

    public User() {
    }

    public User withCallsign(String callsign) {
        this.callsign = callsign;
        return this;
    }

    public User withGrid(String grid) {
        this.grid = grid;
        return this;
    }

    public User withWinlinkPassword(String winlinkPassword) {
        this.winlinkPassword = winlinkPassword;
        return this;
    }

    public String getCallsign() {
        return callsign;
    }

    public String getGrid() {
        return grid;
    }

    public String getWinlinkPassword() {
        return winlinkPassword;
    }

    @Override
    public String toString() {
        return "User [callsign=" + callsign + ", grid=" + grid + ", winlinkPassword=" + winlinkPassword + "]";
    }
}
