package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.Allowance;
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

    @GetMapping(path = "/{id}")
    public ResponseEntity<Allowance> getAllowanceById(@PathVariable Long id) {
        Optional<Allowance> allowance = allowanceService.findById(id);
        return allowance.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
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
    public ResponseEntity<Map<Long, BigDecimal>> calculateTotalAllowanceAmount() {
        Map<Long, BigDecimal> totalAmounts = allowanceService.calculateTotalAllowanceAmountForEachEmployee();
        return ResponseEntity.ok(totalAmounts);
    }
}
