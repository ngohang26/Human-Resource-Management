//package com.hrm.Human.Resource.Management.service.impl;
//
//import com.hrm.Human.Resource.Management.entity.EmployeeAllowance;
//import com.hrm.Human.Resource.Management.repositories.EmployeeAllowanceRepositories;
//import com.hrm.Human.Resource.Management.service.EmployeeAllowanceService;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//public class EmployeeAllowanceServiceImpl implements EmployeeAllowanceService {
//    @Autowired
//    private EmployeeAllowanceRepositories employeeAllowanceRepositories;
//
//    public BigDecimal calculateTotalAllowance(Long employeeId) {
//        List<EmployeeAllowance> employeeAllowances = employeeAllowanceRepositories.findByEmployeeId(employeeId);
//        BigDecimal totalAllowance = BigDecimal.ZERO;
//        for (EmployeeAllowance allowance : employeeAllowances) {
//            totalAllowance = totalAllowance.add(allowance.getAllowance().getAllowanceAmount());
//        }
//        return totalAllowance;
//    }
//}
