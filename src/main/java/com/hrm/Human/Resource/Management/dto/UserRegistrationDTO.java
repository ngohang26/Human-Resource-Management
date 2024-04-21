package com.hrm.Human.Resource.Management.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserRegistrationDTO {
    private String username;
    private String password;
    private String email;
    private Long roleId;
    private Set<Long> permissionIds;

}

