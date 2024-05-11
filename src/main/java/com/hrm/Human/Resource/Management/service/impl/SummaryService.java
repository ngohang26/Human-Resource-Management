package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.dto.Summary;
import com.hrm.Human.Resource.Management.repositories.CandidateRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.repositories.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SummaryService {

    @Autowired
    private CandidateRepositories candidateRepositories;

    @Autowired
    private EmployeeRepositories employeeRepositories;

    @Autowired
    private UserRepositories userRepositories;

    public Summary getSummary() {
        long candidateCount = candidateRepositories.count();
        long employeeCount = employeeRepositories.count();
        long userCount = userRepositories.count();

        return new Summary(candidateCount, employeeCount, userCount);
    }
}

