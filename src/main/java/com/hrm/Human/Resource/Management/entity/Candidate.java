package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "candidates")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String candidateName;

    @Column
    private String email;

    @ManyToMany(mappedBy = "candidates")
    private List<RecruitmentProcess> recruitmentProcesses = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "skill_candidate",
            joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<SkillType> listSkill;

    public Candidate() {}

    public Candidate(String candidateName, List<SkillType> listSkill) {
        this.candidateName = candidateName;
        this.listSkill = listSkill;
    }
}
