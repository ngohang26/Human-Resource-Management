package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.Candidate;
import com.hrm.Human.Resource.Management.response.CandidateResponse;
import com.hrm.Human.Resource.Management.service.impl.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {


    @Autowired
    private CandidateService candidateService;

    @GetMapping(path = "getAllCandidates")
    public List<Candidate> getAllCandidates() {
        return candidateService.getCandidates();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Candidate> getCandidateById(@PathVariable Long id) {
        Optional<Candidate> candidate = candidateService.findById(id);
        return candidate.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping("/addPosition")
    public ResponseEntity<?> addCandidate(@RequestBody Candidate candidate) {
        return candidateService.addCandidate(candidate);
    }

    @PutMapping("/#/{id}")
    public Candidate updateCandidate(@PathVariable Long id, @RequestBody Candidate candidateDetails) {
        return candidateService.updateCandidate(id, candidateDetails);
    }


    @DeleteMapping("/hardDelete/{id}")
    public ResponseEntity<CandidateResponse> hardDeleteCandidate(@PathVariable Long id) {
        return candidateService.hardDeleteCandidate(id);
    }
}

