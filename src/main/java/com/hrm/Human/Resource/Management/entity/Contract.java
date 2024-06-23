package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "contracts")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String contractCode;

    @NotNull(message = "Bạn cần nhập ngày bắt đầu")
    @Future(message = "Ngày bắt đầu phải là ngày trong tương lai")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Bạn cần nhập ngày kết thúc")
    @Future(message = "Ngày kết thúc phải là ngày trong tương lai")
    @Column(nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Bạn cần nhập ngày ký")
    @Column(nullable = false)
    private LocalDate signDate;

    @Column
    private String noteContract;

    @Min(value = 1, message = "Số lượng lần ký phải ít nhất là 1")
    @Column(nullable = false)
    private int numberOfSignatures;

    @NotNull(message = "Bạn cần nhập mức lương cơ bản")
    @DecimalMin(value = "0.0", inclusive = false, message = "Lương tháng phải lớn hơn 0")
    @Column(nullable = false)
    private BigDecimal monthlySalary;

    public enum ContractStatus {
        ACTIVE, EXPIRED, CANCELLED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus contractStatus;

    public Contract() {
        this.contractStatus = ContractStatus.ACTIVE;
        this.numberOfSignatures = 1;
    }

    @PostPersist
    public void generateContractCode() {
        LocalDate date = LocalDate.now();
        int year = date.getYear() % 100;
        int month = date.getMonthValue();
        this.contractCode = String.format("%03d/%02d/%02d" + "-HĐLĐ", id, month, year);
    }

    public void setEmployee(Employee employee) {
        // Implementation goes here
    }

    public void checkContractStatus() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(this.endDate)) {
            this.contractStatus = ContractStatus.EXPIRED;
        }
    }
}
