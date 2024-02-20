package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "skill_types")
public class SkillType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nameType;


}
