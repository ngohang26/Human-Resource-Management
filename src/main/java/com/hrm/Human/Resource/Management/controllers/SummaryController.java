package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.dto.Summary;
import com.hrm.Human.Resource.Management.service.impl.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}
