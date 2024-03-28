package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.service.AttendanceService;
import com.hrm.Human.Resource.Management.service.EmployeeSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/employeeSalary")
public class EmployeeSalaryController {
    @Autowired
    private EmployeeSalaryService employeeSalaryService;
    @GetMapping("/overtimeSalaries")
    public ResponseEntity<Map<Long, BigDecimal>> getOvertimeSalaries() {
        Map<Long, BigDecimal> overtimeSalaries = employeeSalaryService.calculateOvertimeSalaryForEachEmployee();
        return ResponseEntity.ok(overtimeSalaries);
    }
}
