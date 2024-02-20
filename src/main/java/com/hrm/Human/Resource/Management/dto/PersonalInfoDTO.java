package com.hrm.Human.Resource.Management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class PersonalInfoDTO {
    private Long id;
    private String nationality;
    private String birthPlace;
    private String residence;
    private LocalDate birthDate;
    private String identityCardNumber;
    private String personalEmail;
    private String[] certificates;

}
