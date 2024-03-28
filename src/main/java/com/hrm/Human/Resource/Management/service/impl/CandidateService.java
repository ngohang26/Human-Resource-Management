package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.*;
import com.hrm.Human.Resource.Management.repositories.CandidateRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.repositories.JobOfferRepositories;
import com.hrm.Human.Resource.Management.repositories.JobPositionRepositories;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class CandidateService {

    @Autowired
    private final CandidateRepositories candidateRepositories;

    @Autowired
    private JobPositionRepositories jobPositionRepositories;

    @Autowired
    private EmployeeRepositories employeeRepositories;

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

    private Candidate getCandidate(Long id) {
        return candidateRepositories.findById(id).orElse(null);
    }

//    public Candidate offerMade(Long id, String offerDetails) {
//        Candidate candidate = getCandidate(id);
//        if (candidate != null && candidate.getCurrentStatus() == Candidate.CandidateStatus.FIRST_INTERVIEW) {
//            candidate.setCurrentStatus(Candidate.CandidateStatus.OFFER_MADE);
//            emailService.sendEmail(candidate.getEmail(), "","Chúc mừng, bạn đã qua vòng phỏng vấn thứ hai. Chúng tôi muốn gửi đến bạn đề xuất sau: " + offerDetails);
//            return candidateRepositories.save(candidate);
//        }
//        return null;
//    }

    public Candidate updateCandidateStatus(Long id, Candidate.CandidateStatus newStatus) {
        Candidate candidate = getCandidate(id);
        if (candidate != null) {
            switch (newStatus) {
                case INITIAL_REVIEW:
                    if (candidate.getCurrentStatus() == Candidate.CandidateStatus.NEW) {
                        candidate.setCurrentStatus(Candidate.CandidateStatus.INITIAL_REVIEW);
//                        emailService.sendEmail(candidate.getEmail(), "", "Hồ sơ của bạn đã được xem xét và chúng tôi sẽ liên hệ với bạn sớm.");
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
                        convertCandidateToEmployee(candidate.getId());
                        emailService.sendEmail(candidate.getEmail(), "Hồ sơ ứng tuyển của bạn: "
                                + candidate.getJobPosition().getJobPositionName(), "Chúc mừng! Bạn đã ký hợp đồng và trở thành một phần của đội ngũ của chúng tôi.");
                    }
                    break;
                case REFUSE:
                    candidate.setCurrentStatus(Candidate.CandidateStatus.REFUSE);
                    emailService.sendEmail(candidate.getEmail(), "Hồ sơ ứng tuyển của bạn: "
                            + candidate.getJobPosition().getJobPositionName(), "Rất tiếc, hồ sơ của bạn không phù hợp với yêu cầu của chúng tôi.");
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
            String interviewSchedule = "<p>Xin chào, \n </p>"+  "\n"
                    + "<p>Cảm ơn bạn đã nộp hồ sơ ứng tuyển vào vị trí <b style=\"color:#9a6c8e\">“"
                    + candidate.getJobPosition().getJobPositionName()
                    + "”</b> tại <b>Tolo</b></p>" + "\n"
                    + "<p>Chúng tôi rất ấn tượng với kinh nghiệm và kỹ năng của bạn, và chúng tôi muốn mời bạn tham gia một cuộc phỏng vấn.</p>"
                    + "<p>Chúng tôi muốn đặt lịch phỏng vấn vào</p>" + dateTime.toString()
                    + "<p>Phỏng vấn sẽ diễn ra tại tại <b>Tòa nhà ACB tại 12 Nguyen Trai, Hanoi</b></p>"
                    + "<p>Vui lòng xác nhận xem bạn có thể tham gia vào thời gian này không. Nếu không, hãy cho chúng tôi biết thời gian khác phù hợp với bạn trong tuần này.\n" +
                    "\n" +
                    "Chúng tôi mong muốn có cơ hội gặp bạn và tìm hiểu thêm về kinh nghiệm và mục tiêu nghề nghiệp của bạn.</p>"
                    + "<hr style=\"border: none; border-top: 1px solid #ddd;\">"
                    + "<b style=\"color:#9a6c8e\">Người liên hệ của bạn:</b>" + "\n"
                    + "<b>TOLO</b>"
                    + "<p>Email: ngohangvn01@gmail.com </p>"
                    + "<hr style=\"border: none; border-top: 1px solid #ddd;\">"
                    + "<p><b>Trân trọng</b></p>"
                    + "<b>Tolo</b><p>Việt Nam</p>";

            emailService.sendEmail(candidate.getEmail(), "Hồ sơ ứng tuyển của bạn: "
                    + candidate.getJobPosition().getJobPositionName(), interviewSchedule);
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
            String offerDetails =
                    "<p>Xin chào, \n </p>"+  "\n"
                    + "<p>Chúng tôi rất vui mừng thông báo rằng <b>Tolo</b> muốn mời bạn gia nhập đội ngũ của chúng tôi. Chúng tôi đã ấn tượng với kinh nghiệm và kỹ năng của bạn và chúng tôi tin rằng bạn sẽ là một sự bổ sung tuyệt vời cho đội ngũ của chúng tôi."
                    + "\n"
                    + "<p>Chúng tôi muốn đề xuất cho với </p>"
                    + "<p>Lương cơ bản: "+ jobOffer.getMonthlySalary() + " đồng</p> "
                    + "<p>Ngày bắt đầu:   <b style=\"color:#9a6c8e\">“"
                    + jobOffer.getStartDate().toString()
                    + "”</b> tới ngày <b style=\"color:#9a6c8e\">“"
                    + jobOffer.getEndDate().toString() + "”</b>. Bên cạnh đó, bạn cũng sẽ nhận được các quyền lợi khác như bảo hiểm, phúc lợi, ... </p>"
                    + "<p>Nếu bạn đồng ý với đề xuất này, chúng tôi muốn mời bạn đến văn phòng của chúng tôi để thảo luận thêm và ký hợp đồng.</p>"
                    + "<p>Nếu bạn có bất kỳ câu hỏi hoặc cần thêm thông tin, đừng ngần ngại liên hệ với chúng tôi.</p>"
                    + "<p>Chúng tôi mong muốn được làm việc cùng bạn.</p>"
                    + "<hr style=\"border: none; border-top: 1px solid #ddd;\">"
                    + "<b style=\"color:#9a6c8e\">Người liên hệ của bạn:</b>" + "\n"
                    + "<b>TOLO</b>"
                    + "<p>Email: ngohangvn01@gmail.com </p>"
                    + "<hr style=\"border: none; border-top: 1px solid #ddd;\">"
                    + "<p><b>Trân trọng</b></p>"
                    + "<b>Tolo</b><p>Việt Nam</p>";
            emailService.sendEmail(candidate.getEmail(), "Hồ sơ ứng tuyển của bạn: "
                    + candidate.getJobPosition().getJobPositionName(), offerDetails);
            return candidateRepositories.save(candidate);
        }
        return null;
    }

    public void convertCandidateToEmployee(Long candidateId) {
        Candidate candidate = getCandidate(candidateId);
        if (candidate != null && candidate.getCurrentStatus() == Candidate.CandidateStatus.CONTRACT_SIGNED) {
            Employee employee = new Employee();
            PersonalInfo personalInfo = new PersonalInfo();
            Contract contract;
            contract = new Contract();

            employee.setFullName(candidate.getCandidateName());
            employee.setPhoneNumber(candidate.getPhoneNumber());
            employee.setPositionName(candidate.getJobPosition().getJobPositionName());
            employee.setExperiences(candidate.getExperiences());
            employee.setSkills(candidate.getSkills());

            personalInfo.setPersonalEmail(candidate.getEmail());
            personalInfo.setSchool(candidate.getSchool());
            personalInfo.setCertificates(candidate.getCertificates());
            personalInfo.setCertificateLevel(candidate.getCertificateLevel());
            personalInfo.setFieldOfStudy(candidate.getFieldOfStudy());
            contract.setStartDate(candidate.getJobOffer().getStartDate());
            contract.setEndDate(candidate.getJobOffer().getEndDate());
            contract.setMonthlySalary(candidate.getJobOffer().getMonthlySalary());
            contract.setNoteContract(candidate.getJobOffer().getNoteContract());

            employee.setPersonalInfo(personalInfo);
            employeeRepositories.save(employee);
        }
    }

    public Candidate getCandidateById(Long id) {
        return candidateRepositories.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id " + id));
    }

    public List<Candidate> getCandidate() {
        return candidateRepositories.findAll();
    }

    public Candidate updateCandidateInfo(Long id, Candidate candidateDetails) {
        Candidate candidate = getCandidate(id);
        if (candidate != null) {
            candidate.setCandidateName(candidateDetails.getCandidateName());
            candidate.setEmail(candidateDetails.getEmail());
            return candidateRepositories.save(candidate);
        }
        return null;
    }


}

