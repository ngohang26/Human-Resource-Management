package com.hrm.Human.Resource.Management.dto;

import com.hrm.Human.Resource.Management.entity.Contract;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeContractDTO {
    private Long id;
    private String employeeCode;
    private String fullName;
    private String departmentName;
    private String positionName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate signDate;
    private String noteContract;
    private int numberOfSignatures;
    private String contractCode;
    private BigDecimal monthlySalary;
}
