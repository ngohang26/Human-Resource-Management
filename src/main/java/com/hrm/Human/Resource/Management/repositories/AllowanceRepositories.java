package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface AllowanceRepositories extends JpaRepository<Allowance, Long> {
    Optional<Allowance> findByAllowanceNameContaining(String jobAllowanceName);

    Optional<Object> findByAllowanceName(String jobAllowanceName);
}
