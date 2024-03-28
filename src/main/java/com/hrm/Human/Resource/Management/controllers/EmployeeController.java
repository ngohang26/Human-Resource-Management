package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.response.EmployeeResponse;
import com.hrm.Human.Resource.Management.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/employee/{employeeCode}")
    public ResponseEntity<Employee> getEmployeeByEmployeeCode(@PathVariable String employeeCode) {
        Employee employee = employeeService.getEmployeeByEmployeeCode(employeeCode);
        if (employee != null) {
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping(path = "addEmployee")
    public ResponseEntity<?> createEmployee(@RequestPart("employee") String employeeString, @RequestPart("file") MultipartFile file) {
        return employeeService.addEmployee(employeeString, file);
    }

    @PutMapping(path = "updateEmployee/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestPart("employee") String employeeString, @RequestPart("file") MultipartFile file) {
        return employeeService.updateEmployee(id, employeeString, file);
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

    @GetMapping("/exists/{identityCardNumber}")
    public ResponseEntity<Boolean> checkIdentityCardNumberExists(@PathVariable String identityCardNumber) {
        boolean exists = employeeService.existsByIdentityCardNumber(identityCardNumber);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    // Lấy tất cả các phụ cấp của một nhân viên
    @GetMapping("/{id}/allowances")
    public ResponseEntity<List<Allowance>> getEmployeeAllowances(@PathVariable Long id) {
        return employeeService.getAllowances(id);
    }

    // Thêm phụ cấp cho một nhân viên
    @PostMapping("/{id}/allowances/{allowanceId}")
    public ResponseEntity<Employee> addAllowanceToEmployee(@PathVariable Long id, @PathVariable Long allowanceId, @RequestBody EmployeeAllowance employeeAllowance) {
        return employeeService.addAllowance(id, allowanceId, employeeAllowance.getStartDate(), employeeAllowance.getEndDate());
    }

    @PutMapping("/{employeeId}/allowances/{allowanceId}")
    public ResponseEntity<EmployeeAllowance> updateEmployeeAllowance(@PathVariable Long employeeId, @PathVariable Long allowanceId, @RequestBody EmployeeAllowance newEmployeeAllowance) {
        return employeeService.updateAllowance(employeeId, allowanceId, newEmployeeAllowance);
    }

    // Xóa phụ cấp của một nhân viên
    @DeleteMapping("/{employeeId}/allowances/{allowanceId}")
    public ResponseEntity<Void> deleteEmployeeAllowance(@PathVariable Long employeeId, @PathVariable Long allowanceId) {
        return employeeService.deleteAllowance(employeeId, allowanceId);
    }
}