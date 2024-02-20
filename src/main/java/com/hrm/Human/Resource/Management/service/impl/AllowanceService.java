package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.repositories.AllowanceRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AllowanceService {
    @Autowired
    private AllowanceRepositories allowanceRepositories;

    public Allowance saveAllowance(Allowance allowance) {
        return allowanceRepositories.save(allowance);
    }

    // Thêm các phương thức khác như findById, delete, getAll, v.v.
}
