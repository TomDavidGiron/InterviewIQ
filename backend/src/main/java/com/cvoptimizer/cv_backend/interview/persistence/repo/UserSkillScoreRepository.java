package com.cvoptimizer.cv_backend.interview.persistence.repo;

import com.cvoptimizer.cv_backend.interview.persistence.entity.UserSkillScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSkillScoreRepository extends JpaRepository<UserSkillScoreEntity, Long> {

    Optional<UserSkillScoreEntity> findByUserIdAndSkill(String userId, String skill);

    List<UserSkillScoreEntity> findByUserIdOrderByScoreDescSkillAsc(String userId);

    void deleteByUserId(String userId);
}