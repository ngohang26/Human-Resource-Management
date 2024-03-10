package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.Temporal;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_code", referencedColumnName = "employeeCode")
    private Employee employee;

    public Temporal getStartDate() {
        return startDate;
    }

    public Temporal getEndDate() {
        return endDate;
    }


    @Column(nullable = false)
    private Double monthlySalary;
}
