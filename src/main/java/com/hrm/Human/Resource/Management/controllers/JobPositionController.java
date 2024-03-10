package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.JobPosition;
import com.hrm.Human.Resource.Management.repositories.JobPositionRepositories;
import com.hrm.Human.Resource.Management.response.JobPositionResponse;
import com.hrm.Human.Resource.Management.service.JobPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/job_position")
public class JobPositionController {
    @Autowired
    private JobPositionRepositories jobPositionRepositories;

    @Autowired
    private JobPositionService jobPositionService;

    @GetMapping(path = "getAllJobPositions")
    public List<JobPosition> getAllJobPositions() {
        return jobPositionService.getJobPositionName();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<JobPosition> getJobPositionById(@PathVariable Long id) {
        Optional<JobPosition> jobPosition = jobPositionService.findById(id);
        return jobPosition.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping("/addJobPosition")
    public ResponseEntity<?> addJobPosition(@RequestBody JobPosition jobPosition) {
        return jobPositionService.addJobPosition(jobPosition);
    }

    @PutMapping("/#/{id}")
    public JobPosition updateJobPosition(@PathVariable Long id, @RequestBody JobPosition jobPositionDetails) {
        return jobPositionService.updateJobPosition(id, jobPositionDetails);
    }


    @DeleteMapping("/hardDelete/{id}")
    public ResponseEntity<JobPositionResponse> hardDeleteJobPosition(@PathVariable Long id) {
        return jobPositionService.hardDeleteJobPosition(id);
    }
}
