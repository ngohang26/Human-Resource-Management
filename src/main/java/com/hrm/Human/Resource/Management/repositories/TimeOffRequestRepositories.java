//package com.hrm.Human.Resource.Management.repositories;
//
//import com.hrm.Human.Resource.Management.entity.Department;
//import com.hrm.Human.Resource.Management.entity.TimeOffRequest;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@EnableJpaRepositories
//@Repository
//public interface TimeOffRequestRepositories extends JpaRepository<TimeOffRequest, Long> {
//    List<TimeOffRequest> findByEmployeeCodeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Object employeeCode, LocalDate startDate, LocalDate endDate);
//}
