package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.dto.UserRegistrationDTO;
import com.hrm.Human.Resource.Management.entity.User;

public interface UserService {

//    @Autowired
//    private UserRepositories userRepositories;
//
//    @Autowired
//    private RoleRepositories roleRepositories;
//
//    @Autowired
//    private AuthorityRepositories authorityRepositories;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;

//    User createUser(User user);
//
//    User assignRole(Long userId, Long roleId);
//
//    User assignPermissions(Long userId, Set<Long> permissionIds);

    User createUser(UserRegistrationDTO userRegistration);

//    public User saveUser(User user) {
//        return userRepositories.save(user);
//    }
//
//    public Role findRoleByName(String name) {
//        return roleRepositories.findByName(name);
//    }


}
















//    public Authority findAuthorityByName(String name) {
//        return authorityRepositories.findByName(name);
//    }
//
//    public Authority getOrCreateAuthority(String name) {
//        Authority authority = authorityRepositories.findByName(name);
//        if (authority == null) {
//            authority = new Authority(name);
//            authorityRepositories.save(authority);
//        }
//        return authority;
//    }
//
//    public void addAuthority(User user, String authorityName) {
//        Authority authority = findAuthorityByName(authorityName);
//        if (authority != null) {
//            user.getAuthorities().add(authority);
//        }
//    }