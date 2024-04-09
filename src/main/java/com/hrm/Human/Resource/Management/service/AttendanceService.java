package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.dto.AttendanceDTO;
import com.hrm.Human.Resource.Management.entity.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
//    List<Attendance> createWorkTimes();

    List<Attendance> createWorkTimes(LocalDate date);

    List<AttendanceDTO> getAllAttendances();
    List<AttendanceDTO> getAttendancesByEmployee(String employeeCode);

    List<AttendanceDTO> getAttendancesByDate(LocalDate date);



    List<AttendanceDTO> getAttendancesByMonthAndYear(String employeeCode, int month, int year);

    List<AttendanceDTO> getAttendancesByYearAndMonth(int year, int month);

    Map<String, Integer> calculateWorkdays(int year, int month);

    Map<String, Integer> calculateWorkdaysForEachEmployee(String employeeCode, int year, int month);

//    Long calculateWorkDays(Long employeeId);
//
//    Map<Long, BigDecimal> calculateOvertimeSalaryForEachEmployee();
//
//    BigDecimal getMonthlySalary(Employee employee);
//
//    Long getTotalOvertimeHours(Employee employee);
//
//    BigDecimal calculateOvertimeSalary(BigDecimal monthlySalary, Long totalOvertimeHours);
}
