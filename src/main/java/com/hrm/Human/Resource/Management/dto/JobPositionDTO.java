package com.hrm.Human.Resource.Management.dto;

import com.hrm.Human.Resource.Management.entity.Position;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JobPositionDTO {
    private Long id;
    private Position position;
    private String skillsRequired;
    private String jobPositionName;
    private LocalDate applicationDeadline;
    private int candidateCount;
}
