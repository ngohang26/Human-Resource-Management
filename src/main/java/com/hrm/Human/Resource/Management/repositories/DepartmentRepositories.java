package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface DepartmentRepositories extends JpaRepository<Department, Long> {

    Department findByDepartmentName(String departmentName);
}
