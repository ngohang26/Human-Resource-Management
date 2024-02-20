//package com.hrm.Human.Resource.Management.entity;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "users")
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column
//    private String firstName;
//
//    @Column
//    private String lastName;
//
//    @Column
//    private String phoneNumber;
//
//    @Column
//    private String email;
//
//    @ManyToOne
//    @JoinColumn(name = "department_id")
//    private Department department;
//
//
//    @ManyToOne
//    @JoinColumn(name = "position_id")
//    private Position position;
//
//    @Column
//    private Employee manager;
//}
