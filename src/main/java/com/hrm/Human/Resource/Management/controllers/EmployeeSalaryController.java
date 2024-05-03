package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.EmployeeSalary;
import com.hrm.Human.Resource.Management.jwt.JwtTokenProvider;
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

    @Autowired
    private JwtTokenProvider tokenProvider;

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

    @GetMapping("/salaryDetails/{employeeCode}/{year}/{month}")
    public ResponseEntity<?> getSalaryDetails(@PathVariable String employeeCode, @PathVariable int year, @PathVariable int month, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = tokenProvider.getUsernameFromJWT(token);
        List<String> authorities = tokenProvider.getAuthoritiesFromJWT(token); // giả sử bạn có phương thức để lấy danh sách vai trò từ JWT

        if (!authorities.contains("ADD_SALARY") && (!username.equals(employeeCode) || !authorities.contains("VIEW_SALARY"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

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

    @PreAuthorize("hasAuthority('ADD_SALARY')")
    @GetMapping("/allSalaryDetails/{year}/{month}")
    public ResponseEntity<?> getAllSalaryDetails(@PathVariable int year, @PathVariable int month) {
        List<EmployeeSalary> allDetails = employeeSalaryService.getAllEmployeeSalaryDetails(year, month);

        if (allDetails.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(allDetails);
        }
    }

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @GetMapping("/totalOvertimeHoursByDepartment/{year}/{month}")
    public ResponseEntity<Map<String, Long>> getTotalOvertimeHoursByDepartment(@PathVariable int year, @PathVariable int month) {
        Map<String, Long> totalOvertimeHoursByDepartment = employeeSalaryService.calculateTotalOvertimeHoursByDepartment(year, month);
        return ResponseEntity.ok(totalOvertimeHoursByDepartment);
    }

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @GetMapping("/totalIncomeTaxByDepartment/{year}/{month}")
    public ResponseEntity<Map<String, BigDecimal>> getTotalIncomeTaxByDepartment(@PathVariable int year, @PathVariable int month) {
        Map<String, BigDecimal> totalIncomeTaxByDepartment = employeeSalaryService.calculateTotalIncomeTaxByDepartment(year, month);
        return ResponseEntity.ok(totalIncomeTaxByDepartment);
    }

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @GetMapping("/totalOvertimeHoursPerMonth/{year}/{month}")
    public ResponseEntity<Map<String, Long>> getTotalOvertimeHoursPerMonth(@PathVariable int year, @PathVariable int month) {
        Map<String, Long> totalOvertimeHoursPerMonth = employeeSalaryService.calculateTotalOvertimeHoursPerMonth(year, month);
        return ResponseEntity.ok(totalOvertimeHoursPerMonth);
    }

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @GetMapping("/totalWorkingHours/{year}/{month}")
    public ResponseEntity<Map<String, Long>> getTotalWorkingHours(@PathVariable int year, @PathVariable int month) {
        Map<String, Long> totalWorkingHours = employeeSalaryService.calculateTotalWorkingHoursForEachEmployee(year, month);
        return ResponseEntity.ok(totalWorkingHours);
    }

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @GetMapping("/totalSalary/{year}/{month}")
    public ResponseEntity<BigDecimal> getTotalSalary(@PathVariable int year, @PathVariable int month) {
        BigDecimal totalSalary = employeeSalaryService.calculateTotalSalaryForAllEmployees(year, month);
        return ResponseEntity.ok(totalSalary);
    }

}
