package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface CandidateRepositories extends JpaRepository<Candidate, Long> {
    List<Candidate> findByCandidateName(String candidateName);

    Optional<Candidate> findByCandidateNameContaining(String keyword);

    List<Candidate> findAllByCurrentStatusAndDateAppliedBetween(Candidate.CandidateStatus candidateStatus, LocalDate startOfMonth, LocalDate endOfMonth);
}
