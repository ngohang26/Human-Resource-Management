package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Table(name = "candidates")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String candidateName;

    @Column(nullable = false)
    private String email;

    @Column
    private LocalDate birthDate;

    @Column(nullable = false)
    @Pattern(regexp="(^0|[0-9]{10})", message="Phone number must be a 10-digit number")
    private String phoneNumber;

    @Column
    private LocalDateTime interviewTime;

    @Column
    private String[] certificates;

    // education
    @Column
    private String certificateLevel;

    @Column
    private String fieldOfStudy;

    @Column
    private String school;

    @ManyToOne
    private JobPosition jobPosition;

    public enum CandidateStatus {
        NEW,
        REFUSE,
        INITIAL_REVIEW,
        FIRST_INTERVIEW,
        OFFER_MADE,
        CONTRACT_SIGNED
    }

    @Enumerated(EnumType.STRING)
    private CandidateStatus currentStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_offer_id", referencedColumnName = "id")
    private JobOffer jobOffer;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>();
}
