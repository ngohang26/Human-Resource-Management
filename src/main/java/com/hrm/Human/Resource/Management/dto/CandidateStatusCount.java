package com.hrm.Human.Resource.Management.dto;

import com.hrm.Human.Resource.Management.entity.Candidate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidateStatusCount {
    private Candidate.CandidateStatus status;
    private Long count;
    private Double percentage;
}

