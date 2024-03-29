package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.*;
import com.hrm.Human.Resource.Management.repositories.AttendanceRepositories;
import com.hrm.Human.Resource.Management.repositories.ContractRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.service.AllowanceService;
import com.hrm.Human.Resource.Management.service.AttendanceService;
import com.hrm.Human.Resource.Management.service.EmployeeSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public Map<Long, BigDecimal> calculateOvertimeSalaryForEachEmployee(int year, int month) {
        List<Employee> employees = employeeRepositories.findAll();
        Map<Long, BigDecimal> overtimeSalaries = new HashMap<>();

        for (Employee employee : employees) {
            Long employeeId = employee.getId();
            BigDecimal monthlySalary = getMonthlySalary(employeeId);
            Long totalOvertimeHours = getTotalOvertimeHours(employeeId, year, month);

            BigDecimal overtimeSalary = calculateOvertimeSalary(monthlySalary, totalOvertimeHours);
            overtimeSalaries.put(employeeId, overtimeSalary);
        }

        return overtimeSalaries;
    }
    @Override
    public Long getTotalOvertimeHours(Long employeeId, int year, int month) {
        List<Attendance> attendances = attendanceRepositories.findByEmployeeIdAndDateBetween(
                employeeId,
                LocalDate.of(year, month, 1),
                LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth())
        );
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

    @Override
    public Map<Long, BigDecimal> calculateInsuranceForEachEmployee() {
        List<Employee> employees = employeeRepositories.findAll();
        Map<Long, BigDecimal> insuranceAmounts = new HashMap<>();

        for (Employee employee : employees) {
            Long employeeId = employee.getId();
            BigDecimal monthlySalary = getMonthlySalary(employeeId);

            BigDecimal socialInsurance = monthlySalary.multiply(BigDecimal.valueOf(0.08)); // 8% for social insurance
            BigDecimal healthInsurance = monthlySalary.multiply(BigDecimal.valueOf(0.015)); // 1.5% for health insurance
            BigDecimal unemploymentInsurance = monthlySalary.multiply(BigDecimal.valueOf(0.01)); // 1% for unemployment insurance

            BigDecimal totalInsurance = socialInsurance.add(healthInsurance).add(unemploymentInsurance);
            totalInsurance = totalInsurance.setScale(0, RoundingMode.DOWN);
            insuranceAmounts.put(employeeId, totalInsurance);
        }
        return insuranceAmounts;
    }
    // luong net - sau thue
    @Override
    public BigDecimal calculateNetSalary(Long employeeId, int year, int month) {
        BigDecimal overTimeSalary = calculateOvertimeSalaryForEachEmployee(year, month).get(employeeId);
        BigDecimal netSalary = calculateTotalIncome(employeeId, year, month).subtract(overTimeSalary);
        return netSalary;
    }
    // luong truoc thue
    @Override
    public BigDecimal calculateTotalIncome(Long employeeId, int year, int month) {
        BigDecimal monthlySalary = getMonthlySalary(employeeId);
        BigDecimal totalAllowance = allowanceService.calculateTotalAllowanceAmountForEachEmployee().get(employeeId);
        int workingDaysInMonth = attendanceService.calculateWorkdays(year, month).get(employeeId); // Sử dụng phương thức đã chỉnh sửa
        BigDecimal insuranceInMonth = calculateInsuranceForEachEmployee().get(employeeId);
        BigDecimal totalIncome = (monthlySalary.add(totalAllowance)).divide(BigDecimal.valueOf(26), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(workingDaysInMonth)).subtract(insuranceInMonth);
        return totalIncome;
    }

    @Override
    public Map<Long, BigDecimal> calculateIncomeTaxForEachEmployee(int year, int month) {
        List<Employee> employees = employeeRepositories.findAll();
        Map<Long, BigDecimal> incomeTaxes = new HashMap<>();

        for (Employee employee : employees) {
            Long employeeId = employee.getId();
            BigDecimal taxableIncome = calculateTotalIncome(employeeId, year, month);
            boolean isResident = employee.getPersonalInfo().isResident();
            Contract contract = employee.getContract();
            BigDecimal incomeTax = calculateIncomeTax(taxableIncome, isResident, contract.getStartDate(), contract.getEndDate());
            incomeTaxes.put(employeeId, incomeTax);
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

    @Override
    public EmployeeSalary getEmployeeSalaryDetails(Long employeeId, int year, int month) {
            Employee employee = employeeRepositories.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with id " + employeeId));

        BigDecimal monthlySalary = getMonthlySalary(employeeId);
        BigDecimal totalAllowance = allowanceService.calculateTotalAllowanceAmountForEachEmployee().get(employeeId);
        BigDecimal incomeTax = calculateIncomeTaxForEachEmployee(year, month).get(employeeId);
        int workingDaysInMonth = attendanceService.calculateWorkdays(year, month).get(employeeId);
        BigDecimal totalIncome = calculateTotalIncome(employeeId, year, month);

        EmployeeSalary details = new EmployeeSalary();
        details.setEmployeeName(employee.getFullName());
        details.setEmployeeCode(employee.getEmployeeCode());
        details.setMonthlySalary(monthlySalary);
        details.setTotalAllowance(totalAllowance);
        details.setIncomeTax(incomeTax);
        details.setWorkingDaysInMonth(workingDaysInMonth);
        details.setTotalIncome(totalIncome);

        return details;
    }

    @Override
    public List<EmployeeSalary> getAllEmployeeSalaryDetails(int year, int month) {
        List<Employee> employees = employeeRepositories.findAll();
        List<EmployeeSalary> allDetails = new ArrayList<>();

        for (Employee employee : employees) {
            Long employeeId = employee.getId();
            EmployeeSalary details = getEmployeeSalaryDetails(employeeId, year, month);
            allDetails.add(details);
        }

        return allDetails;
    }


//    public void calculateTax(Long employeeId) {
//        BigDecimal monthlySalary = getMonthlySalary(employeeId);
//        BigDecimal taxableIncome = BigDecimal.valueOf(monthlySalary).add(totalAllowance);
//        BigDecimal taxRate;
//        BigDecimal taxDeduction;
//        if (isResident) {
//            if (contractDuration >= 3) {
//                if (taxableIncome.compareTo(BigDecimal.valueOf(5000000)) <= 0) {
//                    taxRate = new BigDecimal("0.05");
//                    taxDeduction = BigDecimal.ZERO;
//                } else if (taxableIncome.compareTo(BigDecimal.valueOf(10000000)) <= 0) {
//                    taxRate = new BigDecimal("0.1");
//                    taxDeduction = BigDecimal.valueOf(250000);
//                } else if (taxableIncome.compareTo(BigDecimal.valueOf(18000000)) <= 0) {
//                    taxRate = new BigDecimal("0.15");
//                    taxDeduction = BigDecimal.valueOf(750000);
//                } else if (taxableIncome.compareTo(BigDecimal.valueOf(32000000)) <= 0) {
//                    taxRate = new BigDecimal("0.2");
//                    taxDeduction = BigDecimal.valueOf(1650000);
//                } else if (taxableIncome.compareTo(BigDecimal.valueOf(52000000)) <= 0) {
//                    taxRate = new BigDecimal("0.25");
//                    taxDeduction = BigDecimal.valueOf(3250000);
//                } else if (taxableIncome.compareTo(BigDecimal.valueOf(80000000)) <= 0) {
//                    taxRate = new BigDecimal("0.3");
//                    taxDeduction = BigDecimal.valueOf(5850000);
//                } else {
//                    taxRate = new BigDecimal("0.35");
//                    taxDeduction = BigDecimal.valueOf(9850000);
//                }
//            } else {
//                if (taxableIncome.compareTo(BigDecimal.valueOf(2000000)) > 0) {
//                    taxRate = new BigDecimal("0.1");
//                } else {
//                    taxRate = BigDecimal.ZERO;
//                }
//                taxDeduction = BigDecimal.ZERO;
//            }
//        } else {
//            taxRate = new BigDecimal("0.2");
//            taxDeduction = BigDecimal.ZERO;
//        }
//        tax = taxableIncome.multiply(taxRate).subtract(taxDeduction);
//    }


    }
