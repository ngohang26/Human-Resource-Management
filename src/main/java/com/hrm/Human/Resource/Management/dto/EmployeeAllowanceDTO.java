package com.hrm.Human.Resource.Management.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeAllowanceDTO {
    private Long allowanceId;
    private String employeeCode;
    private String allowanceName;
    private BigDecimal allowanceAmount;
    private LocalDate startDate;
    private LocalDate endDate;

}
