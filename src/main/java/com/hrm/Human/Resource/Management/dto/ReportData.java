package com.hrm.Human.Resource.Management.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReportData {
    private int employeeCount;
    private double employeePercentageChange;
    private BigDecimal totalSalary;
    private double salaryPercentageChange;
    private long totalWorkHours;
    private double workHoursPercentageChange;

}