//package com.hrm.Human.Resource.Management.controllers;
//
//import com.hrm.Human.Resource.Management.entity.TimeOffRequest;
//import com.hrm.Human.Resource.Management.service.TimeOffRequestService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/time_off_request")
//public class TimeOffRequestController {
//    @Autowired
//    private TimeOffRequestService timeOffRequestService;
//
//    @PostMapping("/addTimeOffRequest")
//    public ResponseEntity<TimeOffRequest> addTimeOffRequest(@RequestBody TimeOffRequest timeOffRequest) {
//        return ResponseEntity.ok(timeOffRequestService.addTimeOffRequest(timeOffRequest));
//    }
//
//    @PutMapping("/update/{id}")
//    public ResponseEntity<TimeOffRequest> updateTimeOffRequest(@PathVariable Long id, @RequestBody TimeOffRequest timeOffRequest) {
//        return ResponseEntity.ok(timeOffRequestService.updateTimeOffRequest(id, timeOffRequest));
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Void> deleteTimeOffRequest(@PathVariable Long id) {
//        timeOffRequestService.deleteTimeOffRequest(id);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/countByType")
//    public Map<Long, Long> getCountByType() {
//        return timeOffRequestService.getCountByType();
//    }
//
//    @GetMapping("/countByStatus")
//    public Map<String, Long> getCountByStatus() {
//        return timeOffRequestService.getCountByStatus();
//    }
//
//    @GetMapping("/countByRangeDate")
//    public Long getCountByDateRange(@RequestParam LocalDate startDate, LocalDate endDate) {
//        return timeOffRequestService.getCountByDateRange(startDate, endDate);
//    }
//}
