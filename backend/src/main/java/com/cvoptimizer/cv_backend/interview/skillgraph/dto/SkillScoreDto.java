package com.cvoptimizer.cv_backend.interview.skillgraph.dto;

import java.time.Instant;

public class SkillScoreDto {

    private String skill;
    private int score;
    private Instant lastUpdated;

    public SkillScoreDto() {
    }

    public SkillScoreDto(String skill, int score, Instant lastUpdated) {
        this.skill = skill;
        this.score = score;
        this.lastUpdated = lastUpdated;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}