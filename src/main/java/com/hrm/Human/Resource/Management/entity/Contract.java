package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "contracts")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String contractCode;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDate signDate;

    @Column
    private String noteContract;

    @Column(nullable = false)
    private int numberOfSignatures;

    @Column(nullable = false)
    private BigDecimal monthlySalary;

    public Contract() {}
    @PostPersist
    public void generateContractCode() {
        LocalDate date = LocalDate.now();
        int year = date.getYear() % 100;
        int month = date.getMonthValue();
        this.contractCode = String.format("%03d/%02d/%02d" + "-HĐLĐ", id, month, year);
    }

    public void setEmployee(Employee employee) {
    }
}
