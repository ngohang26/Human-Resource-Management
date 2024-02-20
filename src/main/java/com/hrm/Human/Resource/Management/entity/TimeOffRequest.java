package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "time_off_request")
public class TimeOffRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String employeeCode;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Long timeOffTypeId;

    @Column(nullable = false)
    private String relatedDocument;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

//    public Object getEmployeeId() {
//        return null;
//    }

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    public TimeOffRequest(Status status) {
        this.status = Status.PENDING;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getTimeOffTypeId() {
        return timeOffTypeId;
    }

    public void setTimeOffTypeId(Long timeOffTypeId) {
        this.timeOffTypeId = timeOffTypeId;
    }

    public String getRelatedDocument() {
        return relatedDocument;
    }

    public void setRelatedDocument(String relatedDocument) {
        this.relatedDocument = relatedDocument;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
