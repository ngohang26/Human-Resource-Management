package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.dto.*;
import com.hrm.Human.Resource.Management.entity.*;
import com.hrm.Human.Resource.Management.repositories.*;
import com.hrm.Human.Resource.Management.response.ErrorResponse;
import com.hrm.Human.Resource.Management.response.ResourceNotFoundException;
import com.hrm.Human.Resource.Management.service.EmployeeService;
import com.hrm.Human.Resource.Management.service.PositionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepositories employeeRepositories;

    @Autowired
    private PersonalInfoRepositories personalInfoRepositories;

    @Autowired
    private PositionRepositories positionRepositories;

    @Autowired
    private DepartmentRepositories departmentRepositories;

    @Autowired
    private AllowanceRepositories allowanceRepositories;

    @Autowired
    private EmployeeAllowanceRepositories employeeAllowanceRepositories;

    @Autowired
    private AttendanceRepositories attendanceRepositories;

    @Autowired
    private ContractRepositories contractRepositories;

    @Autowired
    private SkillNameRepositories skillNameRepositories;

    @Autowired
    private ExperienceNameRepositories experienceNameRepositories;

    @Autowired
    private TerminationReasonRepositories terminationReasonRepositories;

    @Autowired
    private PositionService positionService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<Employee> searchEmployee(String keyword) {
        return employeeRepositories.findByFullNameContaining(keyword);
    }

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Employee getEmployeeByEmployeeCode(String employeeCode) {
        return employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);
    }

