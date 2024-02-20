package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;

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

    @Column(nullable = false)
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sex sex;

    public Boolean isResident() {
        return isResident;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }


    public enum Sex {
        FEMALE,
        MALE,

    }

    @Column
    private String urlImage;

    @Column(nullable = false, unique = true)
    @Pattern(regexp="(^0|[0-9]{10})", message="Phone number must be a 10-digit number")
    private String phoneNumber;

    @Column
    @Email(message = "Email should be valid")
    private String workEmail;

    //emergency_contact
    @Column
    private String name_contactER;

    @Column
    @Pattern(regexp="(^0|[0-9]{10})", message="Phone number must be a 10-digit number")
    private String phone_contactER;

    @Transient
    private String positionName;

    @Transient
    private String departmentName;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;
//
//    @ManyToOne
//    private Employee manager;

    @Column
    private Boolean isDeleted;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "personal_info_id", referencedColumnName = "id")
    private PersonalInfo personalInfo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contract_id", referencedColumnName = "id")
    private Contract contract;

    @Column
    private Boolean isResident;

    public Employee() {
        this.isDeleted = false;this.isResident = false;
    }

    @PostPersist
    public void generateEmployeeCode() {
        LocalDate date = LocalDate.now();
        int year = date.getYear() % 100;
        int month = date.getMonthValue();
        this.employeeCode = String.format("%02d%02d%03d", year, month, id);
    }

    public void setIsDeleted(boolean b) {}


//    public String getPositionName() {
//        return this.position.getPositionName();
//    }
//
//    public String getDepartmentName() {
//        return this.department.getDepartmentName();
//    }

}

