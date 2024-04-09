package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.dto.EmployeeAllowanceDTO;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;

import java.math.BigDecimal;
import java.util.List;

public interface EmployeeAllowanceService {

    EmployeeAllowance addEmployeeAllowance(String employeeCode, EmployeeAllowance employeeAllowance);
    List<EmployeeAllowance> getEmployeeAllowances(String employeeCode);
    EmployeeAllowance updateEmployeeAllowance(String employeeCode, Long employeeAllowanceId, EmployeeAllowance newEmployeeAllowance);
    void deleteEmployeeAllowance(String employeeCode, Long employeeAllowanceId);}
