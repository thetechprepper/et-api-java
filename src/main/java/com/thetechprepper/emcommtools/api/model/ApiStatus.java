package com.thetechprepper.emcommtools.api.model;

public class ApiStatus
{
    private Boolean success;
    private String msg;

    public ApiStatus()
    {
        this.success = false;
        this.msg = "";
    }

    public Boolean getSuccess()
    {
        return success;
    }

    public void setSuccess(Boolean success)
    {
        this.success = success;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("ApiStatus{");
        sb.append("success=").append(success);
        sb.append(", msg='").append(msg).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
