package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.dto.EmployeeAllowanceDTO;
import com.hrm.Human.Resource.Management.entity.*;
import com.hrm.Human.Resource.Management.repositories.AttendanceRepositories;
import com.hrm.Human.Resource.Management.repositories.ContractRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.service.AllowanceService;
import com.hrm.Human.Resource.Management.service.AttendanceService;
import com.hrm.Human.Resource.Management.service.EmployeeSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    @Autowired
    private AllowanceService allowanceService;

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
            BigDecimal totalAllowance = allowanceService.getTotalAllowance(employeeCode);

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
        BigDecimal totalAllowance = allowanceService.getTotalAllowance(employeeCode);

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

//    @Override
//    public Long getTotalOvertimeHours(String employeeCode, int year, int month) {
//        LocalDate startDate = LocalDate.of(year, month, 1);
//        LocalDate endDate = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());
//
//        List<Attendance> attendances = attendanceRepositories.findByEmployee_EmployeeCodeAndDateBetween(employeeCode, startDate, endDate);
//        return attendances.stream().mapToLong(Attendance::getOverTime).sum();
//    }
//
//    private BigDecimal calculateOvertimeSalary(BigDecimal monthlySalary, Long totalOvertimeHours, BigDecimal totalAllowance) {
//        BigDecimal workDaysPerMonth = BigDecimal.valueOf(26);
//        BigDecimal workHoursPerDay = BigDecimal.valueOf(8);
//
//        BigDecimal hourlyRate = (monthlySalary.add(totalAllowance))
//                .divide(workDaysPerMonth, 0, RoundingMode.DOWN)
//                .divide(workHoursPerDay, 0, RoundingMode.DOWN);
//
//        return hourlyRate.multiply(BigDecimal.valueOf(totalOvertimeHours)).multiply(BigDecimal.valueOf(1.5));
//    }

//    public BigDecimal calculateOvertimeSalaryForEmployee(String employeeCode, int year, int month) {
//        Employee employee = employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);
//        BigDecimal monthlySalary = getMonthlySalary(employeeCode);
//        Long totalOvertimeHours = getTotalOvertimeHours(employeeCode, year, month);
//        BigDecimal totalAllowance = allowanceService.calculateTotalAllowanceAmountForEachEmployee().get(employeeCode);
//
//        if (monthlySalary == null || totalOvertimeHours == null || totalAllowance == null) {
//            return null;
//        }
//
//        return calculateOvertimeSalary(monthlySalary, totalOvertimeHours, totalAllowance);
//    }
//

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

    public Map<String, BigDecimal> getAllowances(String employeeCode) {
        List<Allowance> allowances = allowanceService.getAllowancesForEmployee(employeeCode);

        Map<String, BigDecimal> allowanceMap = new HashMap<>();
        for (Allowance allowance : allowances) {
            allowanceMap.put(allowance.getAllowanceName(), allowance.getAllowanceAmount());
        }

        return allowanceMap;
    }

    //
