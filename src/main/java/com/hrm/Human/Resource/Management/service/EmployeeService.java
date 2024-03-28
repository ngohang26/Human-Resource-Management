package com.hrm.Human.Resource.Management.service;
import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
import com.hrm.Human.Resource.Management.response.EmployeeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Optional<Employee> searchEmployee(String keyword);

    Optional<Employee> findById(Long id);

    Employee getEmployeeByEmployeeCode(String employeeCode);

    Employee save(Employee employee);

    List<Employee> getEmployeeEntities();
    List<Employee> getEmployees();

    ResponseEntity<?> addEmployee(String employeeString, MultipartFile file);

//    Employee updateEmployee(Long id, Employee updatedEmployee);

//    ResponseEntity<?> updateEmployee(Long id, Employee updatedEmployee);

    ResponseEntity<?> updateEmployee(Long id, String employeeString, MultipartFile file);

    ResponseEntity<EmployeeResponse> deleteEmployee(Long id);

    ResponseEntity<EmployeeResponse> undoDeleteEmployee(Long id);

    ResponseEntity<EmployeeResponse> hardDeleteEmployee(Long id);

    boolean existsByIdentityCardNumber(String identityCardNumber);

    Optional<Employee> getEmployeeById(Long id);

    ResponseEntity<List<Allowance>> getAllowances(Long employeeId);

    ResponseEntity<Employee> addAllowance(Long employeeId, Long allowanceId, LocalDate startDate, LocalDate endDate);

    ResponseEntity<EmployeeAllowance> updateAllowance(Long employeeId, Long allowanceId, EmployeeAllowance newEmployeeAllowance);

    ResponseEntity<Void> deleteAllowance(Long employeeId, Long allowanceId);

}