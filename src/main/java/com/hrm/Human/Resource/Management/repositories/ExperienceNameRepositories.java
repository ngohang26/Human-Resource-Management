package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.ExperienceName;
import com.hrm.Human.Resource.Management.entity.Experiences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface ExperienceNameRepositories extends JpaRepository<ExperienceName, Long> {
}
