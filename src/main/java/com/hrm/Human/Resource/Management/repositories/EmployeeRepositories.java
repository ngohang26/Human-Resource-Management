package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface EmployeeRepositories extends JpaRepository<Employee, Long> {
//    List<Employee> findAll();
//    Optional<Employee> findById(Long id);
    Optional<Employee> findByFullNameContaining(String keyword);
    Optional<Employee> findByPhoneNumber(String phoneNumber);
    long count();
    boolean existsById(Long id);

    Employee findByEmployeeCode(String employeeCode);

    boolean existsByPersonalInfoIdentityCardNumber(String identityCardNumber);

    Employee findByCodeName(String codeName);

//    List<Employee> findAll();
}
