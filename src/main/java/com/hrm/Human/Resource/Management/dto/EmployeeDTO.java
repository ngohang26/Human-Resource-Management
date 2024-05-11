package com.hrm.Human.Resource.Management.dto;

import com.hrm.Human.Resource.Management.entity.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Long id;
    private String employeeCode;
    private String fullName;
    private String phoneNumber;
    private String workEmail;
    private Position position;
    private Department department;
    private String image;
    private String phoneContactER;
    private String nameContactER;
    @OneToOne(cascade = CascadeType.ALL)
    private PersonalInfo personalInfo;
    private List<Skills> skills;
    private List<Experiences> experiences;
}


