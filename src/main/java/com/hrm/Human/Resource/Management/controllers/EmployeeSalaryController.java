package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.EmployeeSalary;
import com.hrm.Human.Resource.Management.service.AttendanceService;
import com.hrm.Human.Resource.Management.service.EmployeeSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employeeSalary")
public class EmployeeSalaryController {
    @Autowired
    private EmployeeSalaryService employeeSalaryService;

    @GetMapping("/overtimeSalaries/{year}/{month}")
    public ResponseEntity<Map<Long, BigDecimal>> getOvertimeSalaries(@PathVariable int year, @PathVariable int month) {
        Map<Long, BigDecimal> overtimeSalaries = employeeSalaryService.calculateOvertimeSalaryForEachEmployee(year, month);
        return ResponseEntity.ok(overtimeSalaries);
    }

    @GetMapping("/insurance")
    public ResponseEntity<Map<Long, BigDecimal>> getInsuranceForEachEmployee() {
        Map<Long, BigDecimal> insuranceAmounts = employeeSalaryService.calculateInsuranceForEachEmployee();
        return ResponseEntity.ok(insuranceAmounts);
    }

    @GetMapping("/income-tax/{year}/{month}")
    public ResponseEntity<Map<Long, BigDecimal>> getIncomeTaxForEachEmployee(@PathVariable int year, @PathVariable int month) {
        Map<Long, BigDecimal> incomeTaxes = employeeSalaryService.calculateIncomeTaxForEachEmployee(year, month);
        return ResponseEntity.ok(incomeTaxes);
    }

    @GetMapping("/totalIncome/{employeeId}/{year}/{month}")
    public ResponseEntity<BigDecimal> getTotalIncome(@PathVariable Long employeeId, @PathVariable int year, @PathVariable int month) {
        BigDecimal totalIncome = employeeSalaryService.calculateTotalIncome(employeeId, year, month);
        return new ResponseEntity<>(totalIncome, HttpStatus.OK);
    }

    @GetMapping("/overtimeHours/{employeeId}/{year}/{month}")
    public ResponseEntity<Long> getOvertimeHours(@PathVariable Long employeeId, @PathVariable int year, @PathVariable int month) {
        Long totalOvertimeHours = employeeSalaryService.getTotalOvertimeHours(employeeId, year, month);
        return new ResponseEntity<>(totalOvertimeHours, HttpStatus.OK);
    }

    @GetMapping("/salaryDetails/{employeeId}/{year}/{month}")
    public ResponseEntity<EmployeeSalary> getSalaryDetails(@PathVariable Long employeeId, @PathVariable int year, @PathVariable int month) {
        EmployeeSalary details = employeeSalaryService.getEmployeeSalaryDetails(employeeId, year, month);
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    @GetMapping("/allSalaryDetails/{year}/{month}")
    public ResponseEntity<List<EmployeeSalary>> getAllSalaryDetails(@PathVariable int year, @PathVariable int month) {
        List<EmployeeSalary> allDetails = employeeSalaryService.getAllEmployeeSalaryDetails(year, month);
        return new ResponseEntity<>(allDetails, HttpStatus.OK);
    }


}
