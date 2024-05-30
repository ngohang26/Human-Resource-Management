package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
import com.hrm.Human.Resource.Management.repositories.AllowanceRepositories;
import com.hrm.Human.Resource.Management.response.ErrorResponse;
import com.hrm.Human.Resource.Management.service.AllowanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/allowance")
public class AllowanceController {
    @Autowired
    private AllowanceRepositories allowanceRepositories;

    @Autowired
    private AllowanceService allowanceService;

    @PreAuthorize("hasAuthority('VIEW_ALLOWANCE')")
    @GetMapping(path = "getAllAllowances")
    public List<Allowance> getAllAllowances() {
        return allowanceService.getAllowance();
    }

    @PreAuthorize("hasAuthority('VIEW_ALLOWANCE')")
    @GetMapping(path = "/{employeeCode}")
    public ResponseEntity<List<EmployeeAllowance>> getAllowancesByEmployeeCode(@PathVariable String employeeCode) {
        List<EmployeeAllowance> allowances = allowanceService.findByEmployeeCode(employeeCode);
        if (allowances.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok(allowances);
        }
    }


    @PreAuthorize("hasAuthority('VIEW_ALLOWANCE')")
    @PostMapping("/addAllowance")
    public ResponseEntity<?> addAllowance(@RequestBody Allowance allowance) {
        return allowanceService.addAllowance(allowance);
    }

    @PreAuthorize("hasAuthority('EDIT_ALLOWANCE')")
    @PutMapping("/updateAllowance/{id}")
    public Allowance updateAllowance(@PathVariable Long id, @RequestBody Allowance allowanceDetails) {
        return allowanceService.updateAllowance(id, allowanceDetails);
    }


    @PreAuthorize("hasAuthority('DELETE_ALLOWANCE')")
    @DeleteMapping("/hardDelete/{id}")
    public ResponseEntity<ErrorResponse> hardDeleteAllowance(@PathVariable Long id) {
        return allowanceService.hardDeleteAllowance(id);
    }


}
