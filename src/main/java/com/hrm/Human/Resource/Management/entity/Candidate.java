package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @Column
    private LocalDateTime interviewTime;

    @ManyToOne
    private JobPosition jobPosition;

    public enum CandidateStatus {
        NEW,
        REFUSE,
        INITIAL_REVIEW,
        FIRST_INTERVIEW,
        SECOND_INTERVIEW,
        OFFER_MADE,
        CONTRACT_SIGNED
    }

    @Enumerated(EnumType.STRING)
    private CandidateStatus currentStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_offer_id", referencedColumnName = "id")
    private JobOffer jobOffer;

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
