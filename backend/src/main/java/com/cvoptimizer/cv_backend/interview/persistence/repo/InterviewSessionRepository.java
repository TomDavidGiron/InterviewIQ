package com.cvoptimizer.cv_backend.interview.persistence.repo;

import com.cvoptimizer.cv_backend.interview.persistence.entity.InterviewSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSessionRepository extends JpaRepository<InterviewSessionEntity, String> {
}
