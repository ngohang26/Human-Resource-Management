package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "skill_type_id", nullable = false)
    private SkillType skillType;

    @Column
    private String nameSkill;

    @ElementCollection
    private List<String> level;
}
