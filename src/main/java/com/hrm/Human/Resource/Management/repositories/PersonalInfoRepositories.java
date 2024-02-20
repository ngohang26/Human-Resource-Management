package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.PersonalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface PersonalInfoRepositories extends JpaRepository<PersonalInfo, Long> {
    Optional<PersonalInfo> findByIdentityCardNumber(String identityCardNumber);
}
