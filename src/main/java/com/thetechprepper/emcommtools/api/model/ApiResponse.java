package com.thetechprepper.emcommtools.api.model;

public abstract class ApiResponse
{
    ApiStatus status;

    public ApiStatus getStatus()
    {
        return status;
    }

    public void setStatus(ApiStatus status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("ApiResponse{");
        sb.append("status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
