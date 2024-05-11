package com.hrm.Human.Resource.Management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JobPositionDTO {
    private Long id;
    private String jobPositionName;
    private String jobDescription;
    private LocalDate applicationDeadline;
    private int candidateCount;
}
