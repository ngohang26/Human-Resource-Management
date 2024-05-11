package com.hrm.Human.Resource.Management.dto;

import com.hrm.Human.Resource.Management.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {
    private Long id;
    private String candidateName;
    private String email;
    private LocalDate birthDate;
    @Pattern(regexp="(^0|[0-9]{10})", message="Phone number must be a 10-digit number")
    private String phoneNumber;
    private LocalDate dateApplied;
    private String resumeFilePath;
    private LocalDateTime interviewTime;
    private LocalDateTime secondInterviewTime;
    private Candidate.InterviewStatus firstInterviewStatus;
    private Candidate.InterviewStatus secondInterviewStatus;
    private String[] certificates;
    private String certificateLevel;
    private String fieldOfStudy;
    private String school;
    private JobPosition jobPosition;
    private Candidate.CandidateStatus currentStatus;
    private JobOffer jobOffer;
    private List<Skills> skills;
    private List<Experiences> experiences;
    private Candidate.CandidateStatus newStatus;
    private String identityCardNumber;

}

