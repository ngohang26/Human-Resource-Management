package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class EmployeeSalary {
    private Long id;
    private String employeeCode;
    private String employeeName;
    private BigDecimal monthlySalary;
    private BigDecimal totalAllowance;
    private BigDecimal incomeTax;
    private int workingDaysInMonth;
    private BigDecimal totalIncome;
    private Map<String, BigDecimal> allowances;
    private BigDecimal socialInsurance;
    private BigDecimal healthInsurance;
    private BigDecimal unemploymentInsurance;
    private BigDecimal totalInsurance;
    private BigDecimal totalDeductions;
    private BigDecimal overTimeSalary;
    private Long totalOvertimeHours;
    private BigDecimal netSalary;
    private String positionName;
    private String departmentName;
}

