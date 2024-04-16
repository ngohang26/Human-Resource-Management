package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.dto.UserRegistrationDTO;
import com.hrm.Human.Resource.Management.entity.Permission;
import com.hrm.Human.Resource.Management.entity.Role;
import com.hrm.Human.Resource.Management.entity.User;
import com.hrm.Human.Resource.Management.repositories.PermissionRepositories;
import com.hrm.Human.Resource.Management.repositories.RoleRepositories;
import com.hrm.Human.Resource.Management.repositories.UserRepositories;
import com.hrm.Human.Resource.Management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepositories userRepositories;

    @Autowired
    private RoleRepositories roleRepositories;

    @Autowired
    private PermissionRepositories permissionRepositories;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserRegistrationDTO userRegistration) {
        User user = new User();
        user.setUsername(userRegistration.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistration.getPassword()));
        Role role = roleRepositories.findById(userRegistration.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);

        Set<Permission> permissions = userRegistration.getPermissionIds().stream()
                .map(id -> permissionRepositories.findById(id).orElseThrow(() -> new RuntimeException("Permission not found")))
                .collect(Collectors.toSet());
        user.setPermissions(permissions);

        return userRepositories.save(user);
    }
//    @Override
//    public User assignRole(Long userId, Long roleId) {
//        User user = userRepositories.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//        Role role = roleRepositories.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
//        user.setRole(role);
//        return userRepositories.save(user);
//    }
//
//    @Override
//    public User assignPermissions(Long userId, Set<Long> permissionIds) {
//        User user = userRepositories.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//        Set<Permission> permissions = permissionIds.stream()
//                .map(id -> permissionRepositories.findById(id).orElseThrow(() -> new RuntimeException("Permission not found")))
//                .collect(Collectors.toSet());
//        user.setPermissions(permissions);
//        return userRepositories.save(user);
//    }
}