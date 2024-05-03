package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.Department;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.response.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface EmployeeRepositories extends JpaRepository<Employee, Long> {
    Optional<Employee> findById(Long id);
    Optional<Employee> findByFullNameContaining(String keyword);
    Optional<Employee> findByPhoneNumber(String phoneNumber);
    long count();
    boolean existsById(Long id);

    Employee findByEmployeeCode(String employeeCode);

    Employee findByPersonalInfo_IdentityCardNumber(String identityCardNumber);


    Long countByDepartment(Department department);
    default Employee findByEmployeeCodeOrThrow(String employeeCode) throws ResourceNotFoundException {
        Employee employee = findByEmployeeCode(employeeCode);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee with code " + employeeCode + " not found");
        }
        return employee;
    }

    default Employee findByIdentityCardNumberOrThrow(String identityCardNumber) throws ResourceNotFoundException {
        Employee employee = findByPersonalInfo_IdentityCardNumber(identityCardNumber);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee with identity card number " + identityCardNumber + " not found");
        }
        return employee;
    }

    Employee findByCodeName(String codeName);

    Employee findEmployeeByPersonalInfoIdentityCardNumber(String identityCardNumber);

    List<Employee> findByDepartmentId(Long id);
}

