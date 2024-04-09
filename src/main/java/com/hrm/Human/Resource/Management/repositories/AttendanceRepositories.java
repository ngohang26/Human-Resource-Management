package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.Attendance;
import com.hrm.Human.Resource.Management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface AttendanceRepositories extends JpaRepository<Attendance, Long> {
//    Attendance findByEmployeeAndDate(Employee employee, LocalDate date);

    List<Attendance> findByEmployee(Employee employee);


    List<Attendance> findByDate(LocalDate date);


    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);



    @Query("SELECT a FROM Attendance a WHERE a.employee = :employee AND MONTH(a.date) = :month AND YEAR(a.date) = :year")
    List<Attendance> findByEmployeeAndMonthAndYear(@Param("employee") Employee employee, @Param("month") int month, @Param("year") int year);

    @Query("SELECT a FROM Attendance a WHERE FUNCTION('YEAR', a.date) = :year AND FUNCTION('MONTH', a.date) = :month")
    List<Attendance> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    List<Attendance> findByEmployee_EmployeeCodeAndDateBetween(String employeeCode, LocalDate startDate, LocalDate endDate);
}
