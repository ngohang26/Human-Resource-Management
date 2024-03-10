package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.entity.JobPosition;
import com.hrm.Human.Resource.Management.response.JobPositionResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface JobPositionService {
    List<JobPosition> getJobPositionName();

    Optional<JobPosition> findById(Long id);

    ResponseEntity<?> addJobPosition(JobPosition position);

    JobPosition updateJobPosition(Long id, JobPosition positionDetails);


    ResponseEntity<JobPositionResponse> hardDeleteJobPosition(Long id);
}
