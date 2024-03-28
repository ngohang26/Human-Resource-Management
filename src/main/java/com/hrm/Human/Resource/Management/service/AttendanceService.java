package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.dto.AttendanceDTO;
import com.hrm.Human.Resource.Management.entity.Attendance;
import com.hrm.Human.Resource.Management.entity.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
//    List<Attendance> createWorkTimes();

    List<Attendance> createWorkTimes(LocalDate date);

    List<AttendanceDTO> getAllAttendances();
    List<AttendanceDTO> getAttendancesByEmployee(Long employeeId);

    List<AttendanceDTO> getAttendancesByDate(LocalDate date);

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
