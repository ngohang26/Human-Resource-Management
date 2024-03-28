package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.*;
import com.hrm.Human.Resource.Management.repositories.AttendanceRepositories;
import com.hrm.Human.Resource.Management.repositories.ContractRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.service.EmployeeSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeSalaryServiceImpl implements EmployeeSalaryService {
    @Autowired
    private EmployeeRepositories employeeRepositories;

    @Autowired
    private ContractRepositories contractRepositories;

    @Autowired
    private AttendanceRepositories attendanceRepositories;

    @Override
    // Trong EmployeeSalaryServiceImpl
    public Map<Long, BigDecimal> calculateOvertimeSalaryForEachEmployee() {
        List<Employee> employees = employeeRepositories.findAll();
        Map<Long, BigDecimal> overtimeSalaries = new HashMap<>();

        for (Employee employee : employees) {
            Long employeeId = employee.getId();
            BigDecimal monthlySalary = getMonthlySalary(employeeId);
            Long totalOvertimeHours = getTotalOvertimeHours(employeeId);

            BigDecimal overtimeSalary = calculateOvertimeSalary(monthlySalary, totalOvertimeHours);
            overtimeSalaries.put(employeeId, overtimeSalary);
        }

        return overtimeSalaries;
    }

    private BigDecimal getMonthlySalary(Long employeeId) {
        Employee employee = employeeRepositories.findById(employeeId).orElse(null);
        if (employee != null) {
            Contract contract = employee.getContract();
            if (contract != null) {
                return contract.getMonthlySalary();
            }
        }
        return BigDecimal.ZERO; // Hoặc giá trị mặc định khác
    }

    private Long getTotalOvertimeHours(Long employeeId) {
        List<Attendance> attendances = attendanceRepositories.findByEmployeeId(employeeId);
        return attendances.stream()
                .mapToLong(Attendance::getOverTime)
                .sum();
    }

    private BigDecimal calculateOvertimeSalary(BigDecimal monthlySalary, Long totalOvertimeHours) {
        BigDecimal workDaysPerMonth = BigDecimal.valueOf(26);
        BigDecimal workHoursPerDay = BigDecimal.valueOf(8);

        BigDecimal hourlyRate = monthlySalary.divide(workDaysPerMonth, 2, RoundingMode.HALF_UP)
                .divide(workHoursPerDay, 2, RoundingMode.HALF_UP);

        return hourlyRate.multiply(BigDecimal.valueOf(totalOvertimeHours));
    }

}
