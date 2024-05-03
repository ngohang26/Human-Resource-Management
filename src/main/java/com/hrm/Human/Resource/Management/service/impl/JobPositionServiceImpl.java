package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.JobPosition;
import com.hrm.Human.Resource.Management.repositories.JobPositionRepositories;
import com.hrm.Human.Resource.Management.response.ErrorResponse;
import com.hrm.Human.Resource.Management.service.JobPositionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobPositionServiceImpl implements JobPositionService {
    @Autowired
    private JobPositionRepositories jobPositionRepositories;

    @Override
    public Optional<JobPosition> findById(Long id) {
        return jobPositionRepositories.findById(id);
    }

    @Override
    public List<JobPosition> getJobPositionName() {
        return jobPositionRepositories.findAll();
    }

    @Override
    public ResponseEntity<?> addJobPosition(JobPosition jobPosition) {
        Optional<JobPosition> existingJobPosition = jobPositionRepositories.findByJobPositionNameContaining(jobPosition.getJobPositionName());
        if (existingJobPosition.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The jobPosition already exists.");
        }

        try {
            JobPosition savedJobPosition = jobPositionRepositories.save(jobPosition);
            return new ResponseEntity<>(savedJobPosition, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public JobPosition updateJobPosition(Long id, JobPosition updatedJobPosition) {
        JobPosition existingJobPosition = jobPositionRepositories.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JobPosition not found with id " + id));

        existingJobPosition.setJobPositionName(updatedJobPosition.getJobPositionName());
        existingJobPosition.setJobDescription(updatedJobPosition.getJobDescription());
        existingJobPosition.setSkillsRequired(updatedJobPosition.getSkillsRequired());
        existingJobPosition.setApplicationDeadline(updatedJobPosition.getApplicationDeadline());
        return jobPositionRepositories.save(existingJobPosition);
    }

    @Override
    public ResponseEntity<ErrorResponse> hardDeleteJobPosition(Long id) {
        boolean exists = jobPositionRepositories.existsById(id);
        if (exists) {
            jobPositionRepositories.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ErrorResponse("ok", "Delete jobPosition successfully", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse("failed", "Cannot find jobPosition to delete", "")
        );
    }
}
