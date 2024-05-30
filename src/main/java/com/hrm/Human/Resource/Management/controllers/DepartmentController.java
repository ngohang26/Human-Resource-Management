package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.Department;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.repositories.DepartmentRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.repositories.PositionRepositories;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private PositionRepositories positionRepositories;

    @Autowired
    private DepartmentRepositories departmentRepositories;

    @Autowired
    private EmployeeRepositories employeeRepositories;

    @PreAuthorize("hasAuthority('VIEW_DEPARTMENT')")
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

    @PreAuthorize("hasAuthority('ADD_DEPARTMENT')")
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

    @PreAuthorize("hasAuthority('EDIT_DEPARTMENT')")
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

    @PreAuthorize("hasAuthority('DELETE_DEPARTMENT')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        try {
            if (!departmentRepositories.existsById(id)) {
                return new ResponseEntity<>("Không tìm thấy phòng ban", HttpStatus.NOT_FOUND);
            }
            // Kiểm tra xem có bản ghi nào trong bảng 'position' tham chiếu đến 'department_id' này không
            if (positionRepositories.existsByDepartmentId(id)) {
                return new ResponseEntity<>("Không thể xóa phòng ban vì nó đang được tham chiếu bởi một chức vụ", HttpStatus.CONFLICT);
            }
            departmentRepositories.deleteById(id);
            return new ResponseEntity<>("Xóa phòng ban thành công", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Có lỗi xảy ra khi xóa phòng ban", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
