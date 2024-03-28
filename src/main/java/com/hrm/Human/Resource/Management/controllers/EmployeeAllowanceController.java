//package com.hrm.Human.Resource.Management.controllers;
//
//import com.hrm.Human.Resource.Management.entity.Allowance;
//import com.hrm.Human.Resource.Management.entity.Employee;
//import com.hrm.Human.Resource.Management.service.EmployeeAllowanceService;
//import com.hrm.Human.Resource.Management.service.EmployeeService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/employee_allowances")
//public class EmployeeAllowanceController {
//    @Autowired
//    private EmployeeService employeeService;
//
//    @Autowired
//    private EmployeeAllowanceService employeeAllowanceService;
//    @PostMapping("/{id}/allowances")
//    public ResponseEntity<?> addAllowanceToEmployee(@PathVariable Long id, @RequestBody Allowance allowance) {
//        Employee employee = employeeService.addAllowance(id, allowance);
//        return ResponseEntity.ok(employee);
//    }
//
//}
