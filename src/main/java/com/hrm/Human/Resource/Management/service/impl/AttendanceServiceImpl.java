package com.hrm.Human.Resource.Management.service.impl;

import com.github.javafaker.Faker;
import com.hrm.Human.Resource.Management.dto.AttendanceDTO;
import com.hrm.Human.Resource.Management.entity.Contract;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.Attendance;
import com.hrm.Human.Resource.Management.repositories.AttendanceRepositories;
import com.hrm.Human.Resource.Management.repositories.ContractRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.response.ResourceNotFoundException;
import com.hrm.Human.Resource.Management.service.EmployeeService;
import com.hrm.Human.Resource.Management.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AttendanceRepositories attendanceRepositories;

    @Autowired
    private ContractRepositories contractRepositories;

    @Autowired
    private EmployeeRepositories employeeRepositories;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(AttendanceServiceImpl.class);

    public Attendance createWorkTime(Employee employee, LocalDate date) {
        Faker faker = Faker.instance();

        LocalTime timeIn = LocalTime.of(
                faker.number().numberBetween(8, 8), 0
        );

        double overtimeChance = faker.number().randomDouble(2, 0, 1);
        LocalTime timeOut;
        if (overtimeChance < 0.25) {
            timeOut = LocalTime.of(
                    faker.number().numberBetween(17, 22), 0
            );
        } else {
            timeOut = LocalTime.of(17, 0);
        }

        return new Attendance(date, timeIn, timeOut, employee);
    }

    @Override
    public List<Attendance> createWorkTimes(LocalDate date) {
        // Kiểm tra xem ngày hiện tại có phải là Chủ nhật hay không
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            // Nếu là Chủ nhật, không tạo dữ liệu chấm công
            return new ArrayList<>();
        }

        List<Employee> employees = employeeService.getActiveEmployees();
        List<Attendance> attendances = new ArrayList<>();

        // Tính số lượng nhân viên chấm công dựa trên tỷ lệ phần trăm
        int maxEmployees = (int) Math.round(employees.size() * 0.95);

        for (int i = 0; i < Math.min(employees.size(), maxEmployees); i++) {
            Employee employee = employees.get(i);
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

    @Override
    public AttendanceDTO updateAttendance(Long id, AttendanceDTO attendanceDTO) {
        Attendance attendance = attendanceRepositories.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id " + id));
        LocalTime newTimeIn = attendanceDTO.getTimeIn();
        LocalTime newTimeOut = attendanceDTO.getTimeOut();
        if (newTimeOut.isBefore(newTimeIn)) {
            throw new IllegalArgumentException("Không hợp lệ. Thời gian ra không thể trước thời gian vào");
        }
        attendance.setTimeIn(newTimeIn);
        attendance.setTimeOut(newTimeOut);
        attendance.setWorkTime(Duration.between(newTimeIn, newTimeOut).toHours() - 1);
        if (attendance.getWorkTime() > 8) {
            attendance.setOverTime(attendance.getWorkTime() - 8);
        } else {
            attendance.setOverTime(0L);
        }
        Attendance updatedAttendance = attendanceRepositories.save(attendance);
        return convertToDTO(updatedAttendance);
    }

    @Override
    public Map<LocalDate, Map<String, Long>> getTotalWorkAndOvertimeHours(int year, int month) {
        Map<LocalDate, Map<String, Long>> result = new HashMap<>();

        List<Attendance> attendances = attendanceRepositories.findByDateBetween(
                LocalDate.of(year, month, 1),
                LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth())
        );

        for (Attendance attendance : attendances) {
            LocalDate date = attendance.getDate();
            Long workTime = attendance.getWorkTime();
            Long overTime = attendance.getOverTime();
            Long workTimeWithoutOvertime = (workTime > 8) ? workTime - overTime : workTime;

            Map<String, Long> timeMap = result.getOrDefault(date, new HashMap<>());
            timeMap.put("workTime", timeMap.getOrDefault("workTime", 0L) + workTime);
            timeMap.put("overTime", timeMap.getOrDefault("overTime", 0L) + overTime);
            timeMap.put("workTimeWithoutOvertime", timeMap.getOrDefault("workTimeWithoutOvertime", 0L) + workTimeWithoutOvertime);

            result.put(date, timeMap);
        }

        return result;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Chạy mỗi ngày vào lúc 00:00
    public void createDailyWorkTimes() {
        LocalDate today = LocalDate.now();
        List<Attendance> attendancesToday = attendanceRepositories.findByDate(today);
        if (attendancesToday.isEmpty()) {
            createWorkTimes(today);
        }

        List<Contract> contracts = contractRepositories.findAll();
        for (Contract contract : contracts) {
            contract.checkContractStatus();
            contractRepositories.save(contract);
        }
    }
}