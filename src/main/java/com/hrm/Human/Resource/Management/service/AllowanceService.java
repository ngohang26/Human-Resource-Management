package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.JobPosition;
import com.hrm.Human.Resource.Management.response.AllowanceResponse;
import com.hrm.Human.Resource.Management.response.JobPositionResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface AllowanceService {
    List<Allowance> getAllowance();

    Optional<Allowance> findById(Long id);

    ResponseEntity<?> addAllowance(Allowance allowance);

    Allowance updateAllowance(Long id, Allowance allowanceDetails);


    ResponseEntity<AllowanceResponse> hardDeleteAllowance(Long id);
}