//    public Map<String, BigDecimal> getAllowances(String employeeCode) {
//        List<EmployeeAllowanceDTO> allowances = allowanceService.getEmployeeAllowances(employeeCode);
//
//        Map<String, BigDecimal> allowanceMap = new HashMap<>();
//        for (EmployeeAllowanceDTO allowance : allowances) {
//            allowanceMap.put(allowance.getAllowanceName(), allowance.getAllowanceAmount());
//        }
//
//        return allowanceMap;
//    }
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
    public BigDecimal calculateTotalIncome(String employeeCode, int year, int month) {
        BigDecimal monthlySalary = getMonthlySalary(employeeCode);
        BigDecimal totalAllowance = allowanceService.getTotalAllowance(employeeCode);
        Integer workingDaysInMonth = attendanceService.calculateWorkdays(year, month).get(employeeCode);
        BigDecimal insuranceInMonth = getTotalInsurance(employeeCode);

        if (monthlySalary == null || totalAllowance == null || workingDaysInMonth == null || insuranceInMonth == null) {
            return null;
        }

        return (monthlySalary.add(totalAllowance)).divide(BigDecimal.valueOf(26), 0, RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(workingDaysInMonth)).subtract(insuranceInMonth);
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


    public Map<String, BigDecimal> calculateTotalDeductionsForEachEmployee(int year, int month) {
        List<Employee> employees = employeeRepositories.findAll();
        Map<String, BigDecimal> totalDeductionsMap = new HashMap<>();

        for (Employee employee : employees) {
            String employeeCode = employee.getEmployeeCode();
            BigDecimal totalDeductions = calculateTotalDeductionsForEmployee(employeeCode, year, month);
            totalDeductionsMap.put(employeeCode, totalDeductions);
        }

        return totalDeductionsMap;
    }

    @Override
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

    @Override
    public EmployeeSalary getEmployeeSalaryDetails(String employeeCode, int year, int month) {
        Employee employee = employeeRepositories.findByEmployeeCode(employeeCode);
        if (employee == null) {
            return null; // Trả về null nếu nhân viên không tồn tại
        }
        String cacheKey = generateCacheKey(employeeCode, year, month);
        Cache.ValueWrapper valueWrapper = cacheManager.getCache("employeeSalaries").get(cacheKey);
        if (valueWrapper != null) {
            return (EmployeeSalary) valueWrapper.get();
        } else {
            EmployeeSalary details = calculateEmployeeSalaryForMonth(employeeCode, year, month);
            if (details != null) {
                cacheManager.getCache("employeeSalaries").put(cacheKey, details);
            }
            return details;
        }
    }

    public EmployeeSalary calculateEmployeeSalaryForMonth(String employeeCode, int year, int month) {
        Employee employee = employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);
        BigDecimal monthlySalary = getMonthlySalary(employeeCode);
        BigDecimal totalAllowance = allowanceService.getTotalAllowance(employeeCode);
        BigDecimal incomeTax = calculateIncomeTaxForEmployee(employeeCode, year, month);
        Map<String, Integer> workingDaysInMonth = attendanceService.calculateWorkdaysForEachEmployee(employeeCode, year, month);
        BigDecimal totalIncome = calculateTotalIncome(employeeCode, year, month);
        BigDecimal socialInsurance = calculateSocialInsurance(employeeCode);
        BigDecimal healthInsurance = calculateHealthInsurance(employeeCode);
        BigDecimal unemploymentInsurance = calculateUnemploymentInsurance(employeeCode);
        BigDecimal totalInsurance = getTotalInsurance(employeeCode);
        BigDecimal totalDeductions = calculateTotalDeductionsForEmployee(employeeCode, year, month);
        Map<String, BigDecimal> allowances = getAllowances(employeeCode);
        BigDecimal overTimeSalary = calculateOvertimeSalaryForEmployee(employeeCode, year, month);
        Long totalOverTimeHours = getTotalOvertimeHours(employeeCode, year, month);
        BigDecimal netSalary = calculateNetSalary(employeeCode, year, month);

        if (monthlySalary == null || totalAllowance == null || incomeTax == null || workingDaysInMonth == null || totalIncome == null || socialInsurance == null || healthInsurance == null || unemploymentInsurance == null || totalInsurance == null || allowances == null || overTimeSalary == null || totalOverTimeHours == null || netSalary == null) {
            return null; // Return null if any value is null
        }

        EmployeeSalary details = new EmployeeSalary();
        details.setId(employee.getId());
        details.setEmployeeName(employee.getFullName());
        details.setEmployeeCode(employee.getEmployeeCode());
        details.setPositionName(employee.getPositionName());
        details.setDepartmentName(employee.getDepartmentName());
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
    public List<EmployeeSalary> getAllEmployeeSalaryDetails(int year, int month) {
        List<Employee> employees = employeeRepositories.findAll();
        List<EmployeeSalary> allDetails = new ArrayList<>();

        for (Employee employee : employees) {
            String employeeCode = employee.getEmployeeCode();
            EmployeeSalary details = getEmployeeSalaryDetails(employeeCode, year, month);
            if (details != null) {
                allDetails.add(details);
            }
        }

        return allDetails;
    }

}
