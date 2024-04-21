package com.hrm.Human.Resource.Management.dto;

import com.hrm.Human.Resource.Management.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEmployeeDTO {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String image;
    private String fullName;
    private String positionName;

}

