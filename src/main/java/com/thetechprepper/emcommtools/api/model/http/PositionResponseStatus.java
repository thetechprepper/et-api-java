package com.thetechprepper.emcommtools.api.model.http;

import com.thetechprepper.emcommtools.api.model.position.Position;

public class PositionResponseStatus {

    private int httpStatus;
    private Boolean ready = false;
    private Position position;

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Boolean getReady() {
        return ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }
}
