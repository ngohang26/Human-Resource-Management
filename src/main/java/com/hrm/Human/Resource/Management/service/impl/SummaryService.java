package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.dto.ReportData;
import com.hrm.Human.Resource.Management.dto.Summary;
import com.hrm.Human.Resource.Management.entity.MonthlyReport;
import com.hrm.Human.Resource.Management.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class SummaryService {

    @Autowired
    private CandidateRepositories candidateRepositories;

    @Autowired
    private EmployeeRepositories employeeRepositories;

    @Autowired
    private AttendanceRepositories attendanceRepositories;

    @Autowired
    private EmployeeSalaryRecordRepositories employeeSalaryRecordRepositories;

    @Autowired
    private MonthlyReportRepositories monthlyReportRepositories;

    @Autowired
    private UserRepositories userRepositories;

    public Summary getSummary() {
        long candidateCount = candidateRepositories.count();
        long employeeCount = employeeRepositories.count();
        long userCount = userRepositories.count();

        return new Summary(candidateCount, employeeCount, userCount);
    }

    public ReportData generateReport(int month, int year) {
        ReportData reportData = new ReportData();

        // Tính toán số nhân viên và tỷ lệ thay đổi
        int currentMonthEmployeeCount = employeeRepositories.countEmployeesByMonth(month, year);
        int previousMonthEmployeeCount = employeeRepositories.countEmployeesByMonth(month - 1, year);
        double employeePercentageChange = calculatePercentageChange(currentMonthEmployeeCount, previousMonthEmployeeCount);
        reportData.setEmployeeCount(currentMonthEmployeeCount);
        reportData.setEmployeePercentageChange(employeePercentageChange);

        // Tính toán tổng lương và tỷ lệ thay đổi
        BigDecimal currentMonthTotalSalary = employeeSalaryRecordRepositories.sumNetSalariesByMonth(month, year);
        BigDecimal previousMonthTotalSalary = employeeSalaryRecordRepositories.sumNetSalariesByMonth(month - 1, year);
        double salaryPercentageChange = calculatePercentageChange(currentMonthTotalSalary.doubleValue(), previousMonthTotalSalary.doubleValue());

        reportData.setTotalSalary(currentMonthTotalSalary);
        reportData.setSalaryPercentageChange(salaryPercentageChange);
        // Tính toán tổng số giờ làm và tỷ lệ thay đổi
        long currentMonthTotalWorkHours = attendanceRepositories.sumWorkTimeByMonthAndYear(month, year);
        long previousMonthTotalWorkHours = attendanceRepositories.sumWorkTimeByMonthAndYear(month - 1, year);
        double workHoursPercentageChange = calculatePercentageChange(currentMonthTotalWorkHours, previousMonthTotalWorkHours);

        reportData.setTotalWorkHours(currentMonthTotalWorkHours);
        reportData.setWorkHoursPercentageChange(workHoursPercentageChange);

        MonthlyReport monthlyReport = new MonthlyReport();
        monthlyReport.setMonth(month);
        monthlyReport.setYear(year);
        monthlyReport.setEmployeeCount(reportData.getEmployeeCount());
        monthlyReport.setEmployeePercentageChange(reportData.getEmployeePercentageChange());
        monthlyReport.setTotalSalary(reportData.getTotalSalary());
        monthlyReport.setSalaryPercentageChange(reportData.getSalaryPercentageChange());
        monthlyReport.setTotalWorkHours(reportData.getTotalWorkHours());
        monthlyReport.setWorkHoursPercentageChange(reportData.getWorkHoursPercentageChange());
        monthlyReportRepositories.save(monthlyReport);

        return reportData;    }

    private double calculatePercentageChange(double currentValue, double previousValue) {
        if (previousValue == 0) {
            return 100.0;
        } else {
            return ((currentValue - previousValue) / previousValue) * 100.0;
        }
    }
}

