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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('VIEW_ATTENDANCE')")
    @GetMapping(path = "/getAllAttendances")
    public List<AttendanceDTO> getAllAttendances() {
        return attendanceService.getAllAttendances();
    }

    @PreAuthorize("hasAuthority('VIEW_ATTENDANCE')")
    @GetMapping(path = "/attendance/{employeeCode}")
    public List<AttendanceDTO> getAttendancesByEmployee(@PathVariable String employeeCode) {
        return attendanceService.getAttendancesByEmployee(employeeCode);
    }

    @PreAuthorize("hasAuthority('VIEW_ATTENDANCE')")
    @GetMapping("/employee/{employeeCode}/{month}/{year}")
    public ResponseEntity<List<AttendanceDTO>> getAttendancesByMonthAndYear(@PathVariable String employeeCode, @PathVariable int month, @PathVariable int year) {
        List<AttendanceDTO> attendances = attendanceService.getAttendancesByMonthAndYear(employeeCode, month, year);
        return new ResponseEntity<>(attendances, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('VIEW_ATTENDANCE')")
    @GetMapping("/{year}/{month}")
    public ResponseEntity<List<AttendanceDTO>> getAttendancesByMonthAndYear(@PathVariable int year, @PathVariable int month) {
        List<AttendanceDTO> attendances = attendanceService.getAttendancesByYearAndMonth(year, month);
        return new ResponseEntity<>(attendances, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('VIEW_ATTENDANCE')")
    @GetMapping("/attendance/date/{date}")
    public List<AttendanceDTO> getAttendancesByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceService.getAttendancesByDate(date);
    }

    @PostMapping("/createAttendances")
    public List<Attendance> createAttendances(LocalDate date) {
        return attendanceService.createWorkTimes(date);
    }

    @PreAuthorize("hasAuthority('VIEW_ATTENDANCE')")
    @GetMapping("/workdays/{year}/{month}")
    public ResponseEntity<Map<String, Integer>> getWorkdays(@PathVariable int year, @PathVariable int month) {
        Map<String, Integer> workdays = attendanceService.calculateWorkdays(year, month);
        return new ResponseEntity<>(workdays, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('EDIT_ATTENDANCE')")
    @PutMapping(path = "/update/{id}")
    public ResponseEntity<AttendanceDTO> updateAttendance(@PathVariable Long id, @RequestBody AttendanceDTO attendanceDTO) {
        AttendanceDTO updatedAttendance = attendanceService.updateAttendance(id, attendanceDTO);
        return new ResponseEntity<>(updatedAttendance, HttpStatus.OK);
    }
}