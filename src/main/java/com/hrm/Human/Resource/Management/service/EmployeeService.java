package com.hrm.Human.Resource.Management.service;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.response.EmployeeResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Optional<Employee> searchEmployee(String keyword);

    Optional<Employee> findById(Long id);

    Employee getEmployeeByEmployeeCode(String employeeCode);

    Employee save(Employee employee);

    List<Employee> getEmployeeEntities();
    List<Employee> getEmployees();

    ResponseEntity<?> addEmployee(Employee employee);

    Employee updateEmployee(Long id, Employee updatedEmployee);

    ResponseEntity<EmployeeResponse> deleteEmployee(Long id);

    ResponseEntity<EmployeeResponse> undoDeleteEmployee(Long id);

    ResponseEntity<EmployeeResponse> hardDeleteEmployee(Long id);

}