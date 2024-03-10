package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobPositionRepositories extends JpaRepository<JobPosition, Long> {
    Optional<JobPosition> findByJobPositionNameContaining(String jobPositionName);

    Optional<Object> findByJobPositionName(String jobPositionName);
}
