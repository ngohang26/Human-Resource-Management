package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.dto.AttendanceDTO;
import com.hrm.Human.Resource.Management.entity.Attendance;
import com.hrm.Human.Resource.Management.entity.Employee;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
//    List<Attendance> createWorkTimes();

    List<Attendance> createWorkTimes(LocalDate date);

    List<AttendanceDTO> getAllAttendances();
    List<AttendanceDTO> getAttendancesByEmployee(Long employeeId);

    List<AttendanceDTO> getAttendancesByDate(LocalDate date);
}
