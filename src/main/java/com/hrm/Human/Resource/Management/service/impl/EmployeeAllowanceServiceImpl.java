package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.dto.EmployeeAllowanceDTO;
import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
import com.hrm.Human.Resource.Management.repositories.AllowanceRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeAllowanceRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.response.ResourceNotFoundException;
import com.hrm.Human.Resource.Management.service.EmployeeAllowanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeAllowanceServiceImpl implements EmployeeAllowanceService {

    @Autowired
    private EmployeeRepositories employeeRepositories;

    @Autowired
    private EmployeeAllowanceRepositories employeeAllowanceRepositories;

    @Autowired
    private AllowanceRepositories allowanceRepositories;

    @Override
    public List<EmployeeAllowance> getEmployeeAllowances(String employeeCode) {
        Employee employee = employeeRepositories.findByEmployeeCode(employeeCode);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }
        return employeeAllowanceRepositories.findByEmployee(employee);
    }

    @Override
    public EmployeeAllowance addEmployeeAllowance(String employeeCode, EmployeeAllowance employeeAllowance) {
        Employee employee = employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);

        Allowance allowance = allowanceRepositories.findById(employeeAllowance.getAllowance().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Allowance not found with id: " + employeeAllowance.getAllowance().getId()));

        EmployeeAllowance existingAllowance = employeeAllowanceRepositories.findByEmployeeAndAllowance(employee, allowance);
        if (existingAllowance != null) {
            throw new RuntimeException("Employee has already received this allowance");
        }

        employeeAllowance.setEmployee(employee);
        employeeAllowance.setAllowance(allowance);
        return employeeAllowanceRepositories.save(employeeAllowance);
    }

    @Override
    public EmployeeAllowance updateEmployeeAllowance(String employeeCode, Long employeeAllowanceId, EmployeeAllowance newEmployeeAllowance) {
        Employee employee = employeeRepositories.findByEmployeeCode(employeeCode);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }
        EmployeeAllowance employeeAllowance = employeeAllowanceRepositories.findById(employeeAllowanceId)
                .orElseThrow(() -> new RuntimeException("EmployeeAllowance not found for this employee"));
        Allowance allowance = allowanceRepositories.findById(newEmployeeAllowance.getAllowance().getId())
                .orElseThrow(() -> new RuntimeException("Allowance not found"));

        EmployeeAllowance existingAllowance = employeeAllowanceRepositories.findByEmployeeAndAllowance(employee, allowance);
        if (existingAllowance != null && !existingAllowance.getId().equals(employeeAllowanceId)) {
            throw new RuntimeException("Employee has already received this allowance");
        }

        employeeAllowance.setAllowance(allowance);
        employeeAllowance.setStartDate(newEmployeeAllowance.getStartDate());
        employeeAllowance.setEndDate(newEmployeeAllowance.getEndDate());
        return employeeAllowanceRepositories.save(employeeAllowance);
    }

    @Override
    public void deleteEmployeeAllowance(String employeeCode, Long employeeAllowanceId) {
        Employee employee = employeeRepositories.findByEmployeeCode(employeeCode);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }
        EmployeeAllowance employeeAllowance = employeeAllowanceRepositories.findById(employeeAllowanceId)
                .orElseThrow(() -> new RuntimeException("EmployeeAllowance not found for this employee"));
        employeeAllowanceRepositories.delete(employeeAllowance);
    }


}
