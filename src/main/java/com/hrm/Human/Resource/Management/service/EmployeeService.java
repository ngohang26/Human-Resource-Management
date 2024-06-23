package com.hrm.Human.Resource.Management.service;
import com.hrm.Human.Resource.Management.dto.EmployeeContractDTO;
import com.hrm.Human.Resource.Management.dto.EmployeeDTO;
import com.hrm.Human.Resource.Management.dto.GenderPercentage;
import com.hrm.Human.Resource.Management.entity.Contract;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.response.ErrorResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Optional<Employee> searchEmployee(String keyword);

    Optional<Employee> findById(Long id);

    Employee getEmployeeByEmployeeCode(String employeeCode);

//    Optional<Employee> getEmployeeById(Long id);

    Employee save(Employee employee);

    List<Employee> getEmployees();

    List<Employee> getActiveEmployees();

    List<Employee> getTerminatedEmployees();

    List<Employee> getEmployeeEntities();


    EmployeeDTO getEmployeeByEmployeeCodeDTO(String employeeCode);

    Optional<EmployeeDTO> getEmployeeDTOById(Long id);

    List<EmployeeDTO> getEmployeesDTO();

    List<EmployeeDTO> getEmployeeDTOEntities();

    EmployeeDTO saveEmployee(EmployeeDTO employeeDTO);


    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDetailsDTO);

    ResponseEntity<ErrorResponse> deleteEmployee(Long id);

//    ResponseEntity<ErrorResponse> undoDeleteEmployee(Long id);

    ResponseEntity<ErrorResponse> hardDeleteEmployee(Long id);

    Employee findEmployeeByIdentityCardNumber(String identityCardNumber);

    List<EmployeeContractDTO> getAllEmployeeContracts();

    EmployeeContractDTO createContract(String employeeCode, Contract contract);

    EmployeeContractDTO updateContract(String employeeCode, Contract contract);

    EmployeeContractDTO getContract(String employeeCode);

    List<String> getEmployeeCodes();

    GenderPercentage getGenderPercentage();

    @Transactional
    ResponseEntity<ErrorResponse> updateEmployeeStatus(Long id, Long reasonId, LocalDate terminationDate);
}