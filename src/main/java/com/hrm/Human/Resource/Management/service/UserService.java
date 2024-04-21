package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.dto.UserEmployeeDTO;
import com.hrm.Human.Resource.Management.dto.UserRegistrationDTO;
import com.hrm.Human.Resource.Management.entity.Permission;
import com.hrm.Human.Resource.Management.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {

    List<UserEmployeeDTO> getAllUsers();

    User createUser(UserRegistrationDTO userRegistration);


    Set<Map<String, String>> getUserPermissions(Long id);

    User changePermissions(Long userId, Set<Long> newPermissionIds);

    String changePassword(Long userId, String oldPassword, String newPassword);

    String resetPassword(String userName);

    String createTemporaryPasswordForAdmin(Long userId, String adminEmail);

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