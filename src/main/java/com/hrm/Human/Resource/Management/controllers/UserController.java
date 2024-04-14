package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.dto.LoginRequest;
import com.hrm.Human.Resource.Management.dto.UserRegistrationDTO;
import com.hrm.Human.Resource.Management.entity.Role;
import com.hrm.Human.Resource.Management.entity.User;
import com.hrm.Human.Resource.Management.jwt.JwtAuthenticationResponse;
import com.hrm.Human.Resource.Management.jwt.JwtTokenProvider;
import com.hrm.Human.Resource.Management.repositories.RoleRepositories;
import com.hrm.Human.Resource.Management.repositories.UserRepositories;
import com.hrm.Human.Resource.Management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private RoleRepositories roleRepositories;

    @Autowired
    private UserRepositories userRepositories;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")

    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }


    @PostMapping("/register")
    @Transactional
 @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        Role role = userService.findRoleByName(registrationDTO.getRole());
        if (role == null) {
            System.out.println("Role này không tồn tại");
            return ResponseEntity.badRequest().body(null); // Improved error handling
        }
        user.setRole(role);

        // Ensure authorities set is initialized
        if (user.getAuthorities() == null) {
            user.setAuthorities(new HashSet<>());
        }

        // Assign predefined authorities based on the role
        switch (role.getName()) {
            case "EMPLOYEE":
                userService.addAuthority(user, "READ_SELF");
                break;
            case "HR":
                userService.addAuthority(user, "READ_ALL");
                userService.addAuthority(user, "WRITE_EMPLOYEE");
                userService.addAuthority(user, "WRITE_POSITION");
                break;
            case "ADMIN":
                userService.addAuthority(user, "READ_ALL");
                userService.addAuthority(user, "WRITE_ALL");
                userService.addAuthority(user, "CREATE_USER");
                break;
            case "DIRECTOR":
                userService.addAuthority(user, "READ_ALL");
                userService.addAuthority(user, "WRITE_EMPLOYEE");
                break;
            case "ACCOUNTANT":
                userService.addAuthority(user, "READ_SELF");
                userService.addAuthority(user, "READ_SALARY");
                break;
        }

        userService.saveUser(user); // Using UserService to handle saving
        return ResponseEntity.ok(user);
    }
}