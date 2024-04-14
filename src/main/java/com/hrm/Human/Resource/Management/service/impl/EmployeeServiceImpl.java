package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.dto.EmployeeContractDTO;
import com.hrm.Human.Resource.Management.entity.*;
import com.hrm.Human.Resource.Management.repositories.*;
import com.hrm.Human.Resource.Management.response.EmployeeResponse;
import com.hrm.Human.Resource.Management.response.ResourceNotFoundException;
import com.hrm.Human.Resource.Management.service.EmployeeService;
import com.hrm.Human.Resource.Management.service.PositionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Override
    public Optional<Employee> searchEmployee(String keyword) {
        return employeeRepositories.findByFullNameContaining(keyword);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return employeeRepositories.findById(id);
    }

    @Override
    public Employee getEmployeeByEmployeeCode(String employeeCode) { return employeeRepositories.findByEmployeeCodeOrThrow(employeeCode);}

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepositories.findById(id);
    }

    @Override
    public Employee save(Employee employee) {
        return employeeRepositories.save(employee);
    }

    @Override
    public List<Employee> getEmployees() {return employeeRepositories.findAll();}

    @Override
    public List<Employee> getEmployeeEntities() {
        return employeeRepositories.findAll();
    }

@Override
public Employee saveEmployee(Employee employee) {
    String identityCardNumber = employee.getPersonalInfo().getIdentityCardNumber();
    Employee existingEmployee = findEmployeeByIdentityCardNumber(identityCardNumber);
    if (existingEmployee != null) {
        throw new RuntimeException("Employee with Identity Card Number " + identityCardNumber + " already exists.");
    }
    String departmentName = employee.getDepartmentName();
    if (departmentName != null) {
        Department department = departmentRepositories.findByDepartmentName(departmentName);
        if (department != null) {
            employee.setDepartment(department);
        } else {
            throw new RuntimeException("Department with name " + departmentName + " does not exist.");
        }
    }


    String positionName = employee.getPositionName();
    if (positionName != null) {
        Position position = positionRepositories.findByPositionName(positionName);
        if (position != null) {
            employee.setPosition(position);
        } else {
            throw new RuntimeException("Position with name " + positionName + " does not exist.");
        }
    }

    Employee savedEmployee = employeeRepositories.save(employee);

    for (Skill skill : employee.getSkills()) {
        skill.setEmployee(savedEmployee);
        skillRepositories.save(skill);
    }

    for (Experience experience : employee.getExperiences()) {
        experience.setEmployee(savedEmployee);
        experienceRepositories.save(experience);
    }

    return savedEmployee;
}
    @Override
    public ResponseEntity<?> updateEmployee(Long id, Employee employeeDetails) {
        Optional<Employee> optionalEmployee = employeeRepositories.findById(id);
        if (!optionalEmployee.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found.");
        }
        Employee employee = optionalEmployee.get();
        PersonalInfo personalInfo = employee.getPersonalInfo();
        PersonalInfo newPersonalInfo = employeeDetails.getPersonalInfo();
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

        employee.setFullName(employeeDetails.getFullName());
        employee.setPhoneNumber(employeeDetails.getPhoneNumber());
        employee.setImage(employeeDetails.getImage());
        employee.setDepartmentName(employeeDetails.getDepartmentName());
        employee.setPositionName(employeeDetails.getPositionName());

        String departmentName = employeeDetails.getDepartmentName();
        if (departmentName != null) {
            Department department = departmentRepositories.findByDepartmentName(departmentName);
            if (department != null) {
                employee.setDepartment(department);
            } else {
                throw new RuntimeException("Department with name " + departmentName + " does not exist.");
            }
        }
        String positionName = employeeDetails.getPositionName();
        if (positionName != null) {
            Position position = positionRepositories.findByPositionName(positionName);
            if (position != null) {
                employee.setPosition(position);
            } else {
                throw new RuntimeException("Position with name " + positionName + " does not exist.");
            }
        }
        skillRepositories.deleteAll(employee.getSkills());
        experienceRepositories.deleteAll(employee.getExperiences());

        for (Skill skill : employeeDetails.getSkills()) {
            skill.setEmployee(employee);
            skillRepositories.save(skill);
        }
        for (Experience experience : employeeDetails.getExperiences()) {
            experience.setEmployee(employee);
            experienceRepositories.save(experience);
        }

        employee = employeeRepositories.save(employee);

        return new ResponseEntity<>(employee, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<EmployeeResponse> deleteEmployee(Long id) {
        Optional<Employee> employee = employeeRepositories.findById(id);
        if (employee.isPresent()) {
            Employee p = employee.get();
            p.setIsDeleted(true);
            employeeRepositories.save(p);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new EmployeeResponse("ok", "Delete employee successfully", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new EmployeeResponse("failed", "Cannot find employee to delete", "")
        );
    }

    @Override
    public ResponseEntity<EmployeeResponse> undoDeleteEmployee(Long id){
        Optional<Employee> employee = employeeRepositories.findById((id));
        if (employee.isPresent()) {
            Employee p = employee.get();
            p.setIsDeleted(false);
            employeeRepositories.save(p);
            return ResponseEntity.status((HttpStatus.OK)).body(
                    new EmployeeResponse("ok", "Undo employee successfully", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new EmployeeResponse("failed", "Cannot fond employee to undo", "")
        );
    }

    @Override
    public ResponseEntity<EmployeeResponse> hardDeleteEmployee(Long id){
        boolean exists = employeeRepositories.existsById(id);
        if (exists) {
            employeeRepositories.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new EmployeeResponse("ok", "Delete employee succesfully", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new EmployeeResponse("failed", "Cannot find employee to delete", "")
        );
    }
    @Override
    public Employee findEmployeeByIdentityCardNumber(String identityCardNumber) {
        return employeeRepositories.findEmployeeByPersonalInfoIdentityCardNumber(identityCardNumber);
    }
//    @Override
//    public EmployeeAllowance create(EmployeeAllowance employeeAllowance) {
//        return employeeAllowanceRepositories.save(employeeAllowance);
//    }
//
//    @Override
//    public EmployeeAllowance update(String employeeCode, EmployeeAllowance employeeAllowance) {
//        List<EmployeeAllowance> existingEmployeeAllowances = employeeAllowanceRepositories.findByEmployee_EmployeeCode(employeeCode);
//        if (!existingEmployeeAllowances.isEmpty()) {
//            // Assuming you want to update the first EmployeeAllowance in the list
//            EmployeeAllowance existingEmployeeAllowance = existingEmployeeAllowances.get(0);
//            BeanUtils.copyProperties(employeeAllowance, existingEmployeeAllowance, "employee");
//            return employeeAllowanceRepositories.save(existingEmployeeAllowance);
//        } else {
//            throw new ResourceNotFoundException("EmployeeAllowance not found with employeeCode " + employeeCode);
//        }
//    }
//
//    @Override
//    public void delete(String employeeCode) {
//        List<EmployeeAllowance> existingEmployeeAllowances = employeeAllowanceRepositories.findByEmployee_EmployeeCode(employeeCode);
//        if (!existingEmployeeAllowances.isEmpty()) {
//            // Assuming you want to delete the first EmployeeAllowance in the list
//            EmployeeAllowance existingEmployeeAllowance = existingEmployeeAllowances.get(0);
//            employeeAllowanceRepositories.delete(existingEmployeeAllowance);
//        } else {
//            throw new ResourceNotFoundException("EmployeeAllowance not found with employeeCode " + employeeCode);
//        }
//    }



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
        };
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

}

