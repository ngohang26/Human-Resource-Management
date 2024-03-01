package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;

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
    private Double bonus; // Tiền thưởng

    @Column
    private Double tax; // Trừ thuế

    public Payroll() {
    }

    public void calculateTax(boolean isResident, LocalDate contractStartDate, LocalDate contractEndDate) {
        double totalAllowance = allowances.stream().mapToDouble(Allowance::getAllowanceAmount).sum();
        double taxableIncome = baseSalary + totalAllowance + bonus;
        if (isResident) {
            long contractDuration = ChronoUnit.MONTHS.between(contractStartDate, contractEndDate);
            if (contractDuration >= 3) {
                if (taxableIncome > 0 && taxableIncome <= 5000000) {
                    tax = taxableIncome * 0.05;
                } else if (taxableIncome > 5000000 && taxableIncome <= 10000000) {
                    tax = taxableIncome * 0.1 - 250000;
                } else if (taxableIncome > 10000000 && taxableIncome <= 18000000) {
                    tax = taxableIncome * 0.15 - 750000;
                } else if (taxableIncome > 18000000 && taxableIncome <= 32000000) {
                    tax = taxableIncome * 0.2 - 1650000;
                } else if (taxableIncome > 32000000 && taxableIncome <= 52000000) {
                    tax = taxableIncome * 0.25 - 3250000;
                } else if (taxableIncome > 52000000 && taxableIncome <= 80000000) {
                    tax = taxableIncome * 0.3 - 5850000;
                } else {
                    tax = taxableIncome * 0.35 - 9850000;
                }
            } else {
                if (taxableIncome > 2000000) {
                    tax = taxableIncome * 0.1;
                } else {
                    tax = 0.0;
                }
            }
        } else {
            tax = taxableIncome * 0.2;
        }
    }

}

