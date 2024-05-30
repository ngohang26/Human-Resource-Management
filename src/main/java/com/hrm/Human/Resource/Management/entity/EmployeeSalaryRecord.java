package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class EmployeeSalaryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
    private String employeeCode;
    private String fullName;
    private String departmentName;
    private String positionName;
    private Long totalWorkingHours;
    private int year;
    private int month;
    private BigDecimal monthlySalary;
    private BigDecimal totalAllowance;
    private BigDecimal incomeTax;
    private int workingDaysInMonth;
    private BigDecimal totalIncome;
    private BigDecimal socialInsurance;
    private BigDecimal healthInsurance;
    private BigDecimal unemploymentInsurance;
    private BigDecimal totalInsurance;
    private BigDecimal totalDeductions;
    private BigDecimal overTimeSalary;
    private Long totalOvertimeHours;
    private BigDecimal netSalary;

}


