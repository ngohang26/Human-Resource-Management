package com.hrm.Human.Resource.Management.service;
import com.hrm.Human.Resource.Management.dto.EmployeeContractDTO;
import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.Contract;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
import com.hrm.Human.Resource.Management.response.EmployeeResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Optional<Employee> searchEmployee(String keyword);

    Optional<Employee> findById(Long id);

    Employee getEmployeeByEmployeeCode(String employeeCode);

    Optional<Employee> getEmployeeById(Long id);

    Employee save(Employee employee);

    List<Employee> getEmployees();

    List<Employee> getEmployeeEntities();

    Employee saveEmployee(Employee employee);

    ResponseEntity<?> updateEmployee(Long id, Employee employeeDetails);

    ResponseEntity<EmployeeResponse> deleteEmployee(Long id);

    ResponseEntity<EmployeeResponse> undoDeleteEmployee(Long id);

    ResponseEntity<EmployeeResponse> hardDeleteEmployee(Long id);

    Employee findEmployeeByIdentityCardNumber(String identityCardNumber);

    List<EmployeeContractDTO> getAllEmployeeContracts();

    EmployeeContractDTO createContract(String employeeCode, Contract contract);

    EmployeeContractDTO updateContract(String employeeCode, Contract contract);

    EmployeeContractDTO getContract(String employeeCode);

    List<String> getEmployeeCodes();

}