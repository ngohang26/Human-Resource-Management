package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.dto.EmployeeContractDTO;
import com.hrm.Human.Resource.Management.dto.EmployeeDTO;
import com.hrm.Human.Resource.Management.dto.GenderPercentage;
import com.hrm.Human.Resource.Management.dto.TerminationRequest;
import com.hrm.Human.Resource.Management.entity.Contract;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.jwt.JwtTokenProvider;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.response.ErrorResponse;
import com.hrm.Human.Resource.Management.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired
    private EmployeeRepositories employeeRepositories;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PreAuthorize("hasAuthority('ADD_EMPLOYEE')")
    @GetMapping(path = "getAllEmployees")
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getEmployeesDTO();
    }

    @PreAuthorize("hasAuthority('ADD_EMPLOYEE')")
    @GetMapping(path = "getAllEmployeesActive")
    public List<Employee> getAllEmployeesActive() {
        return employeeService.getActiveEmployees();
    }

    @PreAuthorize("hasAuthority('ADD_EMPLOYEE')")
    @GetMapping(path = "getAllEmployeesTermination")
    public List<Employee> getAllEmployeesTermination() {
        return employeeService.getTerminatedEmployees();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = tokenProvider.getUsernameFromJWT(token);

        Optional<Employee> employee = employeeService.findById(id);

        if (employee.isPresent() && username.equals(employee.get().getEmployeeCode()) && !tokenProvider.getAuthoritiesFromJWT(token).contains("ADMIN")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/employee/{employeeCode}")
    public ResponseEntity<EmployeeDTO> getEmployeeByEmployeeCode(@PathVariable String employeeCode, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = tokenProvider.getUsernameFromJWT(token);
        List<String> authorities = tokenProvider.getAuthoritiesFromJWT(token);

        if (!authorities.contains("ADD_EMPLOYEE") && (!username.equals(employeeCode) || !authorities.contains("VIEW_EMPLOYEE"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        EmployeeDTO employeeDTO = employeeService.getEmployeeByEmployeeCodeDTO(employeeCode);
        if (employeeDTO != null) {
            return new ResponseEntity<>(employeeDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PreAuthorize("hasAuthority('ADD_EMPLOYEE')")
    @PostMapping("/addEmployee")
    public ResponseEntity<EmployeeDTO> addEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO savedEmployeeDTO = employeeService.saveEmployee(employeeDTO);
        return new ResponseEntity<>(savedEmployeeDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('EDIT_EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeDTO employeeDetailsDTO, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            result.getFieldErrors().forEach((error) -> {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            });
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.toString());
        }
        EmployeeDTO updatedEmployeeDTO = employeeService.updateEmployee(id, employeeDetailsDTO);
        if (updatedEmployeeDTO == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found.");
        }
        return new ResponseEntity<>(updatedEmployeeDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('DELETE_EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ErrorResponse> deleteEmployee(@PathVariable Long id) {
        return employeeService.deleteEmployee(id);
    }

    //??
//    @PostMapping("/undo/{id}")
//    public ResponseEntity<ErrorResponse> undoDelete(@PathVariable Long id) {
//        return employeeService.undoDeleteEmployee(id);
//    }

    //??
    @DeleteMapping("/hardDelete/{id}")
    public ResponseEntity<ErrorResponse> hardDeleteEmployee(@PathVariable Long id) {
        return employeeService.hardDeleteEmployee(id);
    }

    //??
    @GetMapping(path = "/searchEmployees")
    public Optional<Employee> searchEmployees(@RequestParam String keyword) {
        return employeeService.searchEmployee(keyword);
    }

    @PreAuthorize("hasAuthority('ADD_CONTRACT')")
    @GetMapping("/contracts")
    public ResponseEntity<List<EmployeeContractDTO>> getAllEmployeeContracts() {
        List<EmployeeContractDTO> employeeContracts = employeeService.getAllEmployeeContracts();
        return ResponseEntity.ok(employeeContracts);
    }

    @PreAuthorize("hasAuthority('ADD_CONTRACT')")
    @PostMapping("/{employeeCode}/contract")
    public ResponseEntity<EmployeeContractDTO> createContract(@PathVariable String employeeCode, @RequestBody Contract contract) {
        EmployeeContractDTO createdContract = employeeService.createContract(employeeCode, contract);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContract);
    }

    @PreAuthorize("hasAuthority('EDIT_CONTRACT')")
    @PutMapping("/{employeeCode}/contract")
    public ResponseEntity<EmployeeContractDTO> updateContract(@PathVariable String employeeCode, @RequestBody Contract contract) {
        EmployeeContractDTO updatedContract = employeeService.updateContract(employeeCode, contract);
        return ResponseEntity.ok(updatedContract);
    }

    @GetMapping("/{employeeCode}/contract")
    public ResponseEntity<EmployeeContractDTO> getContract(@PathVariable String employeeCode, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = tokenProvider.getUsernameFromJWT(token);
        List<String> authorities = tokenProvider.getAuthoritiesFromJWT(token); // giả sử bạn có phương thức để lấy danh sách vai trò từ JWT

        if (!authorities.contains("ADD_CONTRACT") && (!username.equals(employeeCode) || !authorities.contains("VIEW_CONTRACT"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        EmployeeContractDTO contract = employeeService.getContract(employeeCode);
        return ResponseEntity.ok(contract);
    }


    @PreAuthorize("hasAuthority('VIEW_EMPLOYEE')")
    @GetMapping("/employeeCodes")
    public List<String> getEmployeeCodes() {
        return employeeService.getEmployeeCodes();
    }

    @PreAuthorize("hasAuthority('VIEW_EMPLOYEE')")
    @GetMapping("/genderPercentage")
    public GenderPercentage getGenderPercentage() {
        return employeeService.getGenderPercentage();
    }

    @PreAuthorize("hasAuthority('VIEW_EMPLOYEE')")
    @PutMapping("/{id}/terminate")
    public ResponseEntity<?> terminateEmployment(@PathVariable Long id, @RequestBody TerminationRequest terminationRequest) {
        ResponseEntity<ErrorResponse> response = employeeService.updateEmployeeStatus(
                id,
                terminationRequest.getReasonId(),
                terminationRequest.getTerminationDate()
        );
        return response;
    }
}