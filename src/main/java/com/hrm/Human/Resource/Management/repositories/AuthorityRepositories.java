package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface AuthorityRepositories extends JpaRepository<Authority, Long> {
    Authority findByName(String name);
}
