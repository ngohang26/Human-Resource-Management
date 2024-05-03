package com.hrm.Human.Resource.Management.dto;

import com.hrm.Human.Resource.Management.entity.Candidate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidateUpdateDTO {
    private Candidate.CandidateStatus newStatus;
    private Candidate candidateDetails;
    private String identityCardNumber;
}

