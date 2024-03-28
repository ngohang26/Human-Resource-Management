package com.hrm.Human.Resource.Management.service.impl;

import com.github.javafaker.Faker;
import com.hrm.Human.Resource.Management.dto.AttendanceDTO;
import com.hrm.Human.Resource.Management.entity.Contract;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.Attendance;
import com.hrm.Human.Resource.Management.repositories.AttendanceRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.service.EmployeeService;
import com.hrm.Human.Resource.Management.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AttendanceRepositories attendanceRepositories;

    @Autowired
    private EmployeeRepositories employeeRepositories;

//    @Autowired
//    private ContractRepositories contractRepositories;
//
    public Attendance createWorkTime(Employee employee, LocalDate date) {
        Faker faker = Faker.instance();

        LocalTime timeIn = LocalTime.of(
                faker.number().numberBetween(8, 8), // Giờ ngẫu nhiên từ 7 đến 8
                0
        );

        double overtimeChance = faker.number().randomDouble(2, 0, 1);
        LocalTime timeOut;
        if (overtimeChance < 0.2) {
            // 20% cơ hội làm việc trên 8 giờ
            timeOut = LocalTime.of(
                    faker.number().numberBetween(17, 22), // Giờ ngẫu nhiên từ 17 đến 22
                    0
            );
        } else {
            // 80% cơ hội làm việc 8 giờ
            timeOut = LocalTime.of(17, 0);
        }

        return new Attendance(date, timeIn, timeOut, employee);
    }

    @Override
    public List<Attendance> createWorkTimes(LocalDate date) {
        List<Employee> employees = employeeService.getEmployeeEntities();
        List<Attendance> attendances = new ArrayList<>();
        for (Employee employee : employees) {
            Attendance attendance = createWorkTime(employee, date);
            attendances.add(attendance);
            attendanceRepositories.save(attendance);
        }
        return attendances;
    }

    @Override
    public List<AttendanceDTO> getAllAttendances() {
        List<Attendance> attendances = attendanceRepositories.findAll();
        return attendances.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAttendancesByEmployee(Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        List<Attendance> attendances = attendanceRepositories.findByEmployee(employee);
        return attendances.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setDate(attendance.getDate());
        dto.setTimeIn(attendance.getTimeIn());
        dto.setTimeOut(attendance.getTimeOut());
        dto.setEmployeeName(attendance.getEmployee().getFullName());
        dto.setEmployeeCode(attendance.getEmployee().getEmployeeCode());
        dto.setWorkTime(attendance.getWorkTime());
        return dto;
    }

    @Override
    public List<AttendanceDTO> getAttendancesByDate(LocalDate date) {
        List<Attendance> attendances = attendanceRepositories.findByDate(date);
        return attendances.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
//
//    @Override
//    public Long calculateWorkDays(Long employeeId) {
//        List<Attendance> attendances = attendanceRepositories.findByEmployeeId(employeeId);
//        return (long) attendances.size();
//    }
//@Override
//    public Map<Long, BigDecimal> calculateOvertimeSalaryForEachEmployee() {
//        List<Employee> employees = employeeRepositories.findAll();
//        Map<Long, BigDecimal> overtimeSalaries = new HashMap<>();
//
//        for (Employee employee : employees) {
//            BigDecimal monthlySalary = getMonthlySalary(employee);
//            Long totalOvertimeHours = getTotalOvertimeHours(employee);
//
//            BigDecimal overtimeSalary = calculateOvertimeSalary(monthlySalary, totalOvertimeHours);
//            overtimeSalaries.put(employee.getId(), overtimeSalary);
//        }
//
//        return overtimeSalaries;
//    }
//@Override
//public BigDecimal getMonthlySalary(Employee employee) {
//    Contract contract = contractRepositories.findByEmployeeCode(employee.getEmployeeCode());
//    return contract.getMonthlySalary();
//    }
//@Override
//public Long getTotalOvertimeHours(Employee employee) {
//        List<Attendance> attendances = attendanceRepositories.findByEmployee(employee);
//        return attendances.stream()
//                .mapToLong(Attendance::getOverTime)
//                .sum();
//    }
//
//    @Override
//    public BigDecimal calculateOvertimeSalary(BigDecimal monthlySalary, Long totalOvertimeHours) {
//        BigDecimal workDaysPerMonth = BigDecimal.valueOf(26);
//        BigDecimal workHoursPerDay = BigDecimal.valueOf(8);
//
//        BigDecimal hourlyRate = monthlySalary.divide(workDaysPerMonth, 2, RoundingMode.HALF_UP)
//                .divide(workHoursPerDay, 2, RoundingMode.HALF_UP);
//
//        return hourlyRate.multiply(BigDecimal.valueOf(totalOvertimeHours));
//    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LocalDate today = LocalDate.now();
        List<Attendance> attendancesToday = attendanceRepositories.findByDate(today);
        if (attendancesToday.isEmpty()) {
            createWorkTimes(today);
        }
    }
}

