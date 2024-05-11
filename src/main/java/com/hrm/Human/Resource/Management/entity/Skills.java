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
@Table(name = "skills")
public class Skills {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "skill_name_id")
    private SkillName skillName;

    @Column
    private Integer rating;

    @JsonIgnore
    @ManyToMany(mappedBy = "skills")
    private List<Employee> employees = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "skills")
    private List<Candidate> candidates = new ArrayList<>();
}
