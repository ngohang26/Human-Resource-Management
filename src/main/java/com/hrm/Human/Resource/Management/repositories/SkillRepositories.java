package com.hrm.Human.Resource.Management.repositories;

import com.hrm.Human.Resource.Management.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface SkillRepositories extends JpaRepository<Skill, Long> {
}
