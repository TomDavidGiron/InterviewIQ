package com.cvoptimizer.cv_backend.interview.persistence.repo;

import com.cvoptimizer.cv_backend.interview.persistence.entity.InterviewAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewAttemptRepository extends JpaRepository<InterviewAttemptEntity, String> {
    List<InterviewAttemptEntity> findBySession_IdOrderByCreatedAtAsc(String sessionId);
}
