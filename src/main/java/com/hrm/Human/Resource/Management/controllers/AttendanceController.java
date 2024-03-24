package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.dto.AttendanceDTO;
import com.hrm.Human.Resource.Management.entity.Attendance;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendances")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    @GetMapping(path = "/getAllAttendances")
    public List<AttendanceDTO> getAllAttendances() {
        return attendanceService.getAllAttendances();
    }

    @GetMapping(path = "/attendance/{id}")
    public List<AttendanceDTO> getAttendancesByEmployee(@PathVariable Long id) {
        return attendanceService.getAttendancesByEmployee(id);
    }

    @GetMapping("/attendance/date/{date}")
    public List<AttendanceDTO> getAttendancesByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceService.getAttendancesByDate(date);
    }

    @PostMapping("/createAttendances")
    public List<Attendance> createAttendances(LocalDate date) {
        return attendanceService.createWorkTimes(date);
    }

}
