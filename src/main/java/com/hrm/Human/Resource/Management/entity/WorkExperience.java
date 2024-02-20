package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "work_experience")
public class WorkExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

//    @ManyToOne()
//    @JoinColumn(name = "position_id", referencedColumnName = "id")
//    private JobPosition jobPosition;

//    @ManyToOne()
//    @JoinColumn(name = "job_seeker_id", referencedColumnName = "id")
//    @JsonIgnore
//    private JobSeeker jobSeeker;

    @Column
    private String workplaceName;

    @Column
    private Date startingDate;

    @Column
    private Date quitDate;

}
