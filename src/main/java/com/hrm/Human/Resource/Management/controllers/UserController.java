package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.dto.*;
import com.hrm.Human.Resource.Management.entity.Permission;
import com.hrm.Human.Resource.Management.entity.Role;
import com.hrm.Human.Resource.Management.entity.User;
import com.hrm.Human.Resource.Management.jwt.JwtAuthenticationResponse;
import com.hrm.Human.Resource.Management.jwt.JwtTokenProvider;
import com.hrm.Human.Resource.Management.repositories.RoleRepositories;
import com.hrm.Human.Resource.Management.repositories.UserRepositories;
import com.hrm.Human.Resource.Management.response.ErrorFieldResponse;
import com.hrm.Human.Resource.Management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepositories userRepositories;

    @Autowired
    private RoleRepositories roleRepositories;

    @PreAuthorize("hasAuthority('VIEW_USER')")
    @GetMapping("/all")
    public List<UserEmployeeDTO> getAllUsers() {
        return userService.getAllUsers();
    }


    @PostMapping("/register")
    public User createUser(@RequestBody UserRegistrationDTO userRegistration) {
        return userService.createUser(userRegistration);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        User user = userRepositories.findByUsername(loginRequest.getUsername());
        if (user == null) {
            return new ResponseEntity<>(
                    new ErrorFieldResponse("401 UNAUTHORIZED", "username", "Tài khoản không tồn tại"),
                    HttpStatus.UNAUTHORIZED);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(
                    new ErrorFieldResponse("401 UNAUTHORIZED", "password", "Sai mật khẩu"),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @PreAuthorize("hasAuthority('VIEW_USER')")
    @GetMapping("/{id}/permissions")
    public Set<Map<String, String>> getUserPermissions(@PathVariable Long id) {
        return userService.getUserPermissions(id);
    }

    @PreAuthorize("hasAuthority('EDIT_USER')")
    @PutMapping("/{id}/change-permissions")
    public User changePermissions(@PathVariable Long id, @RequestBody Set<Long> newPermissionIds) {
        return userService.changePermissions(id, newPermissionIds);
    }

    @PreAuthorize("hasAuthority('EDIT_USER')")
    @PutMapping("/{id}/change-email")
    public ResponseEntity<?> changeEmail(@PathVariable Long id, @RequestBody EmailChangeRequest emailChangeRequest) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>(new ErrorFieldResponse("404 NOT FOUND", "userId", "Người dùng không tồn tại"), HttpStatus.NOT_FOUND);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            emailChangeRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            userService.changeEmail(id, emailChangeRequest.getNewEmail());
            return ResponseEntity.ok("Email đã được thay đổi thành công");
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorFieldResponse("401 UNAUTHORIZED", "password", "Sai mật khẩu"), HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ErrorFieldResponse("400 BAD REQUEST", "email", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    // pb
    @PutMapping("/change-password/{id}")
    public String changePassword(@PathVariable Long id, @RequestBody PasswordChangeRequest request) {
        return userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
    }

    // quen mat khau --> gui email
    // public
    @PutMapping("/reset-password/{username}")
    public String resetPassword(@PathVariable String username) {
        return userService.resetPassword(username);
    }

    @PreAuthorize("hasAuthority('EDIT_USER')")
    @PutMapping("/{id}/reset-password-for-admin")
    public ResponseEntity<?> resetPasswordForAdmin(@PathVariable Long id, @RequestBody String adminEmail) {
        String message = userService.createTemporaryPasswordForAdmin(id, adminEmail);
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("hasAuthority('VIEW_USER')")
    @GetMapping("/roles/{id}/permissions")
    public Set<Permission> getRolePermissions(@PathVariable Long id) {
        Role role = roleRepositories.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return role.getPermissions();
    }

    @PreAuthorize("hasAuthority('VIEW_USER')")
    @GetMapping("/role/{id}/permissions")
    public Set<Map<String, String>> getRolePermission(@PathVariable Long id) {
        return userService.getRolePermission(id);
    }

    @PreAuthorize("hasAuthority('VIEW_USER')")
    @GetMapping("/{id}/role")
    public Role getUserRole(@PathVariable Long id) {
        User user = userRepositories.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng này"));
        return user.getRole();
    }

}


