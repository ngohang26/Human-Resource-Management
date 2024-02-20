package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
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
    private String residence;

    @Column
    private LocalDate birthDate;

    @Column(nullable = false, unique = true)
    @Pattern(regexp="(^$|[0-9]{11})", message="Identity card number must be a 9-digit number")
    private String identityCardNumber;

    @Column
    @Email(message = "Email should be valid")
    private String personalEmail;

    @Column
    private String[] certificates;

    // education
    @Column
    private String certificateLevel;

    @Column
    private String fieldOfStudy;

    @Column
    private String school;


    public String getIdentityCardNumber() {
        return identityCardNumber;
    }
}