//    @Override
//    public Optional<Employee> getEmployeeById(Long id) {
//        return employeeRepositories.findById(id);
//    }

    @Override
    public List<Employee> getEmployees() {
        return employeeRepositories.findAll();
    }

    @Override
    public List<Employee> getActiveEmployees() {
        return employeeRepositories.findAllByEmploymentStatus(Employee.EmploymentStatus.ACTIVE);
    }

    @Override
    public List<Employee> getTerminatedEmployees() {
        return employeeRepositories.findAllByEmploymentStatus(Employee.EmploymentStatus.TERMINATED);
    }

    @Override
    public List<Employee> getEmployeeEntities() {
        return employeeRepositories.findAll();
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return employeeRepositories.findById(id);
    }

    @Override
    public Employee save(Employee employee) {
        return employeeRepositories.save(employee);
    }

    @Override
    public EmployeeDTO getEmployeeByEmployeeCodeDTO(String employeeCode) {
        Employee employee = employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);
        return convertToDTO(employee);
    }

    @Override
    public Optional<EmployeeDTO> getEmployeeDTOById(Long id) {
        Optional<Employee> employee = employeeRepositories.findById(id);
        return employee.map(this::convertToDTO);
    }

    @Override
    public List<EmployeeDTO> getEmployeesDTO() {
        List<Employee> employees = employeeRepositories.findAll();
        return employees.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeeDTOEntities() {
        List<Employee> employees = employeeRepositories.findAll();
        return employees.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {
        if (employeeDTO.getFullName() == null || employeeDTO.getFullName().trim().isEmpty()) {
            throw new RuntimeException("Vui lòng điền đầy đủ thông tin vào form trước khi lưu.");
        }
        Employee employee = convertToEntity(employeeDTO);
        String identityCardNumber = employee.getPersonalInfo().getIdentityCardNumber();
        Employee existingEmployee = findEmployeeByIdentityCardNumber(identityCardNumber);
        if (existingEmployee != null) {
            throw new RuntimeException("Đã tồn tại nhân viên với số CCCD " + identityCardNumber);
        }
        Position position = positionRepositories.findById(employee.getPosition().getId())
                .orElseThrow(() -> new IllegalArgumentException("Position not found with id " + employee.getPosition().getId()));
        employee.setPosition(position);

        Department department = position.getDepartment();
        employee.setDepartment(department);

        for (Skills skill : employee.getSkills()) {
            SkillName skillName = skillNameRepositories.findById(skill.getSkillName().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy SkillName với id: " + skill.getSkillName().getId()));
            skill.setSkillName(skillName);
        }

        for (Experiences experience : employee.getExperiences()) {
            ExperienceName experienceName = experienceNameRepositories.findById(experience.getExperienceName().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ExperienceName với id: " + experience.getExperienceName().getId()));
            experience.setExperienceName(experienceName);
        }


        Employee savedEmployee = employeeRepositories.save(employee);

        return convertToDTO(savedEmployee);
    }


    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDetailsDTO) {
        if (employeeDetailsDTO.getFullName() == null || employeeDetailsDTO.getFullName().trim().isEmpty()) {
            throw new RuntimeException("Vui lòng điền đầy đủ thông tin vào form trước khi lưu.");
        }
        Optional<Employee> optionalEmployee = employeeRepositories.findById(id);
        if (!optionalEmployee.isPresent()) {
            throw new RuntimeException("Employee not found.");
        }
        Employee employee = optionalEmployee.get();

        PersonalInfo personalInfo = employee.getPersonalInfo();
        PersonalInfo newPersonalInfo = employeeDetailsDTO.getPersonalInfo();
        String newIdentityCardNumber = newPersonalInfo.getIdentityCardNumber();
        if (newIdentityCardNumber != null && !personalInfo.getIdentityCardNumber().equals(newIdentityCardNumber)) {
            Employee existingEmployee = findEmployeeByIdentityCardNumber(newIdentityCardNumber);
            if (existingEmployee != null) {
                throw new RuntimeException("Identity Card Number " + newIdentityCardNumber + " already exists.");
            } else {
                personalInfo.setIdentityCardNumber(newIdentityCardNumber);
                personalInfo.setFieldOfStudy(newPersonalInfo.getFieldOfStudy());
            }
        }
        personalInfo.setNationality(newPersonalInfo.getNationality());
        personalInfo.setBirthDate(newPersonalInfo.getBirthDate());
        personalInfo.setFieldOfStudy(newPersonalInfo.getFieldOfStudy());
        personalInfo.setPersonalEmail(newPersonalInfo.getPersonalEmail());
        personalInfo.setBirthPlace(newPersonalInfo.getBirthPlace());
        personalInfo.setSchool(newPersonalInfo.getSchool());
        personalInfo.setCertificateLevel(newPersonalInfo.getCertificateLevel());

        employee.setFullName(employeeDetailsDTO.getFullName());
        employee.setPhoneNumber(employeeDetailsDTO.getPhoneNumber());
        employee.setImage(employeeDetailsDTO.getImage());
        employee.setWorkEmail(employeeDetailsDTO.getWorkEmail());
        employee.setPhoneContactER(employeeDetailsDTO.getPhoneContactER());
        employee.setNameContactER(employeeDetailsDTO.getPhoneContactER());

        List<Skills> newSkills = new ArrayList<>();
        for (Skills skill : employeeDetailsDTO.getSkills()) {
            SkillName skillName = skillNameRepositories.findById(skill.getSkillName().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy SkillName với id: " + skill.getSkillName().getId()));
            skill.setSkillName(skillName);
            newSkills.add(skill);
        }
        employee.setSkills(newSkills);

        List<Experiences> newExperiences = new ArrayList<>();
        for (Experiences experience : employeeDetailsDTO.getExperiences()) {
            ExperienceName experienceName = experienceNameRepositories.findById(experience.getExperienceName().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ExperienceName với id: " + experience.getExperienceName().getId()));
            experience.setExperienceName(experienceName);
            newExperiences.add(experience);
        }
        employee.setExperiences(newExperiences);
        Position position = positionRepositories.findById(employeeDetailsDTO.getPosition().getId())
                .orElseThrow(() -> new IllegalArgumentException("Position not found with id " + employeeDetailsDTO.getPosition().getId()));
        employee.setPosition(position);

        Department department = position.getDepartment();
        employee.setDepartment(department);

        employee = employeeRepositories.save(employee);

        return convertToDTO(employee);
    }

    public EmployeeDTO convertToDTO(Employee employee) {
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    public Employee convertToEntity(EmployeeDTO employeeDTO) {
        return modelMapper.map(employeeDTO, Employee.class);
    }

    @Override
    public ResponseEntity<ErrorResponse> deleteEmployee(Long id) {
        Optional<Employee> employee = employeeRepositories.findById(id);
        if (employee.isPresent()) {
            Employee p = employee.get();
            employeeRepositories.save(p);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ErrorResponse("ok", "Delete employee successfully", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse("failed", "Cannot find employee to delete", "")
        );
    }

    @Override
    public ResponseEntity<ErrorResponse> hardDeleteEmployee(Long id) {
        boolean exists = employeeRepositories.existsById(id);
        if (exists) {
            employeeRepositories.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ErrorResponse("ok", "Delete employee succesfully", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse("failed", "Cannot find employee to delete", "")
        );
    }

    @Override
    public Employee findEmployeeByIdentityCardNumber(String identityCardNumber) {
        return employeeRepositories.findEmployeeByPersonalInfoIdentityCardNumber(identityCardNumber);
    }

    @Override
    public List<EmployeeContractDTO> getAllEmployeeContracts() {
        List<Employee> employees = employeeRepositories.findAll();
        return employees.stream()
                .map(employee -> {
                    Contract contract = employee.getContract();
                    if (contract == null) {
                        return null;
                    }
                    return new EmployeeContractDTO(
                            employee.getId(),
                            employee.getEmployeeCode(),
                            employee.getFullName(),
                            employee.getPosition(),
                            employee.getDepartment(),
                            contract.getStartDate(),
                            contract.getEndDate(),
                            contract.getSignDate(),
                            contract.getContractStatus(),
                            contract.getNoteContract(),
                            contract.getNumberOfSignatures(),
                            contract.getContractCode(),
                            contract.getMonthlySalary());
                })
                .filter(Objects::nonNull) // Loại bỏ các giá trị null
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeContractDTO createContract(String employeeCode, Contract contract) {
        Employee employee = employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found");
        }
        ;
        Contract existingContract = employee.getContract();
        if (existingContract != null) {
            throw new IllegalStateException("Hợp đồng đã tồn tại với nhân viên này");
        }
        contract.setEmployee(employee);
        contract = contractRepositories.save(contract);

        employee.setContract(contract);
        employeeRepositories.save(employee);

        return new EmployeeContractDTO(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getPosition(),
                employee.getDepartment(),
                employee.getContract().getStartDate(),
                employee.getContract().getEndDate(),
                employee.getContract().getSignDate(),
                employee.getContract().getContractStatus(),
                employee.getContract().getNoteContract(),
                employee.getContract().getNumberOfSignatures(),
                employee.getContract().getContractCode(),
                employee.getContract().getMonthlySalary()
        );
    }

    @Override
    public EmployeeContractDTO updateContract(String employeeCode, Contract updatedContract) {
        Employee employee = employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found");
        }
        Contract existingContract = employee.getContract();
        if (existingContract == null) {
            throw new IllegalStateException("No contract found for this employee");
        }

        // Cập nhật các trường cho phép
        if (updatedContract.getNumberOfSignatures() > 0) {
            existingContract.setNumberOfSignatures(updatedContract.getNumberOfSignatures());
        }
        if (updatedContract.getStartDate() != null && updatedContract.getStartDate().isAfter(LocalDate.now())) {
            existingContract.setStartDate(updatedContract.getStartDate());
        }
        if (updatedContract.getEndDate() != null && updatedContract.getEndDate().isAfter(LocalDate.now())) {
            existingContract.setEndDate(updatedContract.getEndDate());
        }
        if (updatedContract.getMonthlySalary() != null && updatedContract.getMonthlySalary().compareTo(BigDecimal.ZERO) > 0) {
            existingContract.setMonthlySalary(updatedContract.getMonthlySalary());
        }
        if (updatedContract.getNoteContract() != null) {
            existingContract.setNoteContract(updatedContract.getNoteContract());
        }

        // Lưu contract cập nhật
        existingContract = contractRepositories.save(existingContract);

        // Cập nhật thông tin employee
        employee.setContract(existingContract);
        employeeRepositories.save(employee);

        // Trả về thông tin của employee và contract mới
        return new EmployeeContractDTO(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getPosition(),
                employee.getDepartment(),
                existingContract.getStartDate(),
                existingContract.getEndDate(),
                existingContract.getSignDate(),
                existingContract.getContractStatus(),
                existingContract.getNoteContract(),
                existingContract.getNumberOfSignatures(),
                existingContract.getContractCode(),
                existingContract.getMonthlySalary()
        );
    }

    @Override
    public EmployeeContractDTO getContract(String employeeCode) {
        Employee employee = employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found");
        }
        Contract contract = employee.getContract();
        if (contract == null) {
            return null;
        }
        return new EmployeeContractDTO(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getPosition(),
                employee.getDepartment(),
                employee.getContract().getStartDate(),
                employee.getContract().getEndDate(),
                employee.getContract().getSignDate(),
                employee.getContract().getContractStatus(),
                employee.getContract().getNoteContract(),
                employee.getContract().getNumberOfSignatures(),
                employee.getContract().getContractCode(),
                employee.getContract().getMonthlySalary()
        );
    }

    @Override
    public List<String> getEmployeeCodes() {
        return employeeRepositories.findAll().stream()
                .map(Employee::getEmployeeCode)
                .collect(Collectors.toList());
    }

    @Override
    public GenderPercentage getGenderPercentage() {

        Query totalQuery = entityManager.createQuery("SELECT COUNT(e) FROM Employee e");
        long totalEmployees = (Long) totalQuery.getSingleResult();

        Query maleQuery = entityManager.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.personalInfo.sex = :sex");
        maleQuery.setParameter("sex", "Nam");
        long maleEmployees = (Long) maleQuery.getSingleResult();

        maleQuery.setParameter("sex", "Nữ");
        long femaleEmployees = (Long) maleQuery.getSingleResult();

        int malePercentage = (int) (((double) maleEmployees / totalEmployees) * 100);
        int femalePercentage = (int) (((double) femaleEmployees / totalEmployees) * 100);

        GenderPercentage genderPercentage = new GenderPercentage();
        genderPercentage.setMalePercentage(malePercentage);
        genderPercentage.setFemalePercentage(femalePercentage);

        return genderPercentage;
    }

    @Transactional
    @Override
    public ResponseEntity<ErrorResponse> updateEmployeeStatus(Long id, Long reasonId, LocalDate terminationDate) {
        Optional<Employee> employeeOpt = employeeRepositories.findById(id);
        if (!employeeOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorResponse("failed", "Không thể tìm thấy nhân viên với id " + id, "")
            );
        }
        Employee employee = employeeOpt.get();
        if (reasonId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("failed", "Vui lòng cung cấp lý do nghỉ việc", "")
            );
        }
        if (terminationDate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("failed", "Vui lòng cung cấp ngày nghỉ việc", "")
            );
        }
        if (terminationDate.isBefore(employee.getContract().getStartDate()) || terminationDate.isAfter(employee.getContract().getEndDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("failed", "Ngày nghỉ việc không hợp lệ", "")
            );
        }
        if (terminationDate.isBefore(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("failed", "Ngày nghỉ việc phải là ngày trong tương lai", "")
            );
        }
        TerminationReason reason = terminationReasonRepositories.findById(reasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lý do nghỉ việc với id " + reasonId));
        employee.terminateEmployment(reason, terminationDate);
        employeeRepositories.save(employee);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ErrorResponse("ok", "Trạng thái nhân viên đã được cập nhật", "")
        );
    }
}

