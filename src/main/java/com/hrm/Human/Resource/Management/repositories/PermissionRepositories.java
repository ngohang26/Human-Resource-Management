package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface PermissionRepositories extends JpaRepository<Permission, Long> {
}
