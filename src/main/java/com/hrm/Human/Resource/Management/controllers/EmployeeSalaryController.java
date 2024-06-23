package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.EmployeeSalaryRecord;
import com.hrm.Human.Resource.Management.jwt.JwtTokenProvider;
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
    @GetMapping("/tax-employeee/{employeeCode}/{year}/{month}")
    public BigDecimal getTax(@PathVariable String employeeCode, @PathVariable int year, @PathVariable int month) {
        return employeeSalaryService.calculateIncomeTaxForEmployee(employeeCode, year, month);
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

    private boolean isValidEmployeeCode(String employeeCode) {
        String pattern = "^[a-zA-Z0-9]+$";
        return Pattern.matches(pattern, employeeCode);
    }

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @GetMapping("/allSalaryRecords/{year}/{month}")
    public ResponseEntity<?> getAllSalaryRecords(@PathVariable int year, @PathVariable int month) {
        List<EmployeeSalaryRecord> allRecords = employeeSalaryService.getAllEmployeeSalaryRecords(year, month);

        if (allRecords.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(allRecords);
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

//    @PreAuthorize("hasAuthority('VIEW_SALARY')")
//    @GetMapping("/totalWorkingHours/{year}/{month}")
//    public ResponseEntity<Map<String, Long>> getTotalWorkingHours(@PathVariable int year, @PathVariable int month) {
//        Map<String, Long> totalWorkingHours = employeeSalaryService.calculateTotalWorkingHoursForEachEmployee(year, month);
//        return ResponseEntity.ok(totalWorkingHours);
//    }

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @PostMapping("/updateSalaryRecord/{employeeCode}/{year}/{month}")
    public ResponseEntity<?> updateSalaryRecord(@PathVariable String employeeCode, @PathVariable int year, @PathVariable int month) {
        employeeSalaryService.updateEmployeeSalaryRecord(employeeCode, year, month);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('VIEW_SALARY')")
    @PostMapping("/updateAllSalaryRecords/{year}/{month}")
    public ResponseEntity<?> updateAllSalaryRecords(@PathVariable int year, @PathVariable int month) {
        employeeSalaryService.updateAllEmployeeSalaryRecords(year, month);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/salaryRecordDetails/{employeeCode}/{year}/{month}")
    public ResponseEntity<?> getSalaryRecordDetails(@PathVariable String employeeCode, @PathVariable int year, @PathVariable int month, @RequestHeader("Authorization") String token) {
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
        EmployeeSalaryRecord record = employeeSalaryService.getEmployeeSalaryRecord(employeeCode, year, month);

            return ResponseEntity.ok(record);
    }

}
