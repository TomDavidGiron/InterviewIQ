package com.cvoptimizer.cv_backend.repository;

import com.cvoptimizer.cv_backend.entity.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {
}
