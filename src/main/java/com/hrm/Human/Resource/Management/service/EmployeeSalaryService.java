package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.dto.EmployeeSalaryDTO;
import com.hrm.Human.Resource.Management.entity.Employee;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EmployeeSalaryService {

//    Map<Long, BigDecimal> calculateOvertimeSalaryForEachEmployee();

    // Trong EmployeeSalaryServiceImpl

    // Trong EmployeeSalaryServiceImpl
    Map<Long, BigDecimal> calculateOvertimeSalaryForEachEmployee();
}
