package com.hrm.Human.Resource.Management.dto;

import com.hrm.Human.Resource.Management.entity.PersonalInfo;
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
    private String positionName;
    private String departmentName;
    private String image;
    private String phoneContactER;
    private String nameContactER;
    @OneToOne(cascade = CascadeType.ALL)
    private PersonalInfo personalInfo;
    private List<SkillDTO> skills;  // sử dụng DTO cho danh sách Skills
    private List<ExperienceDTO> experiences;
}


