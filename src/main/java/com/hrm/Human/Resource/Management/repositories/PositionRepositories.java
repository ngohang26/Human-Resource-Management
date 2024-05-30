package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface PositionRepositories extends JpaRepository<Position, Long> {

    Optional<Position> findByPositionNameContaining(String keyword);

    Position findByPositionName(String positionName);

    boolean existsByDepartmentId(Long id);
}

