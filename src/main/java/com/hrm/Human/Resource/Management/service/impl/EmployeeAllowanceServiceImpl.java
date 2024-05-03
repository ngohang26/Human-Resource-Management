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
            throw new RuntimeException("Nhân viên đã nhận được trợ cấp này");
        }
        if (employeeAllowance.getStartDate() == null || employeeAllowance.getEndDate() == null) {
            throw new RuntimeException("Bạn cần nhập ngày bắt đầu và ngày kết thúc ");
        }
        if (employeeAllowance.getEndDate().compareTo(employeeAllowance.getStartDate()) < 0) {
            throw new RuntimeException("Ngày kết thúc cần phải sau ngày bắt đầu");
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trợ cấp của nhân viên này"));
        Allowance allowance = null;
        if (newEmployeeAllowance.getAllowance() != null) {
            allowance = allowanceRepositories.findById(newEmployeeAllowance.getAllowance().getId())
                    .orElseThrow(() -> new RuntimeException("Allowance not found"));
        } else {
            allowance = employeeAllowance.getAllowance();
        }
        if (newEmployeeAllowance.getStartDate() == null || newEmployeeAllowance.getEndDate() == null) {
            throw new RuntimeException("Bạn cần nhập ngày bắt đầu và ngày kết thúc");
        }
        if (newEmployeeAllowance.getEndDate().compareTo(newEmployeeAllowance.getStartDate()) < 0) {
            throw new RuntimeException("Ngày kết thúc cần phải sau ngày bắt đầu");
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
