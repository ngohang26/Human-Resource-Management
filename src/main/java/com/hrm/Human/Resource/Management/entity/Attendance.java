package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendances")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime timeIn;

    @Column(nullable = false)
    private LocalTime timeOut;

    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "employeeCode")
    private Employee employee;

    public Attendance() {}

    public Attendance(LocalDate date, LocalTime timeIn, LocalTime timeOut, Employee employee) {
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.employee = employee;
    }

    public LocalDate getDate() {return date;}

    public void setDate(LocalDate date) {this.date = date;}

    public LocalTime getTimeIn() {return timeIn;}

    public void setTimeIn(LocalTime timeIn) {this.timeIn = timeIn;}

    public LocalTime getTimeOut() {return timeOut;}

    public void setTimeOut(LocalTime timeOut) {this.timeOut = timeOut;}

    public Employee getEmployee() {return employee;}

    public void setEmployee(Employee employee) {this.employee = employee;}

    //    @JsonIgnore
//    @OneToMany(mappedBy = "workTime")
//    private List<Job> jobs;

}
