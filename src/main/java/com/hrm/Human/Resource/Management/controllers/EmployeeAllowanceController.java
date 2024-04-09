package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.dto.EmployeeAllowanceDTO;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
import com.hrm.Human.Resource.Management.service.EmployeeAllowanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee-allowances")
public class EmployeeAllowanceController {

    @Autowired
    private EmployeeAllowanceService employeeAllowanceService;

    @PostMapping("/{employeeCode}")
    public ResponseEntity<EmployeeAllowance> addEmployeeAllowance(@PathVariable String employeeCode, @RequestBody EmployeeAllowance employeeAllowance) {
        EmployeeAllowance createdEmployeeAllowance = employeeAllowanceService.addEmployeeAllowance(employeeCode, employeeAllowance);
        return new ResponseEntity<>(createdEmployeeAllowance, HttpStatus.CREATED);
    }

    @GetMapping("/{employeeCode}")
    public ResponseEntity<List<EmployeeAllowance>> getEmployeeAllowances(@PathVariable String employeeCode) {
        List<EmployeeAllowance> employeeAllowances = employeeAllowanceService.getEmployeeAllowances(employeeCode);
        return new ResponseEntity<>(employeeAllowances, HttpStatus.OK);
    }

    @PutMapping("/{employeeCode}/allowances/{employeeAllowanceId}")
    public EmployeeAllowance updateEmployeeAllowance(@PathVariable String employeeCode, @PathVariable Long employeeAllowanceId, @RequestBody EmployeeAllowance newEmployeeAllowance) {
        return employeeAllowanceService.updateEmployeeAllowance(employeeCode, employeeAllowanceId, newEmployeeAllowance);
    }

    @DeleteMapping("/{employeeCode}/allowances/{employeeAllowanceId}")
    public void deleteEmployeeAllowance(@PathVariable String employeeCode, @PathVariable Long employeeAllowanceId) {
        employeeAllowanceService.deleteEmployeeAllowance(employeeCode, employeeAllowanceId);
    }
}

