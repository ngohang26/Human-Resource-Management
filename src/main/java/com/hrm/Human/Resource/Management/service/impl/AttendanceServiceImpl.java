package com.hrm.Human.Resource.Management.service.impl;

import com.github.javafaker.Faker;
import com.hrm.Human.Resource.Management.dto.AttendanceDTO;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
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

    public Attendance createWorkTime(Employee employee, LocalDate date) {
        Faker faker = Faker.instance();

        LocalTime timeIn = LocalTime.of(
                faker.number().numberBetween(8, 8),0
        );

        double overtimeChance = faker.number().randomDouble(2, 0, 1);
        LocalTime timeOut;
        if (overtimeChance < 0.2) {
            timeOut = LocalTime.of(
                    faker.number().numberBetween(17, 22),0
            );
        } else {
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
    public List<AttendanceDTO> getAttendancesByEmployee(String employeeCode) {
        Employee employee = employeeService.getEmployeeByEmployeeCode(employeeCode);
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

    @Override
    public List<AttendanceDTO> getAttendancesByMonthAndYear(String employeeCode, int month, int year) {
        Employee employee = employeeService.getEmployeeByEmployeeCode(employeeCode);
        List<Attendance> attendances = attendanceRepositories.findByEmployeeAndMonthAndYear(employee, month, year);
        return attendances.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAttendancesByYearAndMonth(int year, int month) {
        List<Attendance> attendances = attendanceRepositories.findByYearAndMonth(year, month);
        return attendances.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> calculateWorkdays(int year, int month) {
        Map<String, Integer> workdaysMap = new HashMap<>();
        List<Attendance> attendances = attendanceRepositories.findByDateBetween(
                LocalDate.of(year, month, 1),
                LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth())
        );

        for (Attendance attendance : attendances) {
            String employeeCode = attendance.getEmployee().getEmployeeCode();
            Integer workdays = attendance.calculateWorkdays(); // Tính số ngày công
            if (workdays != null) {
                workdaysMap.put(employeeCode, workdaysMap.getOrDefault(employeeCode, 0) + workdays);
            }
        }

        return workdaysMap;
    }

    @Override
    public Map<String, Integer> calculateWorkdaysForEachEmployee(String employeeCode, int year, int month) {
        Map<String, Integer> workdaysMap = new HashMap<>();

        List<Attendance> attendances = attendanceRepositories.findByEmployee_EmployeeCodeAndDateBetween(
                employeeCode,
                LocalDate.of(year, month, 1),
                LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth())
        );

        int workdays = calculateWorkdays(attendances);
        workdaysMap.put(employeeCode, workdays);

        return workdaysMap;
    }

    private int calculateWorkdays(List<Attendance> attendances) {
        Set<LocalDate> workdaysSet = new HashSet<>();
        for (Attendance attendance : attendances) {
            workdaysSet.add(attendance.getDate());
        }
        return workdaysSet.size();
    }


    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LocalDate today = LocalDate.now();
        List<Attendance> attendancesToday = attendanceRepositories.findByDate(today);
        if (attendancesToday.isEmpty()) {
            createWorkTimes(today);
        }
    }
}

