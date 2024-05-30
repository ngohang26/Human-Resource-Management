package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.dto.ReportData;
import com.hrm.Human.Resource.Management.dto.Summary;
import com.hrm.Human.Resource.Management.entity.MonthlyReport;
import com.hrm.Human.Resource.Management.service.impl.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/summary")
public class SummaryController {
    @Autowired
    private SummaryService summaryService;

    @GetMapping("/count")
    public Summary getSummary() {
        return summaryService.getSummary();
    }

    @PreAuthorize("hasAuthority('VIEW_EMPLOYEE')")
    @GetMapping("/{year}/{month}")
    public ResponseEntity<ReportData> getReport(@PathVariable int year, @PathVariable int month) {
        ReportData report = summaryService.generateReport(month, year);
        return ResponseEntity.ok(report);
    }

}
