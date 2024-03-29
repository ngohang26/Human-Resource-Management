package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.dto.EmployeeSalaryDTO;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.EmployeeSalary;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EmployeeSalaryService {

//    Map<Long, BigDecimal> calculateOvertimeSalaryForEachEmployee();

    // Trong EmployeeSalaryServiceImpl

    // Trong EmployeeSalaryServiceImpl

    Map<Long, BigDecimal> calculateOvertimeSalaryForEachEmployee(int year, int month);

    Long getTotalOvertimeHours(Long employeeId, int year, int month);

    Map<Long, BigDecimal> calculateInsuranceForEachEmployee();

    // luong net - sau thue
    BigDecimal calculateNetSalary(Long employeeId, int year, int month);

    BigDecimal calculateTotalIncome(Long employeeId, int year, int month);

    Map<Long, BigDecimal> calculateIncomeTaxForEachEmployee(int year, int month);

    EmployeeSalary getEmployeeSalaryDetails(Long employeeId, int year, int month);

    List<EmployeeSalary> getAllEmployeeSalaryDetails(int year, int month);
}
