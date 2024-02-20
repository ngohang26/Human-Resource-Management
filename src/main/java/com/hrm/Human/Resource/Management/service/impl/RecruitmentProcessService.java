package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.Candidate;
import com.hrm.Human.Resource.Management.entity.RecruitmentProcess;
import com.hrm.Human.Resource.Management.repositories.CandidateRepositories;
import com.hrm.Human.Resource.Management.repositories.RecruitmentProcessRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecruitmentProcessService {
    @Autowired
    private RecruitmentProcessRepositories recruitmentProcessRepositories;

    @Autowired
    private CandidateRepositories candidateRepositories;
    @Autowired
    private EmailService emailService;

    public RecruitmentProcess createRecruitmentProcess() {
        RecruitmentProcess process = new RecruitmentProcess();
        return recruitmentProcessRepositories.save(process);
    }

    public RecruitmentProcess updateRecruitmentProcessStatus(Long recruitmentProcessId, String status) {
        RecruitmentProcess process = recruitmentProcessRepositories.findById(recruitmentProcessId).get();
        process.setStatus(status);
        return recruitmentProcessRepositories.save(process);
    }

    public void notifyCandidate(Long candidateId, String status) {
        Candidate candidate = candidateRepositories.findById(candidateId).get();
        String emailContent = "Dear " + candidate.getCandidateName() + ",\n\nYour application status is: " + status;
        emailService.sendEmail(candidate.getEmail(), "Application Status", emailContent);
    }

    public RecruitmentProcess advanceProcess(Long recruitmentProcessId) {
        RecruitmentProcess process = recruitmentProcessRepositories.findById(recruitmentProcessId).get();
        switch (process.getStatus()) {
            case NEW:
                process.setStatus(String.valueOf(RecruitmentProcess.RecruitmentStatus.INITIAL_REVIEW));
                break;
            case INITIAL_REVIEW:
                process.setStatus(String.valueOf(RecruitmentProcess.RecruitmentStatus.FIRST_INTERVIEW));
                break;
            case FIRST_INTERVIEW:
                process.setStatus(String.valueOf(RecruitmentProcess.RecruitmentStatus.SECOND_INTERVIEW));
                break;
            case SECOND_INTERVIEW:
                process.setStatus(String.valueOf(RecruitmentProcess.RecruitmentStatus.OFFER_MADE));
                break;
            case OFFER_MADE:
                process.setStatus(String.valueOf(RecruitmentProcess.RecruitmentStatus.CONTRACT_SIGNED));
                break;
            default:
                throw new IllegalStateException("Cannot advance process from status: " + process.getStatus());
        }
        return recruitmentProcessRepositories.save(process);
    }

}
