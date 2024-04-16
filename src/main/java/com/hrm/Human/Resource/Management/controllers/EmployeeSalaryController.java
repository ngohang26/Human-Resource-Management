package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.EmployeeSalary;
import com.hrm.Human.Resource.Management.service.AttendanceService;
import com.hrm.Human.Resource.Management.service.EmployeeSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/employeeSalary")
public class EmployeeSalaryController {
    @Autowired
    private EmployeeSalaryService employeeSalaryService;

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @GetMapping("/overtimeSalaries/{year}/{month}")
    public ResponseEntity<Map<String, BigDecimal>> getOvertimeSalaries(@PathVariable int year, @PathVariable int month) {
        Map<String, BigDecimal> overtimeSalaries = employeeSalaryService.calculateOvertimeSalaryForEachEmployee(year, month);
        return ResponseEntity.ok(overtimeSalaries);
    }

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @GetMapping("/income-tax/{year}/{month}")
    public ResponseEntity<Map<String, BigDecimal>> getIncomeTaxForEachEmployee(@PathVariable int year, @PathVariable int month) {
        Map<String, BigDecimal> incomeTaxes = employeeSalaryService.calculateIncomeTaxForEachEmployee(year, month);
        return ResponseEntity.ok(incomeTaxes);
    }

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @GetMapping("/totalIncome/{employeeCode}/{year}/{month}")
    public ResponseEntity<BigDecimal> getTotalIncome(@PathVariable String employeeCode, @PathVariable int year, @PathVariable int month) {
        BigDecimal totalIncome = employeeSalaryService.calculateTotalIncome(employeeCode, year, month);
        return new ResponseEntity<>(totalIncome, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @GetMapping("/overtimeHours/{employeeCode}/{year}/{month}")
    public ResponseEntity<Long> getOvertimeHours(@PathVariable String employeeCode, @PathVariable int year, @PathVariable int month) {
        Long totalOvertimeHours = employeeSalaryService.getTotalOvertimeHours(employeeCode, year, month);
        return new ResponseEntity<>(totalOvertimeHours, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @GetMapping("/salaryDetails/{employeeCode}/{year}/{month}")
    public ResponseEntity<?> getSalaryDetails(@PathVariable String employeeCode, @PathVariable int year, @PathVariable int month) {
        if (!isValidEmployeeCode(employeeCode)) {
            throw new RuntimeException("Mã nhân viên không hợp lệ.");
        }

        if (!employeeSalaryService.employeeExists(employeeCode)) {
            throw new RuntimeException("Nhân viên có mã " + employeeCode + " không tồn tại.");
        }

        if (!employeeSalaryService.employeeDataExists(employeeCode, year, month)) {
            throw new RuntimeException("Dữ liệu " + month + "/" + year + " của nhân viên " + employeeCode + " không tồn tại.");
        }

        EmployeeSalary details = employeeSalaryService.getEmployeeSalaryDetails(employeeCode, year, month);
        return ResponseEntity.ok(details);
    }


    private boolean isValidEmployeeCode(String employeeCode) {
        String pattern = "^[a-zA-Z0-9]+$";
        return Pattern.matches(pattern, employeeCode);
    }
//    @GetMapping("/allSalaryDetails/{year}/{month}")
//    public ResponseEntity<List<EmployeeSalary>> getAllSalaryDetails(@PathVariable int year, @PathVariable int month) {
//        List<EmployeeSalary> allDetails = employeeSalaryService.getAllEmployeeSalaryDetails(year, month);
//        return ResponseEntity.ok(allDetails);
//    }

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @GetMapping("/allSalaryDetails/{year}/{month}")
    public ResponseEntity<?> getAllSalaryDetails(@PathVariable int year, @PathVariable int month) {
        List<EmployeeSalary> allDetails = employeeSalaryService.getAllEmployeeSalaryDetails(year, month);

        if (allDetails.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(allDetails);
        }
    }
}
