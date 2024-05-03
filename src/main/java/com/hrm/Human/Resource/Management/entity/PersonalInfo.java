package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;


import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "personal_info")
public class PersonalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nationality;

    @Column
    private String birthPlace;

    @Column
    private Boolean isResident;

    @Column
    private String sex;

    @Column
    private LocalDate birthDate;

    @Column( unique = true)
    @Pattern(regexp="(^$|[0-9]{12})", message="Số CCCD cần phải có 12 chữ số")
    @NotNull(message = "Số căn cước công dân không thể trống vui lòng điền!")
    private String identityCardNumber;

    @Column
    @Email(message = "Email không đúng định dạng username@domain.com")
    private String personalEmail;

    @Column
    private String[] certificates;

    @Column
    private String certificateLevel;

    @Column
    private String fieldOfStudy;

    @Column
    private String school;

    public Boolean isResident() {
        return isResident;
    }

    @Pattern(regexp="(^$|[0-9]{12})", message="Số CCCD cần phải có 12 chữ số")
    @NotNull(message = "Số căn cước công dân không thể trống vui lòng điền!")
    public String getIdentityCardNumber() {
        return identityCardNumber;
    }
}
