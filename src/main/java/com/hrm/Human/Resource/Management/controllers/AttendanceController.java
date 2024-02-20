package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.dto.WorkTimeDTO;
import com.hrm.Human.Resource.Management.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/work-time")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/createAttendances")
    public List<WorkTimeDTO> createAttendances() {
        return attendanceService.createWorkTimes();
    }

}
