package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
import com.hrm.Human.Resource.Management.repositories.AllowanceRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeAllowanceRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.response.AllowanceResponse;
import com.hrm.Human.Resource.Management.service.AllowanceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AllowanceServiceImpl implements AllowanceService {
    @Autowired
    private AllowanceRepositories allowanceRepositories;

    @Autowired
    private EmployeeRepositories employeeRepositories;

    @Autowired
    private EmployeeAllowanceRepositories employeeAllowanceRepositories;

    @Override
    public List<EmployeeAllowance> findByEmployeeCode(String employeeCode) {
        return employeeAllowanceRepositories.findByEmployee_EmployeeCode(employeeCode);
    }

    @Override
    public List<Allowance> getAllowance() {
        return allowanceRepositories.findAll();
    }

    @Override
    public ResponseEntity<?> addAllowance(Allowance allowance) {
        Optional<Allowance> existingAllowance = allowanceRepositories.findByAllowanceNameContaining(allowance.getAllowanceName());
        if (existingAllowance.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The allowance already exists.");
        }

        try {
            Allowance savedAllowance = allowanceRepositories.save(allowance);
            return new ResponseEntity<>(savedAllowance, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Allowance updateAllowance(Long id, Allowance updatedAllowance) {
        Allowance existingAllowance = allowanceRepositories.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Allowance not found with id " + id));

        existingAllowance.setAllowanceName(updatedAllowance.getAllowanceName());
        existingAllowance.setAllowanceAmount(updatedAllowance.getAllowanceAmount());

        return allowanceRepositories.save(existingAllowance);
    }

    @Override
    public ResponseEntity<AllowanceResponse> hardDeleteAllowance(Long id) {
        boolean exists = allowanceRepositories.existsById(id);
        if (exists) {
            allowanceRepositories.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new AllowanceResponse("ok", "Delete allowance successfully", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new AllowanceResponse("failed", "Cannot find allowance to delete", "")
        );
    }

    //    @Override
//    public List<Allowance> getEmployeeAllowances(String employeeCode) {
//        Employee employee = employeeRepositories.findByEmployeeCode(employeeCode);
//        if (employee == null) {
//            throw new RuntimeException("Employee not found");
//        }
//        List<EmployeeAllowance> employeeAllowances = employeeAllowanceRepositories.findByEmployee(employee);
//        return employeeAllowances.stream().map(employeeAllowance -> {
//            EmployeeAllowanceDTO dto = new EmployeeAllowanceDTO();
//            dto.setAllowanceId(employeeAllowance.getAllowance().getId());
//            dto.setEmployeeCode(employee.getEmployeeCode());
//            dto.setAllowanceName(employeeAllowance.getAllowance().getAllowanceName());
//            dto.setAllowanceAmount(employeeAllowance.getAllowance().getAllowanceAmount());
//            dto.setStartDate(employeeAllowance.getStartDate());
//            dto.setEndDate(employeeAllowance.getEndDate());
//            return dto;
//        }).collect(Collectors.toList());
//    }
    @Override
    public List<Allowance> getAllowancesForEmployee(String employeeCode) {
        Employee employee = employeeRepositories.findByEmployeeCode(employeeCode);
        List<EmployeeAllowance> employeeAllowances = employeeAllowanceRepositories.findByEmployee(employee);
        List<Allowance> allowances = new ArrayList<>();
        for (EmployeeAllowance employeeAllowance : employeeAllowances) {
            allowances.add(employeeAllowance.getAllowance());
        }
        return allowances;
    }

    @Override
    public BigDecimal getTotalAllowance(String employeeCode) {
        Employee employee = employeeRepositories.findByEmployeeCode(employeeCode);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }
        List<EmployeeAllowance> employeeAllowances = employeeAllowanceRepositories.findByEmployee(employee);
        BigDecimal totalAllowance = BigDecimal.ZERO;
        for (EmployeeAllowance employeeAllowance : employeeAllowances) {
            totalAllowance = totalAllowance.add(employeeAllowance.getAllowance().getAllowanceAmount());
        }
        return totalAllowance;
    }
}
