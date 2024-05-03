package com.hrm.Human.Resource.Management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailChangeRequest {
    private String username;
    private String password;
    private String newEmail;
}
