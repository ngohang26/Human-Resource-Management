package com.hrm.Human.Resource.Management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Set;

@Entity
@Setter
@Getter
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    @JsonManagedReference
    private Module module;

    @ManyToMany(mappedBy = "permissions")
    @JsonBackReference
    private Set<Role> roles;
}