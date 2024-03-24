package com.hrm.Human.Resource.Management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Setter
public class AttendanceDTO {
    private Long id;
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;
    private String employeeName;
    private String employeeCode;
    private Long workTime;
}
