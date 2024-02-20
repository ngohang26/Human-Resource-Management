package com.hrm.Human.Resource.Management.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class WorkTimeDTO {
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;
    private String fullName;
    private String employeeCode;


    public WorkTimeDTO() {}

    public WorkTimeDTO(LocalDate date, LocalTime timeIn, LocalTime timeOut, String fullName, String employeeCode) {
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.fullName = fullName;
        this.employeeCode = employeeCode;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(LocalTime timeIn) {
        this.timeIn = timeIn;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(LocalTime timeOut) {
        this.timeOut = timeOut;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }
}

