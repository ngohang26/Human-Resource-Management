package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.Department;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.repositories.DepartmentRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentRepositories departmentRepositories;

    @Autowired
    private EmployeeRepositories employeeRepositories;

    @GetMapping(path = "getAllDepartments")
    public List<Department> getAllDepartments() {
        return departmentRepositories.findAll();
    }

//        @GetMapping(path = "/{id}")
//        public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
//            Optional<Employee> employee = employeeRepositories.findById(id);
//            return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
//        }

    @PostMapping("/addDepartment")
    public ResponseEntity<?> addEmployee(@RequestBody Department department, @RequestParam String employeeCode) {
        try {
            Employee manager = employeeRepositories.findByEmployeeCode(employeeCode);
            if (manager == null) {
                return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
            }
            department.setManager(manager);

            Department savedDepartment = departmentRepositories.save(department);

            // Create a new object to return
            Map<String, Object> response = new HashMap<>();
            response.put("department", savedDepartment);
            response.put("managerCode", manager.getEmployeeCode());
            response.put("managerName", manager.getFullName());

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The department already taken.");
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
