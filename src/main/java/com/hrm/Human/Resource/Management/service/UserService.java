package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.entity.Authority;
import com.hrm.Human.Resource.Management.entity.Role;
import com.hrm.Human.Resource.Management.entity.User;
import com.hrm.Human.Resource.Management.repositories.AuthorityRepositories;
import com.hrm.Human.Resource.Management.repositories.RoleRepositories;
import com.hrm.Human.Resource.Management.repositories.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepositories userRepositories;

    @Autowired
    private RoleRepositories roleRepositories;

    @Autowired
    private AuthorityRepositories authorityRepositories;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        return userRepositories.save(user);
    }

    public Role findRoleByName(String name) {
        return roleRepositories.findByName(name);
    }

    public Authority findAuthorityByName(String name) {
        return authorityRepositories.findByName(name);
    }

    public Authority getOrCreateAuthority(String name) {
        Authority authority = authorityRepositories.findByName(name);
        if (authority == null) {
            authority = new Authority(name);
            authorityRepositories.save(authority);
        }
        return authority;
    }

    public void addAuthority(User user, String authorityName) {
        Authority authority = findAuthorityByName(authorityName);
        if (authority != null) {
            user.getAuthorities().add(authority);
        }
    }
}

