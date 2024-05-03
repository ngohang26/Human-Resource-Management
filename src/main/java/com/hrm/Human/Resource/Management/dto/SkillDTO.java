package com.hrm.Human.Resource.Management.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SkillDTO {
    private Long id;
    private String name;
    private String proficiency;
    private List<Long> candidateIds;  // chỉ lưu trữ id của các Candidate liên quan

}

