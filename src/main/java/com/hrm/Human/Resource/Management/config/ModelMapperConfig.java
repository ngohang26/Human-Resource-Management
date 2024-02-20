//package com.hrm.Human.Resource.Management.config;
//
//import com.hrm.Human.Resource.Management.dto.EmployeeDTO;
//import com.hrm.Human.Resource.Management.dto.PersonalInfoDTO;
//import com.hrm.Human.Resource.Management.entity.Employee;
//import com.hrm.Human.Resource.Management.entity.PersonalInfo;
//import org.modelmapper.ModelMapper;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class ModelMapperConfig {
//    @Bean
//    public ModelMapper modelMapper() {
//        ModelMapper modelMapper = new ModelMapper();
//
//        // Tạo TypeMap cho PersonalInfo
//        modelMapper.createTypeMap(PersonalInfoDTO.class, PersonalInfo.class);
//
//        // Tạo TypeMap cho Employee
//        modelMapper.createTypeMap(EmployeeDTO.class, Employee.class)
//                .addMappings(mapper -> {
//                    mapper.map(EmployeeDTO::getPersonalInfo, Employee::setPersonalInfo);
//                    // Thêm các ánh xạ khác nếu cần
//                });
//
//        return modelMapper;
//    }
//    }
