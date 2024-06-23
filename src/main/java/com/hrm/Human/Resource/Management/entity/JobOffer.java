package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.Temporal;
@Getter
@Setter
@Entity
@Table(name = "contract_proposal")
public class JobOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Bạn cần nhập ngày bắt đầu")
    @Future(message = "Ngày bắt đầu phải là ngày trong tương lai")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Bạn cần nhập ngày kết thúc")
    @Future(message = "Ngày kết thúc phải là ngày trong tương lai")
    @Column(nullable = false)
    private LocalDate endDate;

    @Column
    private String noteContract;

    @NotNull(message = "Bạn cần nhập mức lương cơ bản")
    @DecimalMin(value = "0.0", inclusive = false, message = "Lương tháng phải lớn hơn 0")
    @Column(nullable = false)
    private BigDecimal monthlySalary;
}
