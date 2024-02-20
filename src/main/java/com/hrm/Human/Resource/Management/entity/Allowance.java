package com.hrm.Human.Resource.Management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "allowances")

// tro cap
public class Allowance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String allowanceName;

    @Column
    private double allowanceAmount;

    public double getAllowanceAmount() {
        return allowanceAmount;
    }
}
