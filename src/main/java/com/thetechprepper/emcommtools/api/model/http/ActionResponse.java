package com.thetechprepper.emcommtools.api.model.http;

public class ActionResponse {

    private int status;
    private String message;

    public ActionResponse() {
    }

    public ActionResponse(int status, String message) {
        this.status = status;
	this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
