package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.dto.*;
import com.hrm.Human.Resource.Management.entity.*;
import com.hrm.Human.Resource.Management.repositories.*;
import com.hrm.Human.Resource.Management.response.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private ContractRepositories contractRepositories;

    @Autowired
    private SkillNameRepositories skillNameRepositories;

    @Autowired
    private ExperienceNameRepositories experienceNameRepositories;

    @Autowired
    private final EmailService emailService;

    @Autowired
    private ModelMapper modelMapper;

    public CandidateService(CandidateRepositories candidateRepositories, EmailService emailService) {
        this.candidateRepositories = candidateRepositories;
        this.emailService = emailService;
    }

//    public Map<String, Object> createCandidate(Candidate candidate) {
//        JobPosition jobPosition = jobPositionRepositories.findById(candidate.getJobPosition().getId())
//                .orElseThrow(() -> new IllegalArgumentException("JobPosition not found with id " + candidate.getJobPosition().getId()));
//        candidate.setJobPosition(jobPosition);
//
//        candidate.setCurrentStatus(Candidate.CandidateStatus.NEW);
//
//        Candidate savedCandidate = candidateRepositories.save(candidate);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("candidate", savedCandidate);
//
//        // chuyển đổi danh sách skills và experiences
//        List<Map<String, Object>> skillList = savedCandidate.getSkills().stream()
//                .map(skill -> {
//                    Map<String, Object> skillMap = new HashMap<>();
//                    skillMap.put("name", skill.getName());
//                    skillMap.put("proficiency", skill.getProficiency());
//                    return skillMap;
//                })
//                .collect(Collectors.toList());
//
//        List<Map<String, Object>> experienceList = savedCandidate.getExperiences().stream()
//                .map(experience -> {
//                    Map<String, Object> experienceMap = new HashMap<>();
//                    // thêm các trường bạn muốn trả về từ Experience vào experienceMap
//                    return experienceMap;
//                })
//                .collect(Collectors.toList());
//
//        String htmlContent = "<p>Xin chào, \n </p>" + "\n"
//                + "<p>Chúng tôi xác nhận rằng chúng tôi đã nhận được đơn ứng tuyển của bạn cho vị trí <b style=\"color:#9a6c8e\">“"
//                + candidate.getJobPosition().getJobPositionName()
//                + "”</b> tại <b>Tolo</b></p>" + "\n"
//                + "<p>Chúng tôi sẽ xem xét hồ sơ và liên hệ với bạn.</p>"
//                + "<hr style=\"border: none; border-top: 1px solid #ddd;\">"
//                + "<b style=\"color:#9a6c8e\">Người liên hệ của bạn:</b>" + "\n"
//                + "<b>TOLO</b>"
//                + "<p>Email: ngohangvn01@gmail.com </p>"
//                + "<hr style=\"border: none; border-top: 1px solid #ddd;\">"
//                + "<b style=\"color:#9a6c8e\">Bước tiếp theo là gì?</b>"
//                + "<p>Thông thường, chúng tôi trả lời hồ sơ ứng tuyển trong vòng vài ngày. </p>" + "\n"
//                + "<p>Đừng ngần ngại liên hệ với chúng tôi nếu bạn muốn nhận phản hồi sớm hơn hoặc nếu bạn nhận được phản hồi muộn (chỉ cần trả lời email này).</p>"
//                + "<hr style=\"border: none; border-top: 1px solid #ddd;\">"
//                + "<br><br><b>Tolo</b><p>Việt Nam</p>";
//
//        emailService.sendEmail(candidate.getEmail(), "Hồ sơ ứng tuyển của bạn: “"
//                + candidate.getJobPosition().getJobPositionName() + "”", htmlContent);
//        return response;
//    }

    private Candidate getCandidate(Long id) {
        return candidateRepositories.findById(id).orElse(null);
    }

    public CandidateDTO getCandidateById(Long id) {
        Candidate candidate = candidateRepositories.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id " + id));
        return convertToDTO(candidate);
    }

    public List<CandidateDTO> getCandidates() {
        List<Candidate> candidates = candidateRepositories.findAll();
        return candidates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public ResponseEntity<String> updateCandidateStatus(Long id, Candidate.CandidateStatus newStatus, Candidate candidateDetails, String identityCardNumber) {        Candidate candidate = getCandidate(id);
        if (candidate != null) {
            switch (newStatus) {
                case INITIAL_REVIEW:
                    if (candidate.getCurrentStatus() == Candidate.CandidateStatus.NEW) {
                        candidate.setCurrentStatus(Candidate.CandidateStatus.INITIAL_REVIEW);
                    }
                    break;
                case FIRST_INTERVIEW:
                    Map<String, String> payload = new HashMap<>();
                    payload.put("interviewTime", candidateDetails.getInterviewTime().format(FORMATTER));
                    return setInterviewTime(id, payload);
                case SECOND_INTERVIEW:
                    Map<String, String> payload2 = new HashMap<>();
                    payload2.put("secondInterviewTime", candidateDetails.getSecondInterviewTime().format(FORMATTER));
                    return setSecondInterviewTime(id, payload2);
                case OFFER_MADE:
                    JobOffer jobOffer = candidateDetails.getJobOffer();
                    return makeOffer(id, jobOffer);
                case CONTRACT_SIGNED:
                    if (candidate.getCurrentStatus() == Candidate.CandidateStatus.OFFER_MADE) {
                        candidate.setCurrentStatus(Candidate.CandidateStatus.CONTRACT_SIGNED);
                        convertCandidateToEmployee(candidate.getId(), identityCardNumber);
                        emailService.sendEmail(candidate.getEmail(), "Hồ sơ ứng tuyển của bạn: "
                                + candidate.getJobPosition().getJobPositionName(), "Chúc mừng! Bạn đã ký hợp đồng và trở thành một phần của đội ngũ của chúng tôi.");
                    }
                    break;
                case REFUSE:
                    if (candidate.getCurrentStatus() == Candidate.CandidateStatus.FIRST_INTERVIEW) {
                        candidate.setFirstInterviewStatus(Candidate.InterviewStatus.FAILED);
                    } else if (candidate.getCurrentStatus() == Candidate.CandidateStatus.SECOND_INTERVIEW) {
                        candidate.setSecondInterviewStatus(Candidate.InterviewStatus.FAILED);
                    }
                    candidate.setCurrentStatus(Candidate.CandidateStatus.REFUSE);
                    emailService.sendEmail(candidate.getEmail(), "Hồ sơ ứng tuyển của bạn: "
                            + candidate.getJobPosition().getJobPositionName(), "Rất tiếc, hồ sơ của bạn không phù hợp với yêu cầu của chúng tôi.");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status");
            }
            candidateRepositories.save(candidate);
            return ResponseEntity.ok("Candidate status updated successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidate not found");
    }


    public ResponseEntity<String> setInterviewTime(Long id, Map<String, String> payload) {
        Candidate candidate = getCandidate(id);
        if (candidate != null && candidate.getCurrentStatus() == Candidate.CandidateStatus.INITIAL_REVIEW) {
            LocalDateTime dateTime = LocalDateTime.parse(payload.get("interviewTime"), FORMATTER);
            if (dateTime.isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ngày phỏng vấn không thể là quá khứ");
            }
            candidate.setInterviewTime(dateTime);
            candidate.setCurrentStatus(Candidate.CandidateStatus.FIRST_INTERVIEW);
            candidate.setFirstInterviewStatus(Candidate.InterviewStatus.PENDING);
            String interviewSchedule = "<p>Xin chào, \n </p>" + "\n"
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
            candidateRepositories.save(candidate);
            return ResponseEntity.ok("Interview time set successfully for candidate with ID: " + id);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidate not found");
    }

    public ResponseEntity<String> setSecondInterviewTime(Long id, Map<String, String> payload2) {
        Candidate candidate = getCandidate(id);
        if (candidate != null && candidate.getCurrentStatus() == Candidate.CandidateStatus.FIRST_INTERVIEW) {
            candidate.setFirstInterviewStatus(Candidate.InterviewStatus.PASSED);
            LocalDateTime dateTime = LocalDateTime.parse(payload2.get("secondInterviewTime"), FORMATTER);
            if (dateTime.isBefore(candidate.getInterviewTime())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ngày phỏng vấn lần 2 phải sau ngày phỏng vấn lần 1");
            }
            candidate.setSecondInterviewTime(dateTime);
            candidate.setCurrentStatus(Candidate.CandidateStatus.SECOND_INTERVIEW);
            candidate.setSecondInterviewStatus(Candidate.InterviewStatus.PENDING);
            String secondInterviewSchedule = "<p>Xin chào, \n </p>" + "\n"
                    + "<p>Cảm ơn bạn đã tham gia cuộc phỏng vấn đầu tiên cho vị trí <b style=\"color:#9a6c8e\">“"
                    + candidate.getJobPosition().getJobPositionName()
                    + "”</b> tại <b>Tolo</b></p>" + "\n"
                    + "<p>Chúng tôi rất ấn tượng với kinh nghiệm và kỹ năng của bạn, và chúng tôi muốn mời bạn tham gia một cuộc phỏng vấn thứ hai.</p>"
                    + "<p>Chúng tôi muốn đặt lịch phỏng vấn thứ hai vào</p>" + dateTime.toString()
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
                    + candidate.getJobPosition().getJobPositionName(), secondInterviewSchedule);
            candidateRepositories.save(candidate);
            return ResponseEntity.ok("Second interview time set successfully for candidate with ID: " + id);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidate not found");
    }

    public ResponseEntity<String> makeOffer(Long id, JobOffer jobOffer) {
        Candidate candidate = getCandidate(id);
        if (candidate != null && candidate.getCurrentStatus() == Candidate.CandidateStatus.SECOND_INTERVIEW) {
            candidate.setSecondInterviewStatus(Candidate.InterviewStatus.PASSED);
            jobOffer = jobOfferRepositories.save(jobOffer);
            candidate.setJobOffer(jobOffer);
            candidate.setCurrentStatus(Candidate.CandidateStatus.OFFER_MADE);
            String offerDetails =
                    "<p>Xin chào, \n </p>" + "\n"
                            + "<p>Chúng tôi rất vui mừng thông báo rằng <b>Tolo</b> muốn mời bạn gia nhập đội ngũ của chúng tôi. Chúng tôi đã ấn tượng với kinh nghiệm và kỹ năng của bạn và chúng tôi tin rằng bạn sẽ là một sự bổ sung tuyệt vời cho đội ngũ của chúng tôi."
                            + "\n"
                            + "<p>Chúng tôi muốn đề xuất cho với </p>"
                            + "<p>Lương cơ bản: " + jobOffer.getMonthlySalary() + " đồng</p> "
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
            candidateRepositories.save(candidate);
            return ResponseEntity.ok("Job offer made successfully to candidate with ID: " + id);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidate not found");
    }

    public void convertCandidateToEmployee(Long candidateId, String identityCardNumber) {
        Candidate candidate = getCandidate(candidateId);
        if (candidate != null && candidate.getCurrentStatus() == Candidate.CandidateStatus.CONTRACT_SIGNED) {
            if (identityCardNumber != null && !identityCardNumber.isEmpty()) { // Kiểm tra null và rỗng
                System.out.println(identityCardNumber);
                Employee employee = new Employee();
                PersonalInfo personalInfo = new PersonalInfo();
                Contract contract;
                contract = new Contract();

                employee.setFullName(candidate.getCandidateName());
                employee.setPhoneNumber(candidate.getPhoneNumber());
//                employee.getPosition.getPositonName(candidate.getJobPosition().getJobPositionName());
                List<Experiences> experiences = new ArrayList<>();
                for (Experiences experience : candidate.getExperiences()) {
                    Experiences newExperience = new Experiences();
                    newExperience.setExperienceName(experience.getExperienceName());
                    newExperience.setRating(experience.getRating());
                    experiences.add(newExperience);
                }
                employee.setExperiences(experiences);
                List<Skills> skills = new ArrayList<>();
                for (Skills skill : candidate.getSkills()) {
                    Skills newSkill = new Skills();
                    newSkill.setSkillName(skill.getSkillName());
                    newSkill.setRating(skill.getRating());
                    skills.add(newSkill);
                }
                employee.setSkills(skills);

                personalInfo.setPersonalEmail(candidate.getEmail());
                personalInfo.setSchool(candidate.getSchool());
                personalInfo.setCertificates(candidate.getCertificates());
                personalInfo.setCertificateLevel(candidate.getCertificateLevel());
                personalInfo.setFieldOfStudy(candidate.getFieldOfStudy());
                personalInfo.setIdentityCardNumber(identityCardNumber);
                contract.setStartDate(candidate.getJobOffer().getStartDate());
                contract.setEndDate(candidate.getJobOffer().getEndDate());
                contract.setMonthlySalary(candidate.getJobOffer().getMonthlySalary());
                contract.setNoteContract(candidate.getJobOffer().getNoteContract());
                contract.setSignDate(LocalDate.now());
                employee.setPersonalInfo(personalInfo);
                employee.setContract(contract);
                employeeRepositories.save(employee);
            } else {
                System.out.println("vcc");
            }

        }
    }

    public List<Candidate> getCandidate() {
        return candidateRepositories.findAll();
    }

    public CandidateDTO createCandidate(Candidate candidate) {
        if (candidate.getJobPosition() == null || candidate.getJobPosition().getId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn vị trí công việc.");
        }
        if (candidate.getCandidateName() == null || candidate.getCandidateName().trim().isEmpty()) {
            throw new RuntimeException("Vui lòng điền đầy đủ thông tin vào form trước khi lưu.");
        }
        JobPosition jobPosition = jobPositionRepositories.findById(candidate.getJobPosition().getId())
                .orElseThrow(() -> new IllegalArgumentException("JobPosition not found with id " + candidate.getJobPosition().getId()));
        candidate.setJobPosition(jobPosition);
        candidate.setFirstInterviewStatus(Candidate.InterviewStatus.NOT_APPLICABLE);
        candidate.setSecondInterviewStatus(Candidate.InterviewStatus.NOT_APPLICABLE);
        candidate.setCurrentStatus(Candidate.CandidateStatus.NEW);
        for (Skills skill : candidate.getSkills()) {
            SkillName skillName = skillNameRepositories.findById(skill.getSkillName().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy SkillName với id: " + skill.getSkillName().getId()));
            skill.setSkillName(skillName);
        }

        for (Experiences experience : candidate.getExperiences()) {
            ExperienceName experienceName = experienceNameRepositories.findById(experience.getExperienceName().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ExperienceName với id: " + experience.getExperienceName().getId()));
            experience.setExperienceName(experienceName);
        }


        Candidate savedCandidate = candidateRepositories.save(candidate);

        CandidateDTO candidateDTO = convertToDTO(savedCandidate);
        String htmlContent = "<p>Xin chào, \n </p>" + "\n"
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
        return candidateDTO;
    }

    public CandidateDTO updateCandidateInfo(Long id, CandidateDTO candidateDetailsDTO) {
        if (candidateDetailsDTO.getCandidateName() == null || candidateDetailsDTO.getCandidateName().trim().isEmpty()) {
            throw new RuntimeException("Vui lòng điền đầy đủ thông tin vào form trước khi lưu.");
        }
        Candidate candidate = getCandidate(id);
        if (candidate != null) {
            JobPosition jobPositionDetailsDTO = jobPositionRepositories.findById(candidateDetailsDTO.getJobPosition().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("JobPosition not found with id " + candidateDetailsDTO.getJobPosition().getId()));
            candidate.setJobPosition(jobPositionDetailsDTO);
            candidate.setCandidateName(candidateDetailsDTO.getCandidateName());
            candidate.setEmail(candidateDetailsDTO.getEmail());
            candidate.setJobPosition(candidateDetailsDTO.getJobPosition());
            candidate.setPhoneNumber(candidateDetailsDTO.getPhoneNumber());
            candidate.setDateApplied(candidateDetailsDTO.getDateApplied());
            candidate.setResumeFilePath(candidateDetailsDTO.getResumeFilePath());
            List<Skills> newSkills = new ArrayList<>();
            for (Skills skill : candidateDetailsDTO.getSkills()) {
                SkillName skillName = skillNameRepositories.findById(skill.getSkillName().getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy SkillName với id: " + skill.getSkillName().getId()));
                skill.setSkillName(skillName);
                newSkills.add(skill);
            }
            candidate.setSkills(newSkills);

            List<Experiences> newExperiences = new ArrayList<>();
            for (Experiences experience : candidateDetailsDTO.getExperiences()) {
                ExperienceName experienceName = experienceNameRepositories.findById(experience.getExperienceName().getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy ExperienceName với id: " + experience.getExperienceName().getId()));
                experience.setExperienceName(experienceName);
                newExperiences.add(experience);
            }
            candidate.setExperiences(newExperiences);

            candidate = candidateRepositories.save(candidate);

            CandidateDTO candidateDTO = convertToDTO(candidate);

            return candidateDTO;
        }
        return null;
    }

    public CandidateDTO convertToDTO(Candidate candidate) {
        return modelMapper.map(candidate, CandidateDTO.class);
    }

    public Map<Candidate.CandidateStatus, Long> getCandidateCountByStatus() {
        return candidateRepositories.findAll().stream()
                .collect(Collectors.groupingBy(Candidate::getCurrentStatus, Collectors.counting()));
    }

    public List<SkillName> getAllSkillNames() {
        return skillNameRepositories.findAll();
    }

    public List<ExperienceName> getAllExperienceNames() {
        return experienceNameRepositories.findAll();
    }
}