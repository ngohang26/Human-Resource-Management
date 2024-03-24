package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.repositories.AllowanceRepositories;
import com.hrm.Human.Resource.Management.response.AllowanceResponse;
import com.hrm.Human.Resource.Management.service.AllowanceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AllowanceServiceImpl implements AllowanceService {
    @Autowired
    private AllowanceRepositories allowanceRepositories;

    @Override
    public Optional<Allowance> findById(Long id) {
        return allowanceRepositories.findById(id);
    }

    @Override
    public List<Allowance> getAllowance() {return allowanceRepositories.findAll();}

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
    public ResponseEntity<AllowanceResponse> hardDeleteAllowance(Long id){
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

}
