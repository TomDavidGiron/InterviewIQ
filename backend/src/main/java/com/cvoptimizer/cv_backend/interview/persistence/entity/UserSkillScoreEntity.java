package com.cvoptimizer.cv_backend.interview.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "user_skill_scores",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_skill_scores_user_skill", columnNames = {"user_id", "skill"})
        }
)
public class UserSkillScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(nullable = false, length = 120)
    private String skill;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    public UserSkillScoreEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}