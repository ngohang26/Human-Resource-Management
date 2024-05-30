package com.hrm.Human.Resource.Management.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String employeeCode;

    @Column
    private String codeName;

    @Column(nullable = false)
    @Size(min = 2, max = 50, message = "Họ tên cần phải lớn hơn 2 ký tự và ít hơn 50 ký tự")
    @NotNull(message = "Họ tên không thể trống")
    private String fullName;


    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    @Column
    private String image;

//    @Column(nullable = false, unique = true)
    @Pattern(regexp="(^0|[0-9]{10})", message="Số điện thoại cần có 10 chữ số và bắt đầu bằng số 0")
    @NotNull(message = "Số điện thoại không thể trống") // xem xet nen de trong
    private String phoneNumber;

    @Column
    @Email(message = "Email không đúng định dạng username@domain.com")
    private String workEmail;

    @Column
    @Pattern(regexp="(^0|[0-9]{10})", message="Số điện thoại cần có 10 chữ số và bắt đầu bằng số 0")
    private String phoneContactER;

    //emergency_contact
    @Column
    private String nameContactER;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @Valid
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "personal_info_id", referencedColumnName = "id")
    private PersonalInfo personalInfo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contract_id", referencedColumnName = "id")
    private Contract contract;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "employee_skill",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skills> skills = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "employee_experience",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "experience_id"))
    private List<Experiences> experiences = new ArrayList<>();

    public Employee() {
        this.employmentStatus = EmploymentStatus.ACTIVE;
    }

    @PostPersist
    public void generateCodeNameAndEmployeeCode() {
        LocalDate date = LocalDate.now();
        int year = date.getYear() % 100;
        int month = date.getMonthValue();
        this.employeeCode = String.format("%02d%02d%03d", year, month, id);
        this.codeName = this.employeeCode + " - " + this.fullName;
    }

    @ManyToOne
    @JoinColumn(name = "termination_reason_id")
    private TerminationReason terminationReason;

    public enum EmploymentStatus {
        ACTIVE, TERMINATED
    }

    private LocalDate terminationDate;

    @Column
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    public void terminateEmployment(TerminationReason reason, LocalDate terminationDate) {
        this.employmentStatus = EmploymentStatus.TERMINATED;
        this.terminationReason = reason;
        this.terminationDate = terminationDate;
        if (this.contract != null) {
            this.contract.setContractStatus(Contract.ContractStatus.CANCELLED);
        }
    }

}

