package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.ExperienceName;
import com.hrm.Human.Resource.Management.entity.SkillName;
import com.hrm.Human.Resource.Management.service.impl.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SkillAndExperienceController {

    @Autowired
    private CandidateService candidateService;

    @PreAuthorize("hasAuthority('EDIT_CANDIDATE')")
    @GetMapping("/skillNames")
    public List<SkillName> getAllSkillNames() {
        return candidateService.getAllSkillNames();
    }

    @PreAuthorize("hasAuthority('EDIT_CANDIDATE')")
    @GetMapping("/experienceNames")
    public List<ExperienceName> getAllExperienceNames() {
        return candidateService.getAllExperienceNames();
    }
}
