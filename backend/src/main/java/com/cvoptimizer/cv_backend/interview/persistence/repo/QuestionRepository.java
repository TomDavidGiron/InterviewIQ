package com.cvoptimizer.cv_backend.interview.persistence.repo;

import com.cvoptimizer.cv_backend.interview.persistence.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, String> {
    List<QuestionEntity> findByStatus(String status);
    long countByStatus(String status);
}
