package com.hrm.Human.Resource.Management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrm.Human.Resource.Management.dto.CandidateDTO;
import com.hrm.Human.Resource.Management.dto.CandidateStatusCount;
import com.hrm.Human.Resource.Management.dto.CandidateUpdateDTO;
import com.hrm.Human.Resource.Management.entity.Candidate;
import com.hrm.Human.Resource.Management.service.impl.CandidateService;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/candidates")
public class CandidateController {
    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PreAuthorize("hasAuthority('ADD_CANDIDATE')")
    @PostMapping("/addCandidate")
    public CandidateDTO createCandidate(@RequestBody Candidate candidate) {
        return candidateService.createCandidate(candidate);
    }

    @PreAuthorize("hasAuthority('ADD_CANDIDATE')")
    @PostMapping("/{id}/updateStatus")
    public ResponseEntity<String> updateCandidateStatus(@PathVariable Long id, @RequestBody CandidateUpdateDTO candidateUpdateDTO) {
        return candidateService.updateCandidateStatus(id, candidateUpdateDTO.getNewStatus(), candidateUpdateDTO.getCandidateDetails(), candidateUpdateDTO.getIdentityCardNumber());
    }

    @PreAuthorize("hasAuthority('ADD_CANDIDATE')")
    @GetMapping("/{id}")
    public CandidateDTO getCandidate(@PathVariable Long id) {
        return candidateService.getCandidateById(id);
    }

    @PreAuthorize("hasAuthority('ADD_CANDIDATE') or hasAuthority('VIEW_EMPLOYEE')")
    @GetMapping(path = "/getAllCandidates")
    public List<CandidateDTO> getAllCandidate() {
        return candidateService.getCandidates();
    }

    @PreAuthorize("hasAuthority('ADD_CANDIDATE')")
    @PutMapping("update/{id}")
    public CandidateDTO updateCandidate(@PathVariable Long id, @RequestBody CandidateDTO candidateDetailsDTO) {
        return candidateService.updateCandidateInfo(id, candidateDetailsDTO);
    }

    @PreAuthorize("hasAuthority('ADD_CANDIDATE')")
    @GetMapping("/getCandidateCountByStatus")
    public ResponseEntity<?> getCandidateCountByStatus() {
        try {
            Map<Candidate.CandidateStatus, CandidateStatusCount> countByStatus = candidateService.getCandidateCountByStatus();
            return new ResponseEntity<>(countByStatus, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('ADD_CANDIDATE')")
    @GetMapping("/getCandidateCountByStatusAndMonth/{year}/{month}")
    public ResponseEntity<?> getCandidateCountByStatusAndMonth(@PathVariable int year, @PathVariable int month) {
        try {
            Map<Candidate.CandidateStatus, CandidateStatusCount> countByStatus = candidateService.getCandidateCountByStatusAndMonth(year, month);
            return new ResponseEntity<>(countByStatus, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('ADD_CANDIDATE')")
    @GetMapping("/getCandidatesHiredByMonth/{year}/{month}")
    public ResponseEntity<?> getCandidatesHiredByMonth(@PathVariable int year, @PathVariable int month) {
        try {
            Map<String, Long> candidatesHiredByMonth = candidateService.getCandidatesHiredByMonth(year, month);
            return new ResponseEntity<>(candidatesHiredByMonth, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}