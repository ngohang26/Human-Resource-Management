package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.*;
import com.hrm.Human.Resource.Management.repositories.*;
import com.hrm.Human.Resource.Management.service.AllowanceService;
import com.hrm.Human.Resource.Management.service.AttendanceService;
import com.hrm.Human.Resource.Management.service.EmployeeAllowanceService;
import com.hrm.Human.Resource.Management.service.EmployeeSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
//@EnableScheduling
public class EmployeeSalaryServiceImpl implements EmployeeSalaryService {
    @Autowired
    private EmployeeRepositories employeeRepositories;

    @Autowired
    private ContractRepositories contractRepositories;

    @Autowired
    private AttendanceRepositories attendanceRepositories;

    @Autowired
    private DepartmentRepositories departmentRepositories;

    @Autowired
    private EmployeeSalaryRecordRepositories employeeSalaryRecordRepositories;

    @Autowired
    private AllowanceService allowanceService;

    @Autowired
    private EmployeeAllowanceService employeeAllowanceService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public boolean employeeExists(String employeeCode) {
        return employeeRepositories.findByEmployeeCode(employeeCode) != null;
    }

    @Override
    public boolean employeeDataExists(String employeeCode, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());
        List<Attendance> attendances = attendanceRepositories.findByEmployee_EmployeeCodeAndDateBetween(employeeCode, startDate, endDate);
        return !attendances.isEmpty();
    }


    @Override
    public Map<String, BigDecimal> calculateOvertimeSalaryForEachEmployee(int year, int month) {
        List<Employee> employees = employeeRepositories.findAll();
        Map<String, BigDecimal> overtimeSalaries = new HashMap<>();

        for (Employee employee : employees) {
            String employeeCode = employee.getEmployeeCode();
            BigDecimal monthlySalary = getMonthlySalary(employeeCode);
            Long totalOvertimeHours = getTotalOvertimeHours(employeeCode, year, month);
            BigDecimal totalAllowance = employeeAllowanceService.getTotalAllowance(employeeCode, year, month);

            if (monthlySalary == null || totalOvertimeHours == null || totalAllowance == null) {
                continue;
            }

            BigDecimal overtimeSalary = calculateOvertimeSalary(monthlySalary, totalOvertimeHours, totalAllowance);
            overtimeSalaries.put(employeeCode, overtimeSalary);
        }

        return overtimeSalaries;
    }

    public BigDecimal calculateOvertimeSalaryForEmployee(String employeeCode, int year, int month) {
        BigDecimal monthlySalary = getMonthlySalary(employeeCode);
        Long totalOvertimeHours = getTotalOvertimeHours(employeeCode, year, month);
        BigDecimal totalAllowance = employeeAllowanceService.getTotalAllowance(employeeCode, year, month);

        if (monthlySalary == null || totalOvertimeHours == null || totalAllowance == null) {
            return BigDecimal.ZERO;
        }

        return calculateOvertimeSalary(monthlySalary, totalOvertimeHours, totalAllowance);
    }

    public Long getTotalOvertimeHours(String employeeCode, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());

        List<Attendance> attendances = attendanceRepositories.findByEmployee_EmployeeCodeAndDateBetween(employeeCode, startDate, endDate);
        return attendances.stream().mapToLong(Attendance::getOverTime).sum();
    }

    private BigDecimal calculateOvertimeSalary(BigDecimal monthlySalary, Long totalOvertimeHours, BigDecimal totalAllowance) {
        BigDecimal workDaysPerMonth = BigDecimal.valueOf(26);
        BigDecimal workHoursPerDay = BigDecimal.valueOf(8);

        BigDecimal hourlyRate = (monthlySalary.add(totalAllowance))
                .divide(workDaysPerMonth, 0, RoundingMode.DOWN)
                .divide(workHoursPerDay, 0, RoundingMode.DOWN);

        return hourlyRate.multiply(BigDecimal.valueOf(totalOvertimeHours)).multiply(BigDecimal.valueOf(1.5));
    }

    private BigDecimal getMonthlySalary(String employeeCode) {
        Employee employee = employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);
        if (employee != null) {
            Contract contract = employee.getContract();
            if (contract != null) {
                return contract.getMonthlySalary();
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateSocialInsurance(String employeeCode) {
        BigDecimal monthlySalary = getMonthlySalary(employeeCode);
        return monthlySalary.multiply(BigDecimal.valueOf(0.08));
    }

    public BigDecimal calculateHealthInsurance(String employeeCode) {
        BigDecimal monthlySalary = getMonthlySalary(employeeCode);
        return monthlySalary.multiply(BigDecimal.valueOf(0.015));
    }

    public BigDecimal calculateUnemploymentInsurance(String employeeCode) {
        BigDecimal monthlySalary = getMonthlySalary(employeeCode);
        return monthlySalary.multiply(BigDecimal.valueOf(0.01));
    }

    @Override
    public BigDecimal getTotalInsurance(String employeeCode) {
        BigDecimal socialInsurance = calculateSocialInsurance(employeeCode);
        BigDecimal healthInsurance = calculateHealthInsurance(employeeCode);
        BigDecimal unemploymentInsurance = calculateUnemploymentInsurance(employeeCode);

        return socialInsurance.add(healthInsurance).add(unemploymentInsurance);
    }

    public Map<String, BigDecimal> getAllowances(String employeeCode, int year, int month) {
        List<Allowance> allowances = employeeAllowanceService.getAllowancesForEmployee(employeeCode, year, month);

        Map<String, BigDecimal> allowanceMap = new HashMap<>();
        for (Allowance allowance : allowances) {
            allowanceMap.put(allowance.getAllowanceName(), allowance.getAllowanceAmount());
        }

        return allowanceMap;
    }

    // luong net - sau thue
    @Override
    public BigDecimal calculateNetSalary(String employeeCode, int year, int month) {
        BigDecimal overTimeSalary = calculateOvertimeSalaryForEmployee(employeeCode, year, month);
        BigDecimal incomeTax = calculateIncomeTaxForEmployee(employeeCode, year, month);
        BigDecimal totalIncome = calculateTotalIncome(employeeCode, year, month);

        if (overTimeSalary == null || incomeTax == null || totalIncome == null) {
            return null;
        }

        return totalIncome.subtract(incomeTax).add(overTimeSalary);
    }


    // luong truoc thue moi empl
    @Override
//    public BigDecimal calculateTotalIncome(String employeeCode, int year, int month) {
//        BigDecimal monthlySalary = getMonthlySalary(employeeCode);
//        BigDecimal totalAllowance = employeeAllowanceService.getTotalAllowance(employeeCode, year, month);
//        Integer workingDaysInMonth = attendanceService.calculateWorkdays(year, month).get(employeeCode);
//        BigDecimal insuranceInMonth = getTotalInsurance(employeeCode);
//
//        if (monthlySalary == null || workingDaysInMonth == null || insuranceInMonth == null) {
//            return null;
//        }
//
//        return (monthlySalary.add(totalAllowance)).divide(BigDecimal.valueOf(26), 0, RoundingMode.DOWN)
//                .multiply(BigDecimal.valueOf(workingDaysInMonth)).subtract(insuranceInMonth);
//    }

    public BigDecimal calculateTotalIncome(String employeeCode, int year, int month) {
        BigDecimal monthlySalary = getMonthlySalary(employeeCode);
        BigDecimal totalAllowance = employeeAllowanceService.getTotalAllowance(employeeCode, year, month);
        Integer workingDaysInMonth = attendanceService.calculateWorkdays(year, month).get(employeeCode);
        BigDecimal insuranceInMonth = getTotalInsurance(employeeCode);

        if (monthlySalary == null || workingDaysInMonth == null || insuranceInMonth == null) {
            return null;
        }

        BigDecimal totalIncome;

        // Kiểm tra số ngày làm việc và áp dụng các quy tắc về bảo hiểm ( nghỉ 14 ngày ngày trở lên là không đóng)
        BigDecimal multiply = (monthlySalary.add(totalAllowance)).divide(BigDecimal.valueOf(26), 0, RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(workingDaysInMonth));
        if (workingDaysInMonth > 13) {
            totalIncome = multiply.subtract(insuranceInMonth);
        } else {
            totalIncome = multiply;
        }

        return totalIncome;
    }
    // luong truoc thue all

    @Override
    public BigDecimal calculateIncomeTaxForEmployee(String employeeCode, int year, int month) {
        Employee employee = employeeRepositories.findByEmployeeCode(employeeCode);
        if (employee == null) {
            return BigDecimal.ZERO;
        }

        Contract contract = employee.getContract();
        if (contract == null) {
            return BigDecimal.ZERO;
        }

        LocalDate startDate = contract.getStartDate();
        if (startDate == null || startDate.getYear() > year || (startDate.getYear() == year && startDate.getMonthValue() > month)) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxableIncome = calculateTotalIncome(employeeCode, year, month);
        if (taxableIncome == null) {
            return BigDecimal.ZERO;
        }

        boolean isResident = employee.getPersonalInfo().isResident();
        return calculateIncomeTax(taxableIncome, isResident, startDate, contract.getEndDate());
    }

    @Override
    public Map<String, BigDecimal> calculateIncomeTaxForEachEmployee(int year, int month) {
        List<Employee> employees = employeeRepositories.findAll();
        Map<String, BigDecimal> incomeTaxes = new HashMap<>();

        for (Employee employee : employees) {
            String employeeCode = employee.getEmployeeCode();
            Contract contract = employee.getContract();
            if (contract == null) {
                continue;
            }
            LocalDate startDate = contract.getStartDate();
            if (startDate == null || startDate.getYear() > year || (startDate.getYear() == year && startDate.getMonthValue() > month)) {
                continue;
            }
            BigDecimal taxableIncome = calculateTotalIncome(employeeCode, year, month);
            if (taxableIncome == null) {
                continue;
            }
            boolean isResident = employee.getPersonalInfo().isResident();
            BigDecimal incomeTax = calculateIncomeTax(taxableIncome, isResident, startDate, contract.getEndDate());
            incomeTaxes.put(employeeCode, incomeTax);
        }
        return incomeTaxes;
    }

    private BigDecimal calculateIncomeTax(BigDecimal taxableIncome, boolean isResident, LocalDate startDate, LocalDate endDate) {
        long durationInMonths = ChronoUnit.MONTHS.between(startDate, endDate);
        BigDecimal taxRate;
        BigDecimal taxDeduction;
        BigDecimal incomeTax;
        if (isResident) {
            if (durationInMonths >= 3) {
                if (taxableIncome.compareTo(BigDecimal.valueOf(5000000)) <= 0) {
                    taxRate = new BigDecimal("0.05");
                    taxDeduction = BigDecimal.ZERO;
                } else if (taxableIncome.compareTo(BigDecimal.valueOf(10000000)) <= 0) {
                    taxRate = new BigDecimal("0.1");
                    taxDeduction = BigDecimal.valueOf(250000);
                } else if (taxableIncome.compareTo(BigDecimal.valueOf(18000000)) <= 0) {
                    taxRate = new BigDecimal("0.15");
                    taxDeduction = BigDecimal.valueOf(750000);
                } else if (taxableIncome.compareTo(BigDecimal.valueOf(32000000)) <= 0) {
                    taxRate = new BigDecimal("0.2");
                    taxDeduction = BigDecimal.valueOf(1650000);
                } else if (taxableIncome.compareTo(BigDecimal.valueOf(52000000)) <= 0) {
                    taxRate = new BigDecimal("0.25");
                    taxDeduction = BigDecimal.valueOf(3250000);
                } else if (taxableIncome.compareTo(BigDecimal.valueOf(80000000)) <= 0) {
                    taxRate = new BigDecimal("0.3");
                    taxDeduction = BigDecimal.valueOf(5850000);
                } else {
                    taxRate = new BigDecimal("0.35");
                    taxDeduction = BigDecimal.valueOf(9850000);
                }
            } else {
                if (taxableIncome.compareTo(BigDecimal.valueOf(2000000)) > 0) {
                    taxRate = new BigDecimal("0.1");
                } else {
                    taxRate = BigDecimal.ZERO;
                }
                taxDeduction = BigDecimal.ZERO;
            }
        } else {
            taxRate = new BigDecimal("0.2");
            taxDeduction = BigDecimal.ZERO;
        }

        incomeTax = taxableIncome.multiply(taxRate).subtract(taxDeduction).setScale(0, RoundingMode.DOWN);

        return incomeTax;
    }

    public BigDecimal calculateTotalDeductionsForEmployee(String employeeCode, int year, int month) {
        BigDecimal totalInsurance = getTotalInsurance(employeeCode);
        BigDecimal incomeTax = calculateIncomeTaxForEmployee(employeeCode, year, month);

        if (totalInsurance == null || incomeTax == null) {
            return BigDecimal.ZERO;
        }

        return totalInsurance.add(incomeTax);
    }

    public void calculateAndCacheEmployeeSalariesForMonth(int year, int month) {
        // Tính toán dữ liệu cho tất cả nhân viên và lưu vào bộ nhớ cache
        List<Employee> employees = employeeRepositories.findAll();
        for (Employee employee : employees) {
            EmployeeSalary details = calculateEmployeeSalaryForMonth(employee.getEmployeeCode(), year, month);
            if (details != null) {
                String cacheKey = generateCacheKey(employee.getEmployeeCode(), year, month);
                cacheManager.getCache("employeeSalaries").put(cacheKey, details);
            }
        }
    }

    public EmployeeSalary calculateEmployeeSalaryForMonth(String employeeCode, int year, int month) {
        Employee employee = employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);
        BigDecimal monthlySalary = getMonthlySalary(employeeCode);
        BigDecimal totalAllowance = employeeAllowanceService.getTotalAllowance(employeeCode, year, month);
        BigDecimal incomeTax = calculateIncomeTaxForEmployee(employeeCode, year, month);
        Map<String, Integer> workingDaysInMonth = attendanceService.calculateWorkdaysForEachEmployee(employeeCode, year, month);
        BigDecimal totalIncome = calculateTotalIncome(employeeCode, year, month);
        BigDecimal socialInsurance = calculateSocialInsurance(employeeCode);
        BigDecimal healthInsurance = calculateHealthInsurance(employeeCode);
        BigDecimal unemploymentInsurance = calculateUnemploymentInsurance(employeeCode);
        BigDecimal totalInsurance = getTotalInsurance(employeeCode);
        BigDecimal totalDeductions = calculateTotalDeductionsForEmployee(employeeCode, year, month);
        Map<String, BigDecimal> allowances = getAllowances(employeeCode, year, month);
        BigDecimal overTimeSalary = calculateOvertimeSalaryForEmployee(employeeCode, year, month);
        Long totalOverTimeHours = getTotalOvertimeHours(employeeCode, year, month);
        BigDecimal netSalary = calculateNetSalary(employeeCode, year, month);

        if (monthlySalary == null || totalAllowance == null || incomeTax == null || workingDaysInMonth == null || totalIncome == null || socialInsurance == null || healthInsurance == null || unemploymentInsurance == null || totalInsurance == null || allowances == null || overTimeSalary == null || totalOverTimeHours == null || netSalary == null) {
            return null; // Return null if any value is null
        }

        EmployeeSalary details = new EmployeeSalary();
        details.setId(employee.getId());

        details.setFullName(employee.getFullName());
        details.setEmployeeCode(employee.getEmployeeCode());
        details.setPositionName(employee.getPosition().getPositionName());
        details.setDepartmentName(employee.getDepartment().getDepartmentName());
        details.setMonthlySalary(monthlySalary);
        details.setTotalAllowance(totalAllowance);
        details.setIncomeTax(incomeTax); // thue
        int totalWorkingDays = workingDaysInMonth.values().stream().mapToInt(Integer::intValue).sum();
        details.setWorkingDaysInMonth(totalWorkingDays);
        details.setTotalIncome(totalIncome); // luong truoc thue
        details.setSocialInsurance(socialInsurance);
        details.setHealthInsurance(healthInsurance);
        details.setUnemploymentInsurance(unemploymentInsurance);
        details.setTotalInsurance(totalInsurance);
        details.setTotalDeductions(totalDeductions);
        details.setAllowances(allowances);
        details.setTotalOvertimeHours(totalOverTimeHours);
        details.setOverTimeSalary(overTimeSalary);
        details.setNetSalary(netSalary);
        return details;
    }

    private String generateCacheKey(String employeeCode, int year, int month) {
        return employeeCode + "-" + year + "-" + month;
    }

    @Override
    public List<EmployeeSalaryRecord> getAllEmployeeSalaryRecords(int year, int month) {
        return employeeSalaryRecordRepositories.findByYearAndMonth(year, month);
    }


    @Override
    public Map<String, Long> calculateTotalOvertimeHoursByDepartment(int year, int month) {
        List<Department> departments = departmentRepositories.findAll();
        Map<String, Long> totalOvertimeHoursByDepartment = new HashMap<>();

        for (Department department : departments) {
            List<EmployeeSalaryRecord> records = employeeSalaryRecordRepositories.findByYearAndMonthAndDepartmentName(year, month, department.getDepartmentName());
            long totalOvertimeHours = records.stream().mapToLong(EmployeeSalaryRecord::getTotalOvertimeHours).sum();
            totalOvertimeHoursByDepartment.put(department.getDepartmentName(), totalOvertimeHours);
        }

        return totalOvertimeHoursByDepartment;
    }

    @Override
    public Map<String, BigDecimal> calculateTotalIncomeTaxByDepartment(int year, int month) {
        List<Department> departments = departmentRepositories.findAll();
        Map<String, BigDecimal> totalIncomeTaxByDepartment = new HashMap<>();

        for (Department department : departments) {
            List<EmployeeSalaryRecord> records = employeeSalaryRecordRepositories.findByYearAndMonthAndDepartmentName(year, month, department.getDepartmentName());
            BigDecimal totalIncomeTax = records.stream().map(EmployeeSalaryRecord::getIncomeTax).reduce(BigDecimal.ZERO, BigDecimal::add);
            totalIncomeTaxByDepartment.put(department.getDepartmentName(), totalIncomeTax);
        }

        return totalIncomeTaxByDepartment;
    }

    @Override
    public Map<String, Long> calculateTotalOvertimeHoursPerMonth(int year, int month) {
        List<Employee> employees = employeeRepositories.findAllByEmploymentStatus(Employee.EmploymentStatus.ACTIVE);
        Map<String, Long> totalOvertimeHoursPerMonth = new HashMap<>();

        for (Employee employee : employees) {
            EmployeeSalaryRecord record = employeeSalaryRecordRepositories.findByEmployeeCodeAndYearAndMonth(employee.getEmployeeCode(), year, month);
            totalOvertimeHoursPerMonth.put(employee.getEmployeeCode(), record != null ? record.getTotalOvertimeHours() : 0);
        }

        return totalOvertimeHoursPerMonth;
    }

    @Override
    public void updateEmployeeSalaryRecord(String employeeCode, int year, int month) {
        EmployeeSalary details = calculateEmployeeSalaryForMonth(employeeCode, year, month);
        if (details != null) {
            Employee employee = employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);

            EmployeeSalaryRecord record = employeeSalaryRecordRepositories.findByEmployeeCodeAndYearAndMonth(employeeCode, year, month);
            if (record == null) {
                record = new EmployeeSalaryRecord();
                record.setEmployee(employee);
                record.setEmployeeCode(employee.getEmployeeCode());
                record.setFullName(employee.getFullName());
                record.setDepartmentName(employee.getDepartment().getDepartmentName());
                record.setPositionName(employee.getPosition().getPositionName());
            }

            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());
            List<Attendance> attendances = attendanceRepositories.findByEmployee_EmployeeCodeAndDateBetween(employeeCode, startDate, endDate);
            long totalWorkingHours = attendances.stream().mapToLong(Attendance::getWorkTime).sum();

            record.setYear(year);
            record.setMonth(month);
            record.setMonthlySalary(details.getMonthlySalary());
            record.setTotalAllowance(details.getTotalAllowance());
            record.setIncomeTax(details.getIncomeTax());
            record.setWorkingDaysInMonth(details.getWorkingDaysInMonth());
            record.setTotalIncome(details.getTotalIncome());
            if (details.getWorkingDaysInMonth() > 13) {
                record.setSocialInsurance(details.getSocialInsurance());
                record.setHealthInsurance(details.getHealthInsurance());
                record.setUnemploymentInsurance(details.getUnemploymentInsurance());
                record.setTotalInsurance(details.getTotalInsurance());
                record.setTotalDeductions(details.getTotalDeductions());
            } else {
                record.setSocialInsurance(BigDecimal.ZERO);
                record.setHealthInsurance(BigDecimal.ZERO);
                record.setUnemploymentInsurance(BigDecimal.ZERO);
                record.setTotalInsurance(BigDecimal.ZERO);
                record.setTotalDeductions(details.getIncomeTax());
            }
            record.setOverTimeSalary(details.getOverTimeSalary());
            record.setTotalWorkingHours(totalWorkingHours);
            record.setTotalOvertimeHours(details.getTotalOvertimeHours());
            record.setNetSalary(details.getNetSalary());

            employeeSalaryRecordRepositories.save(record);
        }
    }

    @Override
    public void updateAllEmployeeSalaryRecords(int year, int month) {
        List<Employee> employees = employeeRepositories.findAll();
        for (Employee employee : employees) {
            String employeeCode = employee.getEmployeeCode();
            updateEmployeeSalaryRecord(employeeCode, year, month);
        }
    }

    @Override
    public EmployeeSalaryRecord getEmployeeSalaryRecord(String employeeCode, int year, int month) {
        return employeeSalaryRecordRepositories.findByEmployeeCodeAndYearAndMonth(employeeCode, year, month);
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void updateAllEmployeeSalaryRecordsForLastMonth() {
        // Lấy tháng và năm hiện tại
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        // Trừ đi 1 để lấy tháng trước
        if (month == 1) {
            month = 12;
            year--;
        } else {
            month--;
        }

        updateAllEmployeeSalaryRecords(year, month);
    }

}
