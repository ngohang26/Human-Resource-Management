package com.hrm.Human.Resource.Management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

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

    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    private String fullName;

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    @Column
    private String image;

    @Column(nullable = false, unique = true)
    @Pattern(regexp="(^0|[0-9]{10})", message="Phone number must be a 10-digit number")
    private String phoneNumber;

    @Column
    @Email(message = "Email should be valid")
    private String workEmail;

    //emergency_contact
    @Column
    private String nameContactER;

    @Column
    @Pattern(regexp="(^0|[0-9]{10})", message="Phone number must be a 10-digit number")
    private String phoneContactER;

    @Column
    private String positionName;

    @Column 
    private String departmentName;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @Column
    private Boolean isDeleted;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "personal_info_id", referencedColumnName = "id")
    private PersonalInfo personalInfo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contract_id", referencedColumnName = "id")
    private Contract contract;


    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>();

    public Employee() {
        this.isDeleted = false;
    }

    @PostPersist
    public void generateCodeNameAndEmployeeCode() {
        LocalDate date = LocalDate.now();
        int year = date.getYear() % 100;
        int month = date.getMonthValue();
        this.employeeCode = String.format("%02d%02d%03d", year, month, id);
        this.codeName = this.employeeCode + " - " + this.fullName;
    }

    public void setIsDeleted(boolean b) {}
}

