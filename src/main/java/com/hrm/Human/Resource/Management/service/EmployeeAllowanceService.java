package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;

import java.math.BigDecimal;
import java.util.List;

public interface EmployeeAllowanceService {

    List<EmployeeAllowance> getEmployeeAllowances(String employeeCode);

    List<Allowance> getAllowancesForEmployee(String employeeCode, Integer year, Integer month);

    //    @Override
    //    public List<EmployeeAllowance> getAllowancesForEmployee(String employeeCode, int year, int month) {
    //        Employee employee = employeeRepositories.findByEmployeeCode(employeeCode);
    //        List<EmployeeAllowance> employeeAllowances = employeeAllowanceRepositories.findByEmployee(employee);
    //        List<Allowance> allowances = new ArrayList<>();
    //        for (EmployeeAllowance employeeAllowance : employeeAllowances) {
    //            // Kiểm tra nếu tháng và năm của startDate nằm trong tháng và năm được cung cấp
    //            if (employeeAllowance.getStartDate().getYear() == year &&
    //                    employeeAllowance.getStartDate().getMonthValue() <= month) {
    //                allowances.add(employeeAllowance.getAllowance());
    //            }
    //        }
    //        return allowances;
    //    }
    BigDecimal getTotalAllowance(String employeeCode, int year, int month);

    EmployeeAllowance addEmployeeAllowance(String employeeCode, EmployeeAllowance employeeAllowance);
    EmployeeAllowance updateEmployeeAllowance(String employeeCode, Long employeeAllowanceId, EmployeeAllowance newEmployeeAllowance);
    void deleteEmployeeAllowance(String employeeCode, Long employeeAllowanceId);}
