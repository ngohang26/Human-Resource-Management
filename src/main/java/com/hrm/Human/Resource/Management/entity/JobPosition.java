package com.hrm.Human.Resource.Management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job_position")
public class JobPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String jobPositionName;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @Column
    private String skillsRequired;

    @Column
    private LocalDate applicationDeadline;

    @JsonIgnore
    @OneToMany(mappedBy = "jobPosition", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Candidate> candidates;
}

