package com.hrm.Human.Resource.Management.response;

import lombok.Getter;

@Getter
public class UploadResponse {
    private String status;
    private String message;
    private Object generatedFileName;

    public UploadResponse(String status, String message, Object generatedFileName) {
        this.status = status;
        this.message = message;
        this.generatedFileName = generatedFileName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setGeneratedFileName(Object generatedFileName) {
        this.generatedFileName = generatedFileName;
    }
}
