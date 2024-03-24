package com.hrm.Human.Resource.Management.response;

import lombok.Getter;

@Getter
public class AllowanceResponse {
    private String status;
    private String message;
    private Object data;

    public AllowanceResponse(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
