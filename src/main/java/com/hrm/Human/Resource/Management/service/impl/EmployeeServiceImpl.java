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
    private SkillRepositories skillRepositories;

    @Autowired
    private ExperienceRepositories experienceRepositories;

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

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepositories.findById(id);
    }

    @Override
    public List<Employee> getEmployees() {
        return employeeRepositories.findAll();
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

        String departmentName = employee.getDepartmentName();
        if (departmentName != null) {
            Department department = departmentRepositories.findByDepartmentName(departmentName);
            if (department == null) {
                throw new RuntimeException("Vui lòng chọn bộ phận ");
            }
            employee.setDepartment(department);
        }

        String positionName = employee.getPositionName();
        if (positionName != null) {
            Position position = positionRepositories.findByPositionName(positionName);
            if (position == null) {
                throw new RuntimeException("Vui lòng chọn chức vụ.");
            }
            employee.setPosition(position);
        }

        Employee savedEmployee = employeeRepositories.save(employee);

        return convertToDTO(savedEmployee);
    }


    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDetailsDTO) {
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
        employee.setDepartmentName(employeeDetailsDTO.getDepartmentName());
        employee.setPositionName(employeeDetailsDTO.getPositionName());
        employee.setPhoneContactER(employeeDetailsDTO.getPhoneContactER());
        employee.setNameContactER(employeeDetailsDTO.getPhoneContactER());

        List<SkillDTO> newSkillsDTO = employeeDetailsDTO.getSkills();
        if (newSkillsDTO != null) {
            List<Skill> newSkills = newSkillsDTO.stream().map(this::convertSkillToEntity).collect(Collectors.toList());
            employee.setSkills(newSkills);
        }

        List<ExperienceDTO> newExperiencesDTO = employeeDetailsDTO.getExperiences();
        if (newExperiencesDTO != null) {
            List<Experience> newExperiences = newExperiencesDTO.stream().map(this::convertExperienceToEntity).collect(Collectors.toList());
            employee.setExperiences(newExperiences);
        }

        String departmentName = employeeDetailsDTO.getDepartmentName();
        if (departmentName != null) {
            Department department = departmentRepositories.findByDepartmentName(departmentName);
            if (department == null) {
                throw new RuntimeException("Vui lòng chọn bộ phận.");
            }
            employee.setDepartment(department);
        }

        String positionName = employeeDetailsDTO.getPositionName();
        if (positionName != null) {
            Position position = positionRepositories.findByPositionName(positionName);
            if (position == null) {
                throw new RuntimeException("Vui lòng chọn chức vụ.");
            }
            employee.setPosition(position);
        }

        employee = employeeRepositories.save(employee);

        return convertToDTO(employee);
    }

    public Skill convertSkillToEntity(SkillDTO skillDTO) {
        return modelMapper.map(skillDTO, Skill.class);
    }

    public Experience convertExperienceToEntity(ExperienceDTO experienceDTO) {
        return modelMapper.map(experienceDTO, Experience.class);
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
            p.setIsDeleted(true);
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
    public ResponseEntity<ErrorResponse> undoDeleteEmployee(Long id) {
        Optional<Employee> employee = employeeRepositories.findById((id));
        if (employee.isPresent()) {
            Employee p = employee.get();
            p.setIsDeleted(false);
            employeeRepositories.save(p);
            return ResponseEntity.status((HttpStatus.OK)).body(
                    new ErrorResponse("ok", "Undo employee successfully", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse("failed", "Cannot fond employee to undo", "")
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
                        // Nếu không tìm thấy hợp đồng, bỏ qua nhân viên này
                        return null;
                    }
                    return new EmployeeContractDTO(
                            employee.getId(),
                            employee.getEmployeeCode(),
                            employee.getFullName(),
                            employee.getDepartmentName(),
                            employee.getPositionName(),
                            contract.getStartDate(),
                            contract.getEndDate(),
                            contract.getSignDate(),
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
            throw new IllegalStateException("Contract already exists for this employee");
        }
        contract.setEmployee(employee);
        contract = contractRepositories.save(contract);

        employee.setContract(contract);
        employeeRepositories.save(employee);

        return new EmployeeContractDTO(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getDepartmentName(),
                employee.getPositionName(),
                employee.getContract().getStartDate(),
                employee.getContract().getEndDate(),
                employee.getContract().getSignDate(),
                employee.getContract().getNoteContract(),
                employee.getContract().getNumberOfSignatures(),
                employee.getContract().getContractCode(),
                employee.getContract().getMonthlySalary()
        );
    }

    @Override
    public EmployeeContractDTO updateContract(String employeeCode, Contract contract) {
        Employee employee = employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found");
        }
        Contract existingContract = employee.getContract();
        if (existingContract == null) {
            throw new IllegalStateException("No contract found for this employee");
        }
        BeanUtils.copyProperties(contract, existingContract, "id");
        contract = existingContract;

        contract.setEmployee(employee);
        contract = contractRepositories.save(contract);

        employee.setContract(contract);
        employeeRepositories.save(employee);

        return new EmployeeContractDTO(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getDepartmentName(),
                employee.getPositionName(),
                employee.getContract().getStartDate(),
                employee.getContract().getEndDate(),
                employee.getContract().getSignDate(),
                employee.getContract().getNoteContract(),
                employee.getContract().getNumberOfSignatures(),
                employee.getContract().getContractCode(),
                employee.getContract().getMonthlySalary()
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
                employee.getDepartmentName(),
                employee.getPositionName(),
                employee.getContract().getStartDate(),
                employee.getContract().getEndDate(),
                employee.getContract().getSignDate(),
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
}

