package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.MonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyReportRepositories extends JpaRepository<MonthlyReport, Long> {
    MonthlyReport findByMonthAndYear(int month, int year);
}

