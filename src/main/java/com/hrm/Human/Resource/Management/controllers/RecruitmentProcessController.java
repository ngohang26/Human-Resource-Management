package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.RecruitmentProcess;
import com.hrm.Human.Resource.Management.service.impl.RecruitmentProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
public class RecruitmentProcessController {
        @Autowired
        private RecruitmentProcessService recruitmentProcessService;

        @PostMapping("/recruitment")
        public RecruitmentProcess createRecruitmentProcess() {
            return recruitmentProcessService.createRecruitmentProcess();
        }

        @PutMapping("/recruitment/{recruitmentProcessId}")
        public RecruitmentProcess updateRecruitmentProcessStatus(@PathVariable Long recruitmentProcessId, @RequestBody String status) {
            return recruitmentProcessService.updateRecruitmentProcessStatus(recruitmentProcessId, status);
        }

        @PostMapping("/recruitment/{recruitmentProcessId}/notify")
        public void notifyCandidate(@PathVariable Long recruitmentProcessId, @RequestBody String status) {
            recruitmentProcessService.notifyCandidate(recruitmentProcessId, status);
        }

        @PostMapping("/recruitment/{processId}/advance")
        public RecruitmentProcess advanceProcess(@PathVariable Long processId) {
            return recruitmentProcessService.advanceProcess(processId);
        }
}
