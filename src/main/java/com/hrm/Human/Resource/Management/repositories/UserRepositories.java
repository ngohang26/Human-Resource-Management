package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface UserRepositories extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
