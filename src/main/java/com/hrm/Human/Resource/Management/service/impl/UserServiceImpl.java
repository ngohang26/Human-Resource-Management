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
import com.hrm.Human.Resource.Management.response.CustomException;
import com.hrm.Human.Resource.Management.response.ResourceNotFoundException;
import com.hrm.Human.Resource.Management.service.UserService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
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
            dto.setPosition(employee.getPosition());
        }
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getRole().getName());
        dto.setUsername(user.getUsername());
        return dto;
    }

    @Override
    public UserEmployeeDTO getUserById(Long id) {
        User user = userRepositories.findById(id).orElse(null);
        if (user != null) {
            return convertToUserEmployeeDTO(user);
        }
        return null;
    }


    // Khởi tạo các quyền mặc định cho các vai trò khi khởi động ứng dụng
    @PostConstruct
    public void init() {
        roleRepositories.findAll().forEach(this::setDefaultPermissionsForRole);
    }


    public void setDefaultPermissionsForRole(Role role) {
        Set<Permission> defaultPermissions = new HashSet<>();
        switch (role.getName()) {
            case "SUPER":
                defaultPermissions.addAll(permissionRepositories.findAll());
                break;
            case "ACCOUNTANT":
                defaultPermissions.add(permissionRepositories.findByName("VIEW_EMPLOYEE"));
                defaultPermissions.add(permissionRepositories.findByName("VIEW_CONTRACT"));
                defaultPermissions.add(permissionRepositories.findByName("VIEW_SALARY"));
                defaultPermissions.add(permissionRepositories.findByName("ADD_SALARY"));
                defaultPermissions.add(permissionRepositories.findByName("VIEW_ATTENDANCE"));

                break;
            case "EMPLOYEE":
                defaultPermissions.add(permissionRepositories.findByName("VIEW_EMPLOYEE"));
                defaultPermissions.add(permissionRepositories.findByName("VIEW_SALARY"));
                defaultPermissions.add(permissionRepositories.findByName("VIEW_ATTENDANCE"));
                break;
        }
        role.setPermissions(defaultPermissions);
        roleRepositories.save(role);
    }

    private void validatePasswordComplexity(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!\"#$%&'()*+,-./:;<=>?@^_`{|}~])(?=\\S+$).{8,20}$";
        if (!password.matches(passwordPattern)) {
            throw new RuntimeException("Mật khẩu phải có độ dài từ 8 đến 20 ký tự, chứa ít nhất một ký tự số, một ký tự chữ và một ký tự đặc biệt.");
        }
    }


    @Override
    public User createUser(UserRegistrationDTO userRegistration) {
        if (userRegistration.getUsername().trim().isEmpty()) {
            throw new CustomException("Tên người dùng không được để trống");
        }
        if (userRegistration.getEmail().trim().isEmpty()) {
            throw new CustomException("Email không được để trống");
        }
        if (userRegistration.getRoleId() == null) {
            throw new CustomException("Vai trò không được để trống");
        }
        try {
            validatePasswordComplexity(userRegistration.getPassword());
        } catch (RuntimeException e) {
            throw new CustomException(e.getMessage());
        }

        if (userRepositories.existsByUsername(userRegistration.getUsername())) {
            throw new CustomException("Tài khoản đã tồn tại");
        }

        User user = new User();
        user.setUsername(userRegistration.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistration.getPassword()));
        user.setEmail(userRegistration.getEmail());

        Role role = roleRepositories.findById(userRegistration.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);

        Set<Permission> rolePermissions = new HashSet<>(role.getPermissions());

        Set<Permission> additionalPermissions = userRegistration.getPermissionIds().stream()
                .map(id -> permissionRepositories.findById(id)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền này")))
                .collect(Collectors.toSet());

        rolePermissions.addAll(additionalPermissions);

        user.setPermissions(rolePermissions);

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
    public Set<Map<String, String>> getRolePermission(Long id) {
        Role role = roleRepositories.findById(id)
                .orElseThrow(() -> new RuntimeException("Role khong tim thya"));
        Set<Permission> permissions = role.getPermissions();
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
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu mới không được giống mật khẩu cũ");
        }
        try {
            validatePasswordComplexity(newPassword);
        } catch (RuntimeException e) {
            throw new CustomException(e.getMessage());
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
        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepositories.save(user);
        emailService.sendNewPasswordEmail(user.getEmail(), newPassword);
        return "Mật khẩu tạm thời đã được tạo và gửi tới email của bạn";
    }

    @Override
    public String createTemporaryPasswordForAdmin(Long userId, String adminEmail) {
        User user = userRepositories.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        String tempPassword = generateRandomPassword(); // Hàm này tạo ra một mật khẩu ngẫu nhiên
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepositories.save(user);
        emailService.sendNewPasswordEmail(adminEmail, tempPassword); // Gửi mật khẩu mới đến email của quản trị viên
        return "Mật khẩu tạm thời đã được tạo và gửi tới email đã nhập.";
    }

    @Override
    public User findById(Long id) {
        return userRepositories.findById(id).orElse(null);
    }

    @Override
    public void changeEmail(Long id, String newEmail) {
        User user = userRepositories.findById(id).orElseThrow(() -> new UsernameNotFoundException("User Not Found with -> userId : " + id));
        if (EmailValidator.getInstance().isValid(newEmail)) {
            if (user.getEmail().equals(newEmail)) {
                throw new IllegalArgumentException("Email mới phải khác email hiện tại");
            }
            user.setEmail(newEmail);
            userRepositories.save(user);
        } else {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
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