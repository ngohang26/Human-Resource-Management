package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
import com.hrm.Human.Resource.Management.response.AllowanceResponse;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AllowanceService {

    List<EmployeeAllowance> findByEmployeeCode(String employeeCode);

    List<Allowance> getAllowance();

    ResponseEntity<?> addAllowance(Allowance allowance);

    Allowance updateAllowance(Long id, Allowance updatedAllowance);

    ResponseEntity<AllowanceResponse> hardDeleteAllowance(Long id);

    List<Allowance> getAllowancesForEmployee(String employeeCode);

    BigDecimal getTotalAllowance(String employeeCode);
}
