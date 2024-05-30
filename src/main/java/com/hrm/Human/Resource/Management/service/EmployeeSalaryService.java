package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.entity.EmployeeSalary;
import com.hrm.Human.Resource.Management.entity.EmployeeSalaryRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface EmployeeSalaryService {
    Map<String, BigDecimal> calculateOvertimeSalaryForEachEmployee(int year, int month);

    Long getTotalOvertimeHours(String employeeCode, int year, int month);

    BigDecimal getTotalInsurance(String employeeCode);

    // luong net - sau thue
    BigDecimal calculateNetSalary(String employeeCode, int year, int month);

    // luong truoc thue moi empl
    BigDecimal calculateTotalIncome(String employeeCode, int year, int month);

    BigDecimal calculateIncomeTaxForEmployee(String employeeCode, int year, int month);

    // luong truoc thue all
    Map<String, BigDecimal> calculateIncomeTaxForEachEmployee(int year, int month);

    boolean employeeExists(String employeeCode);
    boolean employeeDataExists(String employeeCode, int year, int month);

    List<EmployeeSalaryRecord> getAllEmployeeSalaryRecords(int year, int month);

    Map<String, Long> calculateTotalOvertimeHoursByDepartment(int year, int month);

    Map<String, BigDecimal> calculateTotalIncomeTaxByDepartment(int year, int month);

    Map<String, Long> calculateTotalOvertimeHoursPerMonth(int year, int month);

    void updateEmployeeSalaryRecord(String employeeCode, int year, int month);

    void updateAllEmployeeSalaryRecords(int year, int month);

    EmployeeSalaryRecord getEmployeeSalaryRecord(String employeeCode, int year, int month);
}
