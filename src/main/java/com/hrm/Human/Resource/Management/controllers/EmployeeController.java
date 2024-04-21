package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.dto.EmployeeContractDTO;
import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.Contract;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
import com.hrm.Human.Resource.Management.jwt.JwtTokenProvider;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.response.EmployeeResponse;
import com.hrm.Human.Resource.Management.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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

    @GetMapping(path = "getAllEmployees")
    public List<Employee> getAllEmployees() {
        return employeeService.getEmployees();
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
    public ResponseEntity<Employee> getEmployeeByEmployeeCode(@PathVariable String  employeeCode, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = tokenProvider.getUsernameFromJWT(token);

        if (!username.equals(employeeCode)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Employee employee = employeeService.getEmployeeByEmployeeCode(employeeCode);
        if (employee != null) {
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('ADD_EMPLOYEE')")
    @PostMapping("/addEmployee")
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeService.saveEmployee(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('EDIT_EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        ResponseEntity<?> updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
        if (updatedEmployee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found.");
        }
        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('DELETE_EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<EmployeeResponse> deleteEmployee(@PathVariable Long id) {
        return employeeService.deleteEmployee(id);
    }

    //??
    @PostMapping("/undo/{id}")
    public ResponseEntity<EmployeeResponse> undoDelete(@PathVariable Long id) {
        return employeeService.undoDeleteEmployee(id);
    }

    //??
    @DeleteMapping("/hardDelete/{id}")
    public ResponseEntity<EmployeeResponse> hardDeleteEmployee(@PathVariable Long id) {
        return employeeService.hardDeleteEmployee(id);
    }

    //??
    @GetMapping(path = "/searchEmployees")
    public Optional<Employee> searchEmployees(@RequestParam String keyword) {
        return employeeService.searchEmployee(keyword);
    }

    @PreAuthorize("hasAuthority('VIEW_CONTRACT')")
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

    @PreAuthorize("hasAuthority('VIEW_CONTRACT')")
    @GetMapping("/{employeeCode}/contract")
    public ResponseEntity<EmployeeContractDTO> getContract(@PathVariable String employeeCode) {
        EmployeeContractDTO contract = employeeService.getContract(employeeCode);
        return ResponseEntity.ok(contract);
    }

    @PreAuthorize("hasAuthority('VIEW_EMPLOYEE')")
    @GetMapping("/employeeCodes")
    public List<String> getEmployeeCodes() {
        return employeeService.getEmployeeCodes();
    }

}