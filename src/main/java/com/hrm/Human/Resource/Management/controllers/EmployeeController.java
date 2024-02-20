package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.response.EmployeeResponse;
import com.hrm.Human.Resource.Management.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/addEmployee")
    public ResponseEntity<?> addEmployee(@RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }

    @PutMapping("/#/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        return employeeService.updateEmployee(id, employeeDetails);
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
}
