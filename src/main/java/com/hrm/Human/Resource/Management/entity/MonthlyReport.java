package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "monthly_report")
public class MonthlyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int employeeCount;

    @Column(nullable = false)
    private BigDecimal totalSalary;

    @Column(nullable = false)
    private long totalWorkHours;

    @Column(nullable = false)
    private double employeePercentageChange;

    @Column(nullable = false)
    private double salaryPercentageChange;

    @Column
    private double workHoursPercentageChange;
}

