package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "job_position")
public class    JobPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String jobPositionName;

    @Column
    private String jobDescription;

    @Column
    private String skillsRequired;

    @Column
    private Date applicationDeadline;
}
