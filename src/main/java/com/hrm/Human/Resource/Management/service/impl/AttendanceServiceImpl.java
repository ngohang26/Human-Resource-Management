package com.hrm.Human.Resource.Management.service.impl;

import com.github.javafaker.Faker;
import com.hrm.Human.Resource.Management.dto.WorkTimeDTO;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.Attendance;
import com.hrm.Human.Resource.Management.repositories.AttendanceRepositories;
import com.hrm.Human.Resource.Management.service.EmployeeService;
import com.hrm.Human.Resource.Management.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service

public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AttendanceRepositories workTimeRepositories;

    public Attendance createWorkTime() {
        Faker faker = Faker.instance();

        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LocalDate date = faker.date().between(start, end).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalTime timeIn = LocalTime.of(
                faker.number().numberBetween(7, 8), // Giờ ngẫu nhiên từ 8 đến 17
                0
        ); // Giữ phút bằng 0

        LocalTime timeOut = LocalTime.of(
                faker.number().numberBetween(17, 22), // Giờ ngẫu nhiên từ 12 đến 18
                0
        ); // Giữ phút bằng 0

        List<Employee> employees = employeeService.getEmployeeEntities();
        Employee employee = employees.get(faker.random().nextInt(employees.size()));

        return new Attendance(date, timeIn, timeOut, employee);
    }

    @Override
    public List<WorkTimeDTO> createWorkTimes() {
        List<WorkTimeDTO> workTimes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Attendance workTime = createWorkTime();
            workTimeRepositories.save(workTime);

            WorkTimeDTO dto = new WorkTimeDTO();
            dto.setDate(workTime.getDate());
            dto.setTimeIn(workTime.getTimeIn());
            dto.setTimeOut(workTime.getTimeOut());
//            dto.setFullName(workTime.getEmployee().getFullName());
//            dto.setEmployeeCode(workTime.getEmployee().getEmployeeCode());

            workTimes.add(dto);
        }
        return workTimes;
    }


    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        createWorkTimes();
    }
}

