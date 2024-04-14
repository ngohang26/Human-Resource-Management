package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.dto.EmployeeContractDTO;
import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.Contract;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
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


    @GetMapping(path = "getAllEmployees")
    public List<Employee> getAllEmployees() {
        return employeeService.getEmployees();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.findById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PreAuthorize("hasAuthority('READ_EMPLOYEE')")
    @GetMapping("/employee/{employeeCode}")
    public ResponseEntity<Employee> getEmployeeByEmployeeCode(@PathVariable String employeeCode) {
        Employee employee = employeeService.getEmployeeByEmployeeCode(employeeCode);
        if (employee != null) {
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('CREATE_EMPLOYEE')")
    @PostMapping("/addEmployee")
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeService.saveEmployee(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        ResponseEntity<?> updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
        if (updatedEmployee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found.");
        }
        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EmployeeResponse> deleteEmployee(@PathVariable Long id) {
        return employeeService.deleteEmployee(id);
    }

    @PostMapping("/undo/{id}")
    public ResponseEntity<EmployeeResponse> undoDelete(@PathVariable Long id) {
        return employeeService.undoDeleteEmployee(id);
    }

    @DeleteMapping("/hardDelete/{id}")
    public ResponseEntity<EmployeeResponse> hardDeleteEmployee(@PathVariable Long id) {
        return employeeService.hardDeleteEmployee(id);
    }

    @GetMapping(path = "/searchEmployees")
    public Optional<Employee> searchEmployees(@RequestParam String keyword) {
        return employeeService.searchEmployee(keyword);
    }

    @GetMapping("/contracts")
    public ResponseEntity<List<EmployeeContractDTO>> getAllEmployeeContracts() {
        List<EmployeeContractDTO> employeeContracts = employeeService.getAllEmployeeContracts();
        return ResponseEntity.ok(employeeContracts);
    }

    @PostMapping("/{employeeCode}/contract")
    public ResponseEntity<EmployeeContractDTO> createContract(@PathVariable String employeeCode, @RequestBody Contract contract) {
        EmployeeContractDTO createdContract = employeeService.createContract(employeeCode, contract);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContract);
    }

    @PutMapping("/{employeeCode}/contract")
    public ResponseEntity<EmployeeContractDTO> updateContract(@PathVariable String employeeCode, @RequestBody Contract contract) {
        EmployeeContractDTO updatedContract = employeeService.updateContract(employeeCode, contract);
        return ResponseEntity.ok(updatedContract);
    }

    @GetMapping("/{employeeCode}/contract")
    public ResponseEntity<EmployeeContractDTO> getContract(@PathVariable String employeeCode) {
        EmployeeContractDTO contract = employeeService.getContract(employeeCode);
        return ResponseEntity.ok(contract);
    }

    @GetMapping("/employeeCodes")
    public List<String> getEmployeeCodes() {
        return employeeService.getEmployeeCodes();
    }

}