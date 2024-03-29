package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.dto.AttendanceDTO;
import com.hrm.Human.Resource.Management.entity.Attendance;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.service.AttendanceService;
import com.hrm.Human.Resource.Management.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendances")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EmployeeService employeeService;

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

    @GetMapping("/workdays/{year}/{month}")
    public ResponseEntity<Map<Long, Integer>> getWorkdays(@PathVariable int year, @PathVariable int month) {
        Map<Long, Integer> workdays = attendanceService.calculateWorkdays(year, month);
        return new ResponseEntity<>(workdays, HttpStatus.OK);
    }


}
