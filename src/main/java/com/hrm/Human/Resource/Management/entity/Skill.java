package com.hrm.Human.Resource.Management.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String proficiency; // e.g. Beginner, Intermediate, Advanced, Expert

    @ManyToMany(mappedBy = "skills")
    private List<Employee> employees = new ArrayList<>();

    @ManyToMany(mappedBy = "skills")
    private List<Candidate> candidates = new ArrayList<>();
}
