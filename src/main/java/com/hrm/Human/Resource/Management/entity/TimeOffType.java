package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "time_off_types")
public class TimeOffType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String timeOffTypeName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalType approvalType;

    public enum ApprovalType {
        NO_VALIDATION,
        APPROVED_BY_TIME_OFF_MANAGER,
        APPROVED_BY_EMPLOYEE_SUPERVISOR,
        APPROVED_BY_BOTH
    }

    public void setTimeOffTypeName(String timeOffTypeName) {
        this.timeOffTypeName = timeOffTypeName;
    }

    public String getTimeOffTypeName() {
        return timeOffTypeName;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(ApprovalType approvalType) {
        this.approvalType = approvalType;
    }
}
