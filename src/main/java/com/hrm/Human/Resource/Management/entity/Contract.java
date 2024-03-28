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

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

}
