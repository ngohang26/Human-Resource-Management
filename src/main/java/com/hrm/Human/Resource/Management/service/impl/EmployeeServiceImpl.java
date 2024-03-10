package com.hrm.Human.Resource.Management.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hrm.Human.Resource.Management.entity.Department;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.PersonalInfo;
import com.hrm.Human.Resource.Management.entity.Position;
import com.hrm.Human.Resource.Management.repositories.DepartmentRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.repositories.PersonalInfoRepositories;
import com.hrm.Human.Resource.Management.repositories.PositionRepositories;
import com.hrm.Human.Resource.Management.response.EmployeeResponse;
import com.hrm.Human.Resource.Management.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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
    private ImageStorageService imageStorageService;

    @Override
    public Optional<Employee> searchEmployee(String keyword) {
        return employeeRepositories.findByFullNameContaining(keyword);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return employeeRepositories.findById(id);
    }

    @Override
    public Employee getEmployeeByEmployeeCode(String employeeCode) {
        return employeeRepositories.findByEmployeeCode(employeeCode);
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

    //    @Override
//    public ResponseEntity<?> addEmployee(Employee employee) {
//        Optional<PersonalInfo> existingPersonalInfo = personalInfoRepositories.findByIdentityCardNumber(employee.getPersonalInfo().getIdentityCardNumber());
//        if (existingPersonalInfo.isPresent()) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("The employee already exists.");
//        }
//
//        try {
//            Employee savedEmployee = employeeRepositories.save(employee);
//            return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//@Override
//public ResponseEntity<?> addEmployee(Employee employee) {
//    Optional<Employee> existingEmployee = employeeRepositories.findByFullNameContaining(employee.getFullName());
//    if (existingEmployee.isPresent()) {
//        return ResponseEntity.status(HttpStatus.CONFLICT).body("The employee already exists.");
//    }
//
//    try {
//        Employee savedEmployee = employeeRepositories.save(employee);
//        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
//    } catch (Exception e) {
//        e.printStackTrace();
//        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}
@Override
public ResponseEntity<?> addEmployee(String employeeString, MultipartFile file) {

    System.out.println("Adding employee: " + employeeString);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    Employee employee;
    try {
        employee = objectMapper.readValue(employeeString, Employee.class);
    } catch (JsonProcessingException e) {
        System.out.println("Error parsing JSON: " + e.getMessage());
        System.out.println("Invalid JSON: " + employeeString);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid employee data.");
    }

//        try {
//            employee = objectMapper.readValue(employeeString, Employee.class);
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid employee data.");
//        }
        Optional<Employee> existingEmployee = employeeRepositories.findByFullNameContaining(employee.getFullName());
        if (existingEmployee.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The employee already exists.");
        }

        try {
            // Handle file upload
            if (file != null && !file.isEmpty()) {
                String fileName = imageStorageService.storeFile(file);
                employee.setImage(fileName);
            }

            Position position = positionRepositories.findByPositionName(employee.getPositionName());
            if (position == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Position does not exist.");
            }
            employee.setPosition(position);

            Department  department = departmentRepositories.findByDepartmentName(employee.getDepartmentName());
            if (department == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Department does not exist.");
            }
            employee.setDepartment(department);

            PersonalInfo personalInfo = employee.getPersonalInfo();
            if (personalInfo == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PersonalInfo must not be null.");
            }
            PersonalInfo savedPersonalInfo = personalInfoRepositories.save(personalInfo);
            employee.setPersonalInfo(savedPersonalInfo);

            Employee savedEmployee = employeeRepositories.save(employee);

            return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<?> updateEmployee(Long id, String employeeString, MultipartFile file) {
        Optional<Employee> existingEmployeeOpt = employeeRepositories.findById(id);
        if (!existingEmployeeOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found.");
        }
        Employee existingEmployee = existingEmployeeOpt.get();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Employee updatedEmployee;
        try {
            updatedEmployee = objectMapper.readValue(employeeString, Employee.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid employee data.");
        }
        if (file != null && !file.isEmpty()) {
            String fileName = imageStorageService.storeFile(file);
            existingEmployee.setImage(fileName);
        }
        existingEmployee.setFullName(updatedEmployee.getFullName());
        existingEmployee.setPhoneNumber(updatedEmployee.getPhoneNumber());
        existingEmployee.setWorkEmail(updatedEmployee.getWorkEmail());
        existingEmployee.setNameContactER(updatedEmployee.getNameContactER());
        existingEmployee.setPhoneContactER(updatedEmployee.getPhoneContactER());
        // existingEmployee.setIsDeleted(updatedEmployee.getIsDeleted());

        // Find the updated Department and Position in the database
        Department updatedDepartment = departmentRepositories.findByDepartmentName(updatedEmployee.getDepartmentName());
        if (updatedDepartment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Department does not exist.");
        }
        Position updatedPosition = positionRepositories.findByPositionName(updatedEmployee.getPositionName());
        if (updatedPosition == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Position does not exist.");
        }

        existingEmployee.setDepartment(updatedDepartment);
        existingEmployee.setPosition(updatedPosition);
        existingEmployee.setDepartmentName(updatedDepartment.getDepartmentName());
        existingEmployee.setPositionName(updatedPosition.getPositionName());

        // Update PersonalInfo
        PersonalInfo existingPersonalInfo = existingEmployee.getPersonalInfo();
        PersonalInfo updatedPersonalInfo = updatedEmployee.getPersonalInfo();

        existingPersonalInfo.setBirthPlace(updatedPersonalInfo.getBirthPlace());
        existingPersonalInfo.setIsResident(updatedPersonalInfo.getIsResident());
        existingPersonalInfo.setSex(updatedPersonalInfo.getSex());
        existingPersonalInfo.setIdentityCardNumber(updatedPersonalInfo.getIdentityCardNumber());

        existingPersonalInfo.setBirthDate(updatedPersonalInfo.getBirthDate());
        existingPersonalInfo.setPersonalEmail(updatedPersonalInfo.getPersonalEmail());
        existingPersonalInfo.setCertificateLevel(updatedPersonalInfo.getCertificateLevel());
        existingPersonalInfo.setFieldOfStudy(updatedPersonalInfo.getFieldOfStudy());
        existingPersonalInfo.setSchool(updatedPersonalInfo.getSchool());
        existingPersonalInfo.setNationality(updatedPersonalInfo.getNationality());
        try {
            // Handle file upload
            if (file != null && !file.isEmpty()) {
                String fileName = imageStorageService.storeFile(file);
                existingEmployee.setImage(fileName);
            }

            Employee savedEmployee = employeeRepositories.save(existingEmployee);

            return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    public boolean existsByIdentityCardNumber(String identityCardNumber) {
        return employeeRepositories.existsByPersonalInfoIdentityCardNumber(identityCardNumber);
    }
}
