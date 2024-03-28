//package com.hrm.Human.Resource.Management.controllers;
//
//import com.hrm.Human.Resource.Management.entity.TimeOffType;
//import com.hrm.Human.Resource.Management.service.TimeOffTypeService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/time_off")
//public class TimeOffTypeController {
//    @Autowired
//    private TimeOffTypeService timeOffTypeService;
//
//    @PostMapping("/addTimeOff")
//    public ResponseEntity<TimeOffType> addTimeOff(@RequestBody TimeOffType timeOffType) {
//        return ResponseEntity.ok(timeOffTypeService.addTimeOffType(timeOffType));
//    }
//
//    @PutMapping("/update/{id}")
//    public ResponseEntity<TimeOffType> updateTimeOffType(@PathVariable Long id, @RequestBody TimeOffType timeOffType) {
//        return ResponseEntity.ok(timeOffTypeService.updateTimeOffType(id, timeOffType));
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Void> deleteTimeOffType(@PathVariable Long id) {
//        timeOffTypeService.deleteTimeOffType(id);
//        return ResponseEntity.ok().build();
//    }
//
//}
//// su dung ten de tim kiem
////    public TimeOffType updateTimeOffType(String timeOffTypeName, TimeOffType timeOffTypeDetails) {
////        TimeOffType timeOffType = timeOffTypeRepositories.findByTimeOffTypeName(timeOffTypeName)
////                .orElseThrow(() -> new EntityNotFoundException("TimeOff Type not found with name " + timeOffTypeName));
////
////        timeOffType.setTimeOffTypeName(timeOffTypeDetails.getTimeOffTypeName());
////        timeOffType.setApprovalBy(timeOffTypeDetails.getApprovalBy());
////
////        return timeOffTypeRepositories.save(timeOffType);
////    }
////
////    public void deleteTimeOffType(String timeOffTypeName) {
////        TimeOffType timeOffType = timeOffTypeRepositories.findByTimeOffTypeName(timeOffTypeName)
////                .orElseThrow(() -> new EntityNotFoundException("TimeOff Type not found with name " + timeOffTypeName));
////
////        timeOffTypeRepositories.delete(timeOffType);
////    }
