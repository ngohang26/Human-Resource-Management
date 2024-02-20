package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.TimeOffRequest;
import com.hrm.Human.Resource.Management.repositories.TimeOffRequestRepositories;
import com.hrm.Human.Resource.Management.service.TimeOffRequestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TimeOffRequestServiceImpl implements TimeOffRequestService {
    @Autowired
    private TimeOffRequestRepositories timeOffRequestRepositories;

    @Override
    public TimeOffRequest addTimeOffRequest(TimeOffRequest timeOffRequest) {
        List<TimeOffRequest> existingRequests = timeOffRequestRepositories.findByEmployeeCodeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                timeOffRequest.getEmployeeCode(), timeOffRequest.getStartDate(), timeOffRequest.getEndDate());

        if (!existingRequests.isEmpty()) {
            throw new RuntimeException("There is already a time off request for the given date range.");
        }

        return timeOffRequestRepositories.save(timeOffRequest);
    }

    @Override
    public TimeOffRequest updateTimeOffRequest(Long id, TimeOffRequest timeOffDetails) {
        TimeOffRequest timeOff = timeOffRequestRepositories.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TimeOff request not found with id" + id));

        timeOff.setEmployeeCode(timeOffDetails.getEmployeeCode());
        timeOff.setTimeOffTypeId(timeOffDetails.getTimeOffTypeId());
        timeOff.setStartDate(timeOffDetails.getStartDate());
        timeOff.setEndDate(timeOffDetails.getEndDate());
        timeOff.setStatus(timeOffDetails.getStatus());

        return timeOffRequestRepositories.save(timeOff);
    }

    @Override
    public void deleteTimeOffRequest(Long id) {
        TimeOffRequest timeOff = timeOffRequestRepositories.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TimeOff request not found with id" + id));

        timeOffRequestRepositories.delete(timeOff);
    }

    @Override
    public Map<Long, Long> getCountByType() {
        List<TimeOffRequest> timeOffRequests = timeOffRequestRepositories.findAll();
        return timeOffRequests.stream().collect(Collectors.groupingBy(TimeOffRequest::getTimeOffTypeId, Collectors.counting()));
    }

    @Override
    public Map<String, Long> getCountByStatus() {
        List<TimeOffRequest> timeOffRequests = timeOffRequestRepositories.findAll();
        return timeOffRequests.stream().collect(Collectors.groupingBy(t -> t.getStatus().name(), Collectors.counting()));
    }

    @Override
    public Long getCountByDateRange(LocalDate startDate, LocalDate endDate) {
        List<TimeOffRequest> timeOffRequests = timeOffRequestRepositories.findAll();
        return timeOffRequests.stream().filter(t -> (t.getStartDate().isEqual(startDate)
                || t.getStartDate().isAfter(startDate)) && (t.getEndDate().isEqual(endDate) || t.getEndDate().isBefore(endDate))).count();
    }
}













