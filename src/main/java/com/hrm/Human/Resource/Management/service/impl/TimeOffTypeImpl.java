//package com.hrm.Human.Resource.Management.service.impl;
//
//import com.hrm.Human.Resource.Management.entity.TimeOffType;
//import com.hrm.Human.Resource.Management.repositories.TimeOffTypeRepositories;
//import com.hrm.Human.Resource.Management.service.TimeOffTypeService;
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//public class TimeOffTypeImpl implements TimeOffTypeService {
//    @Autowired
//    private TimeOffTypeRepositories timeOffTypeRepositories;
//
//    public TimeOffType addTimeOffType(TimeOffType timeOffType) {
//        return timeOffTypeRepositories.save(timeOffType);
//    }
//
//    public TimeOffType updateTimeOffType(Long id, TimeOffType timeOffTypeDetails) {
//        TimeOffType timeOffType = timeOffTypeRepositories.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("TimeOff Type not found with id" + id));
//
//        timeOffType.setTimeOffTypeName(timeOffTypeDetails.getTimeOffTypeName());
//        timeOffType.setApprovalType(timeOffTypeDetails.getApprovalType());
//
//        return timeOffTypeRepositories.save(timeOffType);
//    }
//
//    public void deleteTimeOffType(Long id) {
//        TimeOffType timeOffType = timeOffTypeRepositories.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("TimeOff Type not found with id" + id));
//
//        timeOffTypeRepositories.delete(timeOffType);
//    }
//}
