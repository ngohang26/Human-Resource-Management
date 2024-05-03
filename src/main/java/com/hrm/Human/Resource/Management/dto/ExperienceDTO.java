package com.hrm.Human.Resource.Management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ExperienceDTO {
    private Long id;
    private String jobTitle;
    private String company;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Long> candidateIds;  // chỉ lưu trữ id của các Candidate liên quan

}

