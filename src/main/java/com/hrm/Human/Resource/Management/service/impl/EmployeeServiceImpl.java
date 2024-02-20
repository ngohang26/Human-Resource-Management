package com.hrm.Human.Resource.Management.service.impl;

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
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
public ResponseEntity<?> addEmployee(Employee employee) {
    Optional<Employee> existingEmployee = employeeRepositories.findByFullNameContaining(employee.getFullName());
    if (existingEmployee.isPresent()) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("The employee already exists.");
    }

    try {
        PersonalInfo personalInfo = employee.getPersonalInfo();
        if (personalInfo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PersonalInfo must not be null.");
        }
        PersonalInfo savedPersonalInfo = personalInfoRepositories.save(personalInfo);
        employee.setPersonalInfo(savedPersonalInfo);

        Position position = positionRepositories.findByPositionName(employee.getPositionName());
        if (position == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Position does not exist.");
        }
        employee.setPosition(position);

        Department  department = departmentRepositories.findByDepartmentName(employee.getDepartmentName());
        if (department == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Department does not exist.");
        }
        employee.setPosition(position);

        Employee savedEmployee = employeeRepositories.save(employee);

        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    } catch (Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    @Override
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        Employee existingEmployee = employeeRepositories.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));

        existingEmployee.setFullName(updatedEmployee.getFullName());

        return employeeRepositories.save(existingEmployee);
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
}