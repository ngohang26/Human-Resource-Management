package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.Candidate;
import com.hrm.Human.Resource.Management.entity.JobOffer;
import com.hrm.Human.Resource.Management.service.impl.CandidateService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/candidates")
public class CandidateController {
    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping("/addCandidate")
    public Candidate createCandidate(@RequestBody Candidate candidate) {
        return candidateService.createCandidate(candidate);
    }

    @PostMapping("/{id}/updateStatus")
    public Candidate updateCandidateStatus(@PathVariable Long id, @RequestBody Candidate.CandidateStatus newStatus) {
        return candidateService.updateCandidateStatus(id, newStatus);
    }

    @PostMapping("/{id}/setInterviewTime")
    public Candidate setInterviewTime(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return candidateService.setInterviewTime(id, payload);
    }

    @PostMapping("/{id}/makeOffer")
    public Candidate makeOffer(@PathVariable Long id, @RequestBody JobOffer jobOffer) {
        return candidateService.makeOffer(id, jobOffer);

    }
}