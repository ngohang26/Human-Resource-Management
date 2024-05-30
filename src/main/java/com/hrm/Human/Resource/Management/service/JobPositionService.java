package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.dto.JobPositionDTO;
import com.hrm.Human.Resource.Management.entity.JobPosition;
import com.hrm.Human.Resource.Management.response.ErrorResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface JobPositionService {
    List<JobPosition> getJobPositionName();

    Optional<JobPosition> findById(Long id);

    List<JobPositionDTO> getAllJobPositions();

    ResponseEntity<?> addJobPosition(JobPosition jobPosition);

    JobPosition updateJobPosition(Long id, JobPosition updatedJobPosition);

    ResponseEntity<ErrorResponse> hardDeleteJobPosition(Long id);
}
