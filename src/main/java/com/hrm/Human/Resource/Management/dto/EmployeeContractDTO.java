package com.hrm.Human.Resource.Management.dto;

import com.hrm.Human.Resource.Management.entity.Department;
import com.hrm.Human.Resource.Management.entity.Position;
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
    private Position position;
    private Department department;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate signDate;
    private String noteContract;
    private int numberOfSignatures;
    private String contractCode;
    private BigDecimal monthlySalary;
}
