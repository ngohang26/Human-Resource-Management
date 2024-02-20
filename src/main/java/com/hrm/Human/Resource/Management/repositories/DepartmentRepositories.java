package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.Department;
import com.hrm.Human.Resource.Management.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface DepartmentRepositories extends JpaRepository<Department, Long> {
    List<Department> findAll();
    Optional<Department> findById(Long id);
    List<Department> findByDepartmentNameContaining(String keyword);
    Department findByDepartmentName(String departmentName);
}
