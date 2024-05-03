package com.hrm.Human.Resource.Management.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "experiences")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String jobTitle;

    @Column
    private String company;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @ManyToMany(mappedBy = "experiences")
    private List<Employee> employees = new ArrayList<>();

    @ManyToMany(mappedBy = "experiences")
    private List<Candidate> candidates = new ArrayList<>();
}