package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = false)
    @Size(min = 2, max = 50, message = "Họ tên cần phải lớn hơn 2 ký tự và ít hơn 50 ký tự")
    @NotNull(message = "Họ tên không thể trống")
    private String candidateName;

    @Column(nullable = false)
    @Email(message = "Email không đúng định dạng username@domain.com")
    private String email;

    @Column
    private LocalDate dateApplied;

    @Column(nullable = false)
    private String resumeFilePath;

    @Column
    private LocalDate birthDate;

    @Column(nullable = false, unique = true)
    @Pattern(regexp="(^0|[0-9]{10})", message="Số điện thoại cần có 10 chữ số và bắt đầu bằng số 0")
    @NotNull(message = "Số điện thoại không thể trống")
    private String phoneNumber;

    @Column
    private LocalDateTime interviewTime;

    @Column
    private LocalDateTime secondInterviewTime;
    @Column
    private String[] certificates;

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
        SECOND_INTERVIEW,
        OFFER_MADE,
        CONTRACT_SIGNED
    }

    @Enumerated(EnumType.STRING)
    private CandidateStatus currentStatus;

    public enum InterviewStatus {
        PASSED,
        FAILED,
        PENDING,
        NOT_APPLICABLE
    }

    @Enumerated(EnumType.STRING)
    private InterviewStatus firstInterviewStatus;

    @Enumerated(EnumType.STRING)
    private InterviewStatus secondInterviewStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_offer_id", referencedColumnName = "id")
    private JobOffer jobOffer;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "candidate_skill",
            joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skills> skills = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "candidate_experience",
            joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "experience_id"))
    private List<Experiences> experiences = new ArrayList<>();
}
