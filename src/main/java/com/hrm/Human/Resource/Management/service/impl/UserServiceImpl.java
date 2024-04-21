package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.dto.UserEmployeeDTO;
import com.hrm.Human.Resource.Management.dto.UserRegistrationDTO;
import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.entity.Permission;
import com.hrm.Human.Resource.Management.entity.Role;
import com.hrm.Human.Resource.Management.entity.User;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.repositories.PermissionRepositories;
import com.hrm.Human.Resource.Management.repositories.RoleRepositories;
import com.hrm.Human.Resource.Management.repositories.UserRepositories;
import com.hrm.Human.Resource.Management.response.ResourceNotFoundException;
import com.hrm.Human.Resource.Management.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private EmployeeRepositories employeeRepositories;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public List<UserEmployeeDTO> getAllUsers() {
        List<User> users = userRepositories.findAll();
        return users.stream().map(this::convertToUserEmployeeDTO).collect(Collectors.toList());
    }

    private UserEmployeeDTO convertToUserEmployeeDTO(User user) {
        UserEmployeeDTO dto = new UserEmployeeDTO();
        Employee employee = employeeRepositories.findByEmployeeCode(user.getUsername());
        if (employee != null) {
            dto.setImage(employee.getImage());
            dto.setFullName(employee.getFullName());
            dto.setPositionName(employee.getPositionName());
        }
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getRole().getName());
        dto.setUsername(user.getUsername());
        return dto;
    }

    @Override
    public User createUser(UserRegistrationDTO userRegistration) {
        if (userRepositories.existsByUsername(userRegistration.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tài khoản đã tồn tại");
        }

        User user = new User();
        user.setUsername(userRegistration.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistration.getPassword()));
        user.setEmail(userRegistration.getEmail());

        Role role = roleRepositories.findById(userRegistration.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);

        Set<Permission> permissions = userRegistration.getPermissionIds().stream()
                .map(id -> permissionRepositories.findById(id)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền này")))
                .collect(Collectors.toSet());
        user.setPermissions(permissions);

        return userRepositories.save(user);
    }

    @Override
    public Set<Map<String, String>> getUserPermissions(Long id) {
        User user = userRepositories.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Permission> permissions = user.getPermissions();

        return permissions.stream()
                .map(permission -> {
                    Map<String, String> permissionMap = new HashMap<>();
                    String[] parts = permission.getName().split("_", 2);
                    permissionMap.put("permission", parts[0]);
                    permissionMap.put("module", parts[1]);
                    return permissionMap;
                })
                .collect(Collectors.toSet());
    }


    @Override
    public User changePermissions(Long userId, Set<Long> newPermissionIds) {
        User user = userRepositories.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Set<Permission> permissions = newPermissionIds.stream()
                .map(id -> permissionRepositories.findById(id).orElseThrow(() -> new RuntimeException("Permission not found")))
                .collect(Collectors.toSet());
        user.setPermissions(permissions);
        return userRepositories.save(user);
    }

    @Override
    public String changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepositories.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepositories.save(user);
        return "Mật khẩu được thay đổi thành công";
    }

    public String generateRandomPassword() {
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = true;
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

    @Override
    public String resetPassword(String userName) {
        User user = userRepositories.findByUsername(userName);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng không tồn tại");
        }
        String newPassword = generateRandomPassword(); // Hàm này tạo ra một mật khẩu ngẫu nhiên
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepositories.save(user);
        emailService.sendNewPasswordEmail(user.getEmail(), newPassword);
        return "Temporary password has been created and sent to the admin.";
    }

    @Override
    public String createTemporaryPasswordForAdmin(Long userId, String adminEmail) {
        User user = userRepositories.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        String tempPassword = generateRandomPassword(); // Hàm này tạo ra một mật khẩu ngẫu nhiên
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepositories.save(user);
        emailService.sendNewPasswordEmail(adminEmail, tempPassword); // Gửi mật khẩu mới đến email của quản trị viên
        return "Temporary password has been created and sent to the admin.";
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