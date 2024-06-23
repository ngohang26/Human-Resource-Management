package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;

import java.math.BigDecimal;
import java.util.List;

public interface EmployeeAllowanceService {

    List<EmployeeAllowance> getEmployeeAllowances(String employeeCode);

    List<Allowance> getAllowancesForEmployee(String employeeCode, int year, int month);

    BigDecimal getTotalAllowance(String employeeCode, int year, int month);

    EmployeeAllowance addEmployeeAllowance(String employeeCode, EmployeeAllowance employeeAllowance);
    EmployeeAllowance updateEmployeeAllowance(String employeeCode, Long employeeAllowanceId, EmployeeAllowance newEmployeeAllowance);
    void deleteEmployeeAllowance(String employeeCode, Long employeeAllowanceId);}
