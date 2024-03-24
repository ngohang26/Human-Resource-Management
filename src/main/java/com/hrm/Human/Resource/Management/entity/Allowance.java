package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "allowances")

// tro cap
public class Allowance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String allowanceName;

    @Column
    private BigDecimal allowanceAmount;

//    public BigDecimal getAllowanceAmount() {
//        return allowanceAmount;
//    }
}
