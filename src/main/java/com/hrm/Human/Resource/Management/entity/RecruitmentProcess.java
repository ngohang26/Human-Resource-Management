package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "recruitment_process")
public class RecruitmentProcess {
        @Id
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        private Long id;

        @Enumerated(EnumType.STRING)
        @Column(length = 20)
        private RecruitmentStatus status;

        public enum RecruitmentStatus {
                REFUSE,
                NEW,
                INITIAL_REVIEW,
                FIRST_INTERVIEW,
                OFFER_MADE,
                CONTRACT_SIGNED
        }

        @ManyToMany
        @JoinTable(
                name = "recruitment_candidate",
                joinColumns = @JoinColumn(name = "recruitment_id"),
                inverseJoinColumns = @JoinColumn(name = "candidate_id"))
        private List<Candidate> candidates = new ArrayList<>();

        public void setStatus(String status) {
        }
}
