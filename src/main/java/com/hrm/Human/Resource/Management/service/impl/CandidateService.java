package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.Candidate;
import com.hrm.Human.Resource.Management.entity.JobOffer;
import com.hrm.Human.Resource.Management.entity.JobPosition;
import com.hrm.Human.Resource.Management.repositories.CandidateRepositories;
import com.hrm.Human.Resource.Management.repositories.JobOfferRepositories;
import com.hrm.Human.Resource.Management.repositories.JobPositionRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class CandidateService {

    @Autowired
    private final CandidateRepositories candidateRepositories;

    @Autowired
    private JobPositionRepositories jobPositionRepositories;

    @Autowired
    private JobOfferRepositories jobOfferRepositories;
    @Autowired
    private final EmailService emailService;
    public CandidateService(CandidateRepositories candidateRepositories, EmailService emailService) {
        this.candidateRepositories = candidateRepositories;
        this.emailService = emailService;
    }


    public Candidate createCandidate(Candidate candidate) {
        Object jobPosition = jobPositionRepositories.findByJobPositionName(candidate.getJobPosition().getJobPositionName())
                .orElseThrow(() -> new IllegalArgumentException("JobPosition not found with name " + candidate.getJobPosition().getJobPositionName()));

        candidate.setJobPosition((JobPosition) jobPosition);

        candidate.setCurrentStatus(Candidate.CandidateStatus.NEW);

        Candidate savedCandidate = candidateRepositories.save(candidate);

        String htmlContent = "<p>Xin chào, \n </p>"+  "\n"
                + "<p>Chúng tôi xác nhận rằng chúng tôi đã nhận được đơn ứng tuyển của bạn cho vị trí <b style=\"color:#9a6c8e\">“"
                + candidate.getJobPosition().getJobPositionName()
                + "”</b> tại <b>Tolo</b></p>" + "\n"
                + "<p>Chúng tôi sẽ xem xét hồ sơ và liên hệ với bạn.</p>"
                 + "<hr style=\"border: none; border-top: 1px solid #ddd;\">"
                + "<b style=\"color:#9a6c8e\">Người liên hệ của bạn:</b>" + "\n"
                + "<b>TOLO</b>"
                + "<p>Email: ngohangvn01@gmail.com </p>"
                + "<hr style=\"border: none; border-top: 1px solid #ddd;\">"
                + "<b style=\"color:#9a6c8e\">Bước tiếp theo là gì?</b>"
                + "<p>Thông thường, chúng tôi trả lời hồ sơ ứng tuyển trong vòng vài ngày. </p>" + "\n"
                + "<p>Đừng ngần ngại liên hệ với chúng tôi nếu bạn muốn nhận phản hồi sớm hơn hoặc nếu bạn nhận được phản hồi muộn (chỉ cần trả lời email này).</p>"
                + "<hr style=\"border: none; border-top: 1px solid #ddd;\">"
                + "<br><br><b>Tolo</b><p>Việt Nam</p>";

        emailService.sendEmail(candidate.getEmail(), "Hồ sơ ứng tuyển của bạn: “"
                + candidate.getJobPosition().getJobPositionName() + "”", htmlContent);




        return savedCandidate;
    }

    public Candidate initialReview(Long id, boolean isSuitable) {
        Candidate candidate = getCandidate(id);
        if (candidate != null && candidate.getCurrentStatus() == Candidate.CandidateStatus.NEW) {
            if (isSuitable) {
                candidate.setCurrentStatus(Candidate.CandidateStatus.INITIAL_REVIEW);
                emailService.sendEmail(candidate.getEmail(), "","Hồ sơ của bạn đã được xem xét và chúng tôi sẽ liên hệ với bạn sớm.");
            } else {
                candidate.setCurrentStatus(Candidate.CandidateStatus.REFUSE);
                emailService.sendEmail(candidate.getEmail(), "","Rất tiếc, hồ sơ của bạn không phù hợp với yêu cầu của chúng tôi.");
            }
            return candidateRepositories.save(candidate);
        }
        return null;
    }

    public Candidate firstInterview(Long id, LocalDateTime interviewTime, String location, boolean isPassed) {
        Candidate candidate = getCandidate(id);
        if (candidate != null && candidate.getCurrentStatus() == Candidate.CandidateStatus.INITIAL_REVIEW) {
            if (isPassed) {
                candidate.setCurrentStatus(Candidate.CandidateStatus.FIRST_INTERVIEW);
                emailService.sendEmail(candidate.getEmail(),"", "Bạn đã qua vòng xem xét hồ sơ. Chúng tôi đã lên lịch phỏng vấn cho bạn vào " + interviewTime + " tại " + location);
            } else {
                candidate.setCurrentStatus(Candidate.CandidateStatus.REFUSE);
                emailService.sendEmail(candidate.getEmail(),"", "Rất tiếc, bạn không qua được vòng xem xét hồ sơ.");
            }
            return candidateRepositories.save(candidate);
        }
        return null;
    }

    private Candidate getCandidate(Long id) {
        return candidateRepositories.findById(id).orElse(null);
    }

    public Candidate offerMade(Long id, String offerDetails) {
        Candidate candidate = getCandidate(id);
        if (candidate != null && candidate.getCurrentStatus() == Candidate.CandidateStatus.FIRST_INTERVIEW) {
            candidate.setCurrentStatus(Candidate.CandidateStatus.OFFER_MADE);
            emailService.sendEmail(candidate.getEmail(), "","Chúc mừng, bạn đã qua vòng phỏng vấn thứ hai. Chúng tôi muốn gửi đến bạn đề xuất sau: " + offerDetails);
            return candidateRepositories.save(candidate);
        }
        return null;
    }

    public Candidate contractSigned(Long id) {
        Candidate candidate = getCandidate(id);
        if (candidate != null && candidate.getCurrentStatus() == Candidate.CandidateStatus.OFFER_MADE) {
            candidate.setCurrentStatus(Candidate.CandidateStatus.CONTRACT_SIGNED);
            emailService.sendEmail(candidate.getEmail(),"", "Chúc mừng, bạn đã ký hợp đồng với chúng tôi. Chúng tôi rất mong chờ sự hợp tác với bạn!");
            return candidateRepositories.save(candidate);
        }
        return null;
    }

    public Candidate updateCandidateStatus(Long id, Candidate.CandidateStatus newStatus) {
        Candidate candidate = getCandidate(id);
        if (candidate != null) {
            switch (newStatus) {
                case INITIAL_REVIEW:
                    if (candidate.getCurrentStatus() == Candidate.CandidateStatus.NEW) {
                        candidate.setCurrentStatus(Candidate.CandidateStatus.INITIAL_REVIEW);
                        emailService.sendEmail(candidate.getEmail(), "", "Hồ sơ của bạn đã được xem xét và chúng tôi sẽ liên hệ với bạn sớm.");
                    }
                    break;
                case FIRST_INTERVIEW:
                    if (candidate.getCurrentStatus() == Candidate.CandidateStatus.INITIAL_REVIEW) {
                        candidate.setCurrentStatus(Candidate.CandidateStatus.FIRST_INTERVIEW);
                        String interviewSchedule = candidate.getInterviewTime() != null ? "Lịch phỏng vấn đầu tiên của bạn là: " + candidate.getInterviewTime().toString() : "";
                        emailService.sendEmail(candidate.getEmail(), "", "Bạn đã qua vòng xem xét hồ sơ. Chúng tôi sẽ liên hệ với bạn để sắp xếp lịch phỏng vấn đầu tiên. " + interviewSchedule);
                    }
                    break;
//                case SECOND_INTERVIEW:
//                    if (candidate.getCurrentStatus() == Candidate.CandidateStatus.FIRST_INTERVIEW) {
//                        candidate.setCurrentStatus(Candidate.CandidateStatus.SECOND_INTERVIEW);
//                        String interviewSchedule = candidate.getInterviewTime() != null ? "Lịch phỏng vấn thứ hai của bạn là: " + candidate.getInterviewTime().toString() : "";
//                        emailService.sendEmail(candidate.getEmail(), "", "Bạn đã qua vòng phỏng vấn đầu tiên. Chúng tôi sẽ liên hệ với bạn để sắp xếp lịch phỏng vấn thứ hai. " + interviewSchedule);
//                    }
//                    break;
                case OFFER_MADE:
                    if (candidate.getCurrentStatus() == Candidate.CandidateStatus.FIRST_INTERVIEW) {
                        candidate.setCurrentStatus(Candidate.CandidateStatus.OFFER_MADE);
                        emailService.sendEmail(candidate.getEmail(), "", "Bạn đã qua vòng phỏng vấn thứ hai. Chúng tôi sẽ gửi cho bạn một đề nghị công việc.");
                    }
                    break;
                case CONTRACT_SIGNED:
                    if (candidate.getCurrentStatus() == Candidate.CandidateStatus.OFFER_MADE) {
                        candidate.setCurrentStatus(Candidate.CandidateStatus.CONTRACT_SIGNED);
                        emailService.sendEmail(candidate.getEmail(), "", "Chúc mừng! Bạn đã ký hợp đồng và trở thành một phần của đội ngũ của chúng tôi.");
                    }
                    break;
                case REFUSE:
                    candidate.setCurrentStatus(Candidate.CandidateStatus.REFUSE);
                    emailService.sendEmail(candidate.getEmail(), "", "Rất tiếc, hồ sơ của bạn không phù hợp với yêu cầu của chúng tôi.");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status");
            }
            return candidateRepositories.save(candidate);
        }
        return null;
    }

    public Candidate setInterviewTime(Long id, Map<String, String> payload) {
        Candidate candidate = getCandidate(id);
        if (candidate != null && candidate.getCurrentStatus() == Candidate.CandidateStatus.INITIAL_REVIEW) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(payload.get("interviewTime"), formatter);
            candidate.setInterviewTime(dateTime);
            candidate.setCurrentStatus(Candidate.CandidateStatus.FIRST_INTERVIEW);
            String interviewSchedule = "Lịch phỏng vấn đầu tiên của bạn là: " + dateTime.toString();
            emailService.sendEmail(candidate.getEmail(), "", "Bạn đã qua vòng xem xét hồ sơ. Chúng tôi sẽ liên hệ với bạn để sắp xếp lịch phỏng vấn đầu tiên. " + interviewSchedule);
            return candidateRepositories.save(candidate);
        }
        return null;
    }

    public Candidate makeOffer(Long id, JobOffer jobOffer) {
        Candidate candidate = getCandidate(id);
        if (candidate != null && candidate.getCurrentStatus() == Candidate.CandidateStatus.FIRST_INTERVIEW) {
            jobOffer = jobOfferRepositories.save(jobOffer); // Lưu JobOffer vào cơ sở dữ liệu
            candidate.setJobOffer(jobOffer); // Gán JobOffer cho ứng viên
            candidate.setCurrentStatus(Candidate.CandidateStatus.OFFER_MADE);
            String offerDetails = "Lương cơ bản: " + jobOffer.getMonthlySalary() + "\n" +
                    "Ngày bắt đầu: " + jobOffer.getStartDate() + "\n" +
                    "tới ngày: " + jobOffer.getEndDate().toString();
            emailService.sendEmail(candidate.getEmail(), "", "Bạn đã qua vòng phỏng vấn thứ hai. Chúng tôi sẽ gửi cho bạn một đề nghị công việc với các chi tiết sau:\n" + offerDetails);
            return candidateRepositories.save(candidate);
        }
        return null;
    }


}

