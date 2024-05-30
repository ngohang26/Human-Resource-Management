package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.EmployeeSalaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@EnableJpaRepositories
@Repository
public interface EmployeeSalaryRecordRepositories extends JpaRepository<EmployeeSalaryRecord, Long> {
    List<EmployeeSalaryRecord> findByYearAndMonth(int year, int month);

    EmployeeSalaryRecord findByEmployeeCodeAndYearAndMonth(String employeeCode, int year, int month);

    @Query("SELECT SUM(esr.netSalary) FROM EmployeeSalaryRecord esr WHERE esr.month = :month AND esr.year = :year")
    BigDecimal sumNetSalariesByMonth(@Param("month") int month, @Param("year") int year);

    List<EmployeeSalaryRecord> findByYearAndMonthAndDepartmentName(int year, int month, String departmentName);
}
