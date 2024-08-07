package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Setter
@Entity
@NoArgsConstructor
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

    @Column(nullable = false)
    private Long workTime;

    @Column
    private Long overTime;

    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "employeeCode")
    private Employee employee;

    public Attendance(LocalDate date, LocalTime timeIn, LocalTime timeOut, Employee employee) {
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.employee = employee;
        this.workTime = Duration.between(timeIn, timeOut).toHours() - 1;
        if (this.workTime > 8) {
            this.overTime = this.workTime - 8;
        } else {
            this.overTime = 0L;
        }
    }

    public int calculateWorkdays() {
        return (workTime >= 8) ? 1 : 0;  // Nếu workTime lớn hơn hoặc bằng 8, số ngày công là 1
        // Ngược lại, số ngày công là 0
    }
}
