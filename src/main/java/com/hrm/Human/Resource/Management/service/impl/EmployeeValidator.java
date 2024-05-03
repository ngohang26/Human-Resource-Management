//package com.hrm.Human.Resource.Management.service.impl;
//import com.hrm.Human.Resource.Management.entity.Employee;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.Errors;
//import org.springframework.validation.Validator;
//
//@Component
//public class EmployeeValidator implements Validator {
//
//    @Override
//    public boolean supports(Class<?> clazz) {
//        return Employee.class.isAssignableFrom(clazz);
//    }
//
//    @Override
//    public void validate(Object target, Errors errors) {
//        Employee employee = (Employee) target;
//
//        // Kiểm tra ràng buộc của employee ở đây, ví dụ:
//        // Kiểm tra email
//        if (!isValidEmail(employee.getWorkEmail())) {
//            errors.rejectValue("email", "invalid.email", "Email không hợp lệ");
//        }
//
//        // Kiểm tra số điện thoại
//        if (!isValidPhoneNumber(employee.getPhoneNumber())) {
//            errors.rejectValue("phoneNumber", "invalid.phoneNumber", "Số điện thoại không hợp lệ");
//        }
//
//        // Kiểm tra số CCCD
//        if (!isValidIdentityCardNumber(employee.getPersonalInfo().getIdentityCardNumber())) {
//            errors.rejectValue("personalInfo.identityCardNumber", "invalid.identityCardNumber", "Số CCCD không hợp lệ");
//        }
//    }
//
//    // Hàm kiểm tra email hợp lệ
//    private boolean isValidEmail(String email) {
//        // Implement logic kiểm tra email hợp lệ
//        return true;
//    }
//
//    // Hàm kiểm tra số điện thoại hợp lệ
//    private boolean isValidPhoneNumber(String phoneNumber) {
//        // Implement logic kiểm tra số điện thoại hợp lệ
//        return true;
//    }
//
//    // Hàm kiểm tra số CCCD hợp lệ
//    private boolean isValidIdentityCardNumber(String identityCardNumber) {
//        // Implement logic kiểm tra số CCCD hợp lệ
//        return true;
//    }
//}
