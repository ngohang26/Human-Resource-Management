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

    @PreAuthorize("hasAuthority('ADD_USER')")
    @PostMapping("/register")
    public User createUser(@RequestBody UserRegistrationDTO userRegistration) {
        return userService.createUser(userRegistration);
    }
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

}