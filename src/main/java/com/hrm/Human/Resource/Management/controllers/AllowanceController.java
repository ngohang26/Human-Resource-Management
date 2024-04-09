package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
import com.hrm.Human.Resource.Management.repositories.AllowanceRepositories;
import com.hrm.Human.Resource.Management.response.AllowanceResponse;
import com.hrm.Human.Resource.Management.service.AllowanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/allowance")
public class AllowanceController {
    @Autowired
    private AllowanceRepositories allowanceRepositories;

    @Autowired
    private AllowanceService allowanceService;

    @GetMapping(path = "getAllAllowances")
    public List<Allowance> getAllAllowances() {
        return allowanceService.getAllowance();
    }

    @GetMapping(path = "/{employeeCode}")
    public ResponseEntity<List<EmployeeAllowance>> getAllowancesByEmployeeCode(@PathVariable String employeeCode) {
        List<EmployeeAllowance> allowances = allowanceService.findByEmployeeCode(employeeCode);
        if (allowances.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok(allowances);
        }
    }


    @PostMapping("/addAllowance")
    public ResponseEntity<?> addAllowance(@RequestBody Allowance allowance) {
        return allowanceService.addAllowance(allowance);
    }

    @PutMapping("/updateAllowance/{id}")
    public Allowance updateAllowance(@PathVariable Long id, @RequestBody Allowance allowanceDetails) {
        return allowanceService.updateAllowance(id, allowanceDetails);
    }


    @DeleteMapping("/hardDelete/{id}")
    public ResponseEntity<AllowanceResponse> hardDeleteAllowance(@PathVariable Long id) {
        return allowanceService.hardDeleteAllowance(id);
    }

    @GetMapping("/calculateTotalAllowanceAmount")
    public ResponseEntity<BigDecimal> getTotalAllowance(@PathVariable String employeeCode) {
        BigDecimal totalAllowance = allowanceService.getTotalAllowance(employeeCode);
        return ResponseEntity.ok(totalAllowance);
    }
}
