package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.JobPosition;
import com.hrm.Human.Resource.Management.repositories.JobPositionRepositories;
import com.hrm.Human.Resource.Management.response.ErrorResponse;
import com.hrm.Human.Resource.Management.service.JobPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jobPositions")
public class JobPositionController {
    @Autowired
    private JobPositionRepositories jobPositionRepositories;

    @Autowired
    private JobPositionService jobPositionService;

    @PreAuthorize("hasAuthority('ADD_CANDIDATE')")
    @GetMapping(path = "getAllJobPositions")
    public List<JobPosition> getAllJobPositions() {
        return jobPositionService.getJobPositionName();
    }

    @PreAuthorize("hasAuthority('ADD_CANDIDATE')")
    @GetMapping(path = "/{id}")
    public ResponseEntity<JobPosition> getJobPositionById(@PathVariable Long id) {
        Optional<JobPosition> jobPosition = jobPositionService.findById(id);
        return jobPosition.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PreAuthorize("hasAuthority('ADD_CANDIDATE')")
    @PostMapping("/addJobPosition")
    public ResponseEntity<?> addJobPosition(@RequestBody JobPosition jobPosition) {
        return jobPositionService.addJobPosition(jobPosition);
    }

    @PreAuthorize("hasAuthority('ADD_CANDIDATE')")
    @PutMapping("/update/{id}")
    public JobPosition updateJobPosition(@PathVariable Long id, @RequestBody JobPosition jobPositionDetails) {
        return jobPositionService.updateJobPosition(id, jobPositionDetails);
    }


    @PreAuthorize("hasAuthority('ADD_CANDIDATE')")
    @DeleteMapping("/hardDelete/{id}")
    public ResponseEntity<ErrorResponse> hardDeleteJobPosition(@PathVariable Long id) {
        return jobPositionService.hardDeleteJobPosition(id);
    }
}
