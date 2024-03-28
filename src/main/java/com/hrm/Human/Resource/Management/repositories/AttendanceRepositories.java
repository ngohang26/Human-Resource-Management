package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.Attendance;
import com.hrm.Human.Resource.Management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface AttendanceRepositories extends JpaRepository<Attendance, Long> {
//    Attendance findByEmployeeAndDate(Employee employee, LocalDate date);

    List<Attendance> findByEmployee(Employee employee);

    List<Attendance> findByEmployee(Optional<Employee> employee);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByEmployeeId(Long employeeId);

    List<Attendance> findByEmployeeAndDateBetween(Employee employee, LocalDate localDate, LocalDate localDate1);
}
