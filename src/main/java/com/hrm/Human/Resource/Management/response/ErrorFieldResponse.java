package com.hrm.Human.Resource.Management.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorFieldResponse {
    private String status;
    private String field;
    private String message;

    public ErrorFieldResponse(String status, String field, String message) {
        this.status = status;
        this.field = field;
        this.message = message;
    }

}
