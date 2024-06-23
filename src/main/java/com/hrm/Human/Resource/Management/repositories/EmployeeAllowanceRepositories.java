package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface EmployeeAllowanceRepositories extends JpaRepository<EmployeeAllowance, Long> {

    List<EmployeeAllowance> findByEmployee_EmployeeCode(String employeeCode);


    List<EmployeeAllowance> findByEmployee(Employee employee);

    EmployeeAllowance findByEmployeeAndAllowance(Employee employee, Allowance allowance);

    boolean existsByAllowanceId(Long id);
}
