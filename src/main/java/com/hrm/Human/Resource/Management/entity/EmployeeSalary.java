package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
@Getter
@Setter
public class EmployeeSalary {
    private String employeeCode;
    private String employeeName;
    private BigDecimal monthlySalary;
    private BigDecimal totalAllowance;
    private BigDecimal incomeTax;
    private int workingDaysInMonth;
    private BigDecimal totalIncome;
}

