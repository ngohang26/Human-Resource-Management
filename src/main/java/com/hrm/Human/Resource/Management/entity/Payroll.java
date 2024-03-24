package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Table(name = "payroll")
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_code", referencedColumnName = "employeeCode")
    private Employee employee;

    @Column
    private Double baseSalary; // Lương cơ bản

    @OneToMany
    @JoinColumn(name = "allowance_id", referencedColumnName = "id")
    private List<Allowance> allowances; // Phụ cấp

    @Column
    private BigDecimal bonus; // Tiền thưởng

    @Column
    private BigDecimal tax; // Trừ thuế

    public Payroll() {
    }

    public void calculateTax(boolean isResident, LocalDate contractStartDate, LocalDate contractEndDate) {
        BigDecimal totalAllowance = allowances.stream()
                .map(Allowance::getAllowanceAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxableIncome = BigDecimal.valueOf(baseSalary).add(totalAllowance).add(bonus);
        BigDecimal taxRate;
        BigDecimal taxDeduction;
        if (isResident) {
            long contractDuration = ChronoUnit.MONTHS.between(contractStartDate, contractEndDate);
            if (contractDuration >= 3) {
                if (taxableIncome.compareTo(BigDecimal.valueOf(5000000)) <= 0) {
                    taxRate = new BigDecimal("0.05");
                    taxDeduction = BigDecimal.ZERO;
                } else if (taxableIncome.compareTo(BigDecimal.valueOf(10000000)) <= 0) {
                    taxRate = new BigDecimal("0.1");
                    taxDeduction = BigDecimal.valueOf(250000);
                } else if (taxableIncome.compareTo(BigDecimal.valueOf(18000000)) <= 0) {
                    taxRate = new BigDecimal("0.15");
                    taxDeduction = BigDecimal.valueOf(750000);
                } else if (taxableIncome.compareTo(BigDecimal.valueOf(32000000)) <= 0) {
                    taxRate = new BigDecimal("0.2");
                    taxDeduction = BigDecimal.valueOf(1650000);
                } else if (taxableIncome.compareTo(BigDecimal.valueOf(52000000)) <= 0) {
                    taxRate = new BigDecimal("0.25");
                    taxDeduction = BigDecimal.valueOf(3250000);
                } else if (taxableIncome.compareTo(BigDecimal.valueOf(80000000)) <= 0) {
                    taxRate = new BigDecimal("0.3");
                    taxDeduction = BigDecimal.valueOf(5850000);
                } else {
                    taxRate = new BigDecimal("0.35");
                    taxDeduction = BigDecimal.valueOf(9850000);
                }
            } else {
                if (taxableIncome.compareTo(BigDecimal.valueOf(2000000)) > 0) {
                    taxRate = new BigDecimal("0.1");
                } else {
                    taxRate = BigDecimal.ZERO;
                }
                taxDeduction = BigDecimal.ZERO;
            }
        } else {
            taxRate = new BigDecimal("0.2");
            taxDeduction = BigDecimal.ZERO;
        }
        tax = taxableIncome.multiply(taxRate).subtract(taxDeduction);
    }

}

