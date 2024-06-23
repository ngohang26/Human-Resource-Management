package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
import com.hrm.Human.Resource.Management.repositories.AllowanceRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeAllowanceRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.response.ResourceNotFoundException;
import com.hrm.Human.Resource.Management.service.EmployeeAllowanceService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public List<Allowance> getAllowancesForEmployee(String employeeCode, int year, int month) {
        Employee employee = employeeRepositories.findByEmployeeCode(employeeCode);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }
        List<EmployeeAllowance> employeeAllowances = employeeAllowanceRepositories.findByEmployee(employee);
        List<Allowance> allowances = new ArrayList<>();
        for (EmployeeAllowance employeeAllowance : employeeAllowances) {
            if ((employeeAllowance.getStartDate().getYear() == year &&
                    employeeAllowance.getStartDate().getMonthValue() <= month) &&
                    (employeeAllowance.getEndDate().isAfter(YearMonth.from(LocalDate.of(year, month, monthLength(year, month)))) ||
                            employeeAllowance.getEndDate().getMonthValue() == month)) {
                allowances.add(employeeAllowance.getAllowance());
            }
        }
        return allowances;
    }

    @Override
    public BigDecimal getTotalAllowance(String employeeCode, int year, int month) {
        Employee employee = employeeRepositories.findByEmployeeCode(employeeCode);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }
        List<EmployeeAllowance> employeeAllowances = employeeAllowanceRepositories.findByEmployee(employee);
        BigDecimal totalAllowance = BigDecimal.ZERO;
        for (EmployeeAllowance employeeAllowance : employeeAllowances) {
            if (employeeAllowance.getStartDate().getYear() == year &&
                    employeeAllowance.getStartDate().getMonthValue() <= month &&
                    employeeAllowance.getEndDate().isAfter(YearMonth.from(LocalDate.of(year, month, monthLength(year, month)))) ||
                    employeeAllowance.getEndDate().getMonthValue() == month) {
                totalAllowance = totalAllowance.add(employeeAllowance.getAllowance().getAllowanceAmount());
            }
        }
        return totalAllowance;
    }

    // Hàm tính toán số ngày trong một tháng
    private int monthLength(int year, int month) {
        return LocalDate.of(year, month, 1).lengthOfMonth();
    }

    private void validateAllowanceDates(YearMonth startDate, YearMonth endDate) {
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfMonth = startDate.atDay(1);
        LocalDate endOfMonth = endDate.atEndOfMonth();

        if (endDate.isBefore(startDate)) {
            throw new RuntimeException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu");
        }
        if (startOfMonth.isBefore(currentDate.plusMonths(1))) {
            throw new RuntimeException("Tháng bắt đầu phải là tháng sau tháng hiện tại");
        }
        if (endOfMonth.isBefore(currentDate.plusMonths(1))) {
            throw new RuntimeException("Tháng kết thúc phải là tháng sau tháng hiện tại");
        }
    }


    @Override
    public EmployeeAllowance addEmployeeAllowance(String employeeCode, EmployeeAllowance employeeAllowance) {
        if (Objects.isNull(employeeAllowance.getStartDate()) || Objects.isNull(employeeAllowance.getEndDate())) {
            throw new IllegalArgumentException("Vui lòng nhập ngày bắt đầu và ngày kết thúc");
        }
        Employee employee = employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);
        Allowance allowance = allowanceRepositories.findById(employeeAllowance.getAllowance().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy trợ cấp với id: " + employeeAllowance.getAllowance().getId()));

        validateAllowanceDates(employeeAllowance.getStartDate(), employeeAllowance.getEndDate());

        EmployeeAllowance existingAllowance = employeeAllowanceRepositories.findByEmployeeAndAllowance(employee, allowance);
        if (existingAllowance != null) {
            throw new RuntimeException("Nhân viên đã nhận được trợ cấp này");
        }

        employeeAllowance.setEmployee(employee);
        employeeAllowance.setAllowance(allowance);
        return employeeAllowanceRepositories.save(employeeAllowance);
    }

    @Override
    public EmployeeAllowance updateEmployeeAllowance(String employeeCode, Long employeeAllowanceId, EmployeeAllowance newEmployeeAllowance) {
        if (Objects.isNull(newEmployeeAllowance.getStartDate()) || Objects.isNull(newEmployeeAllowance.getEndDate())) {
            throw new IllegalArgumentException("Vui lòng nhập ngày bắt đầu và ngày kết thúc");
        }

        Employee employee = employeeRepositories.findByEmployeeCode(employeeCode);
        if (employee == null) {
            throw new RuntimeException("Nhân viên không tồn tại");
        }
        EmployeeAllowance employeeAllowance = employeeAllowanceRepositories.findById(employeeAllowanceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trợ cấp của nhân viên này"));

        validateAllowanceDates(newEmployeeAllowance.getStartDate(), newEmployeeAllowance.getEndDate());

        Allowance allowance;
        if (newEmployeeAllowance.getAllowance() != null) {
            allowance = allowanceRepositories.findById(newEmployeeAllowance.getAllowance().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy trợ cấp"));
        } else {
            allowance = employeeAllowance.getAllowance();
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
