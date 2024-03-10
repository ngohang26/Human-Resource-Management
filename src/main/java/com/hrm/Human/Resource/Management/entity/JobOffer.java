package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.Temporal;
@Getter
@Setter
@Entity
@Table(name = "contract_proposal")
public class JobOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column
    private String noteContract;

    @Column(nullable = false)
    private Double monthlySalary;

//    @OneToOne(mappedBy = "jobOffer")
//    private Candidate candidate;
}
