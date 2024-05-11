package com.hrm.Human.Resource.Management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "experiences")
public class Experiences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "experience_name_id")
    private ExperienceName experienceName;

    @Column
    private Integer rating;

    @JsonIgnore
    @ManyToMany(mappedBy = "experiences")
    private List<Employee> employees = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "experiences")
    private List<Candidate> candidates = new ArrayList<>();
}