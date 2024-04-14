package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.Department;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.repositories.DepartmentRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
//@RestController
//@RequestMapping("/departments")
//public class DepartmentController {
//
//    @Autowired
//    private DepartmentRepositories departmentRepositories;
//
//    @Autowired
//    private EmployeeRepositories employeeRepositories;
//
//    @GetMapping(path = "getAllDepartments")
//    public List<Department> getAllDepartments() {
//        return departmentRepositories.findAll();
//    }
//
////        @GetMapping(path = "/{id}")
////        public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
////            Optional<Employee> employee = employeeRepositories.findById(id);
////            return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
////        }
//
//    @PostMapping("/addDepartment")
//    public ResponseEntity<?> addEmployee(@RequestBody Department department, @RequestParam String codeName) {
//        try {
//            Employee manager = employeeRepositories.findByCodeName(codeName);
//            if (manager == null) {
//                return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
//            }
//            department.setManager(manager);
//
//            Department savedDepartment = departmentRepositories.save(department);
//
//            // Create a new object to return
//            Map<String, Object> response = new HashMap<>();
//            response.put("department", savedDepartment);
//            response.put("managerCode", manager.getEmployeeCode());
//            response.put("managerName", manager.getFullName());
//
//            return new ResponseEntity<>(response, HttpStatus.CREATED);
//        } catch (DataIntegrityViolationException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("The department already taken.");
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//    }
//}

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentRepositories departmentRepositories;

    @Autowired
    private EmployeeRepositories employeeRepositories;

    @GetMapping(path = "getAllDepartments")
    public List<Map<String, Object>> getAllDepartments() {
        List<Department> departments = departmentRepositories.findAll();
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (Department department : departments) {
            Employee manager = department.getManager();

            long employeeCount = employeeRepositories.countByDepartment(department);

            Map<String, Object> response = new HashMap<>();
            response.put("id", department.getId());
            response.put("departmentName", department.getDepartmentName());
            response.put("managerCode", manager.getEmployeeCode());
            response.put("managerName", manager.getFullName());
            response.put("managerCodeName", manager.getCodeName());
            response.put("employeeCount", employeeCount);

            responseList.add(response);
        }

        return responseList;
    }

    @PostMapping("/addDepartment")
    public ResponseEntity<?> addEmployee(@RequestBody Department department, @RequestParam String employeeCode) {
        Employee manager = employeeRepositories.findByEmployeeCode(employeeCode);
        if (manager == null) {
            return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
        }

        Optional<Department> existingDepartment = Optional.ofNullable(departmentRepositories.findByDepartmentName(department.getDepartmentName()));
        if (existingDepartment.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The department already exists.");
        }

        try {
            department.setManager(manager);
            Department savedDepartment = departmentRepositories.save(department);

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedDepartment.getId());
            response.put("departmentName", savedDepartment.getDepartmentName());
            response.put("managerCode", manager.getEmployeeCode());
            response.put("managerName", manager.getFullName());
            response.put("managerCodeName", manager.getCodeName());

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody Department departmentDetails) {
            try {
                Department department = departmentRepositories.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Department not found with id " + id));

                Optional<Department> existingDepartment = Optional.ofNullable(departmentRepositories.findByDepartmentName(departmentDetails.getDepartmentName()));
                if (existingDepartment.isPresent() && !existingDepartment.get().getId().equals(id)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("The department name is already taken.");
                }

                Employee manager = employeeRepositories.findByEmployeeCode(departmentDetails.getManager().getEmployeeCode());
                if (manager == null) {
                    return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
                }

                department.setDepartmentName(departmentDetails.getDepartmentName());
                department.setManager(manager);

                Department updatedDepartment = departmentRepositories.save(department);

                Map<String, Object> response = new HashMap<>();
                response.put("id", updatedDepartment.getId());
                response.put("departmentName", updatedDepartment.getDepartmentName());
                response.put("managerCode", manager.getEmployeeCode());
                response.put("managerName", manager.getFullName());
                response.put("managerCodeName", manager.getCodeName());

                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        try {
            if (!departmentRepositories.existsById(id)) {
                return new ResponseEntity<>("Department not found", HttpStatus.NOT_FOUND);
            }
            departmentRepositories.deleteById(id);
            return new ResponseEntity<>("Department deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
