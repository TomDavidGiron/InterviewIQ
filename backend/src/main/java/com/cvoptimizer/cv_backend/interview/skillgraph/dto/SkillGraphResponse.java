package com.cvoptimizer.cv_backend.interview.skillgraph.dto;

import java.util.List;

public class SkillGraphResponse {

    private String userId;
    private int overallAverage;
    private List<SkillScoreDto> skills;
    private List<String> strongestSkills;
    private List<String> weakestSkills;

    public SkillGraphResponse() {
    }

    public SkillGraphResponse(String userId,
                              int overallAverage,
                              List<SkillScoreDto> skills,
                              List<String> strongestSkills,
                              List<String> weakestSkills) {
        this.userId = userId;
        this.overallAverage = overallAverage;
        this.skills = skills;
        this.strongestSkills = strongestSkills;
        this.weakestSkills = weakestSkills;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getOverallAverage() {
        return overallAverage;
    }

    public void setOverallAverage(int overallAverage) {
        this.overallAverage = overallAverage;
    }

    public List<SkillScoreDto> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillScoreDto> skills) {
        this.skills = skills;
    }

    public List<String> getStrongestSkills() {
        return strongestSkills;
    }

    public void setStrongestSkills(List<String> strongestSkills) {
        this.strongestSkills = strongestSkills;
    }

    public List<String> getWeakestSkills() {
        return weakestSkills;
    }

    public void setWeakestSkills(List<String> weakestSkills) {
        this.weakestSkills = weakestSkills;
    }
}