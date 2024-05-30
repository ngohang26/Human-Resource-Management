package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.dto.JobPositionDTO;
import com.hrm.Human.Resource.Management.entity.JobPosition;
import com.hrm.Human.Resource.Management.entity.Position;
import com.hrm.Human.Resource.Management.repositories.JobPositionRepositories;
import com.hrm.Human.Resource.Management.repositories.PositionRepositories;
import com.hrm.Human.Resource.Management.response.ErrorResponse;
import com.hrm.Human.Resource.Management.service.JobPositionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobPositionServiceImpl implements JobPositionService {
    @Autowired
    private JobPositionRepositories jobPositionRepositories;

    @Autowired
    PositionRepositories positionRepositories;

    @Override
    public Optional<JobPosition> findById(Long id) {
        return jobPositionRepositories.findById(id);
    }

    @Override
    public List<JobPosition> getJobPositionName() {
        return jobPositionRepositories.findAll();
    }

    @Override
    public List<JobPositionDTO> getAllJobPositions() {
        List<JobPosition> jobPositions = jobPositionRepositories.findAll();
        return jobPositions.stream().map(this::convertToJobPositionDTO).collect(Collectors.toList());
    }

    private JobPositionDTO convertToJobPositionDTO(JobPosition jobPosition) {
        JobPositionDTO jobPositionDTO = new JobPositionDTO();
        jobPositionDTO.setId(jobPosition.getId());
        jobPositionDTO.setPosition(jobPosition.getPosition());
        jobPositionDTO.setSkillsRequired(jobPosition.getSkillsRequired());
        jobPositionDTO.setJobPositionName(jobPosition.getJobPositionName());
        jobPositionDTO.setApplicationDeadline(jobPosition.getApplicationDeadline());
        jobPositionDTO.setCandidateCount(jobPosition.getCandidates().size());
        return jobPositionDTO;
    }

    @Override
    public ResponseEntity<?> addJobPosition(JobPosition jobPosition) {
        Position position = positionRepositories.findById(jobPosition.getPosition().getId())
                .orElseThrow(() -> new IllegalArgumentException("Position not found with id " + jobPosition.getPosition().getId()));

        jobPosition.setPosition(position);

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
        Position position = positionRepositories.findById(updatedJobPosition.getPosition().getId())
                .orElseThrow(() -> new IllegalArgumentException("Position not found with id " + updatedJobPosition.getPosition().getId()));

        existingJobPosition.setPosition(position);
        existingJobPosition.setSkillsRequired(updatedJobPosition.getSkillsRequired());
        existingJobPosition.setApplicationDeadline(updatedJobPosition.getApplicationDeadline());
        existingJobPosition.setJobPositionName(updatedJobPosition.getJobPositionName());
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
