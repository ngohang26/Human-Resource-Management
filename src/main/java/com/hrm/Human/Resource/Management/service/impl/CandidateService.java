package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.Candidate;
import com.hrm.Human.Resource.Management.repositories.CandidateRepositories;
import com.hrm.Human.Resource.Management.response.CandidateResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {
    @Autowired
    private CandidateRepositories candidateRepositories;


    public Optional<Candidate> findById(Long id) {
        return candidateRepositories.findById(id);
    }


    public List<Candidate> getCandidates() {return candidateRepositories.findAll();}

    public ResponseEntity<?> addCandidate(Candidate candidate) {
        Optional<Candidate> existingCandidate = candidateRepositories.findByCandidateNameContaining(candidate.getCandidateName());
        if (existingCandidate.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The candidate already exists.");
        }

        try {
            Candidate savedCandidate = candidateRepositories.save(candidate);
            return new ResponseEntity<>(savedCandidate, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Candidate updateCandidate(Long id, Candidate updatedCandidate) {
        Candidate existingCandidate = candidateRepositories.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id " + id));

        existingCandidate.setCandidateName(updatedCandidate.getCandidateName());

        return candidateRepositories.save(existingCandidate);
    }

    public ResponseEntity<CandidateResponse> hardDeleteCandidate(Long id){
        boolean exists = candidateRepositories.existsById(id);
        if (exists) {
            candidateRepositories.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new CandidateResponse("ok", "Delete candidate successfully", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new CandidateResponse("failed", "Cannot find candidate to delete", "")
        );
    }

}
