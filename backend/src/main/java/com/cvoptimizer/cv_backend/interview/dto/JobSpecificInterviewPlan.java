package com.cvoptimizer.cv_backend.interview.dto;

import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;

import java.util.ArrayList;
import java.util.List;

public class JobSpecificInterviewPlan {

    private String normalizedJobText;
    private List<String> extractedSkills = new ArrayList<>();
    private List<String> prioritizedSkills = new ArrayList<>();
    private List<InterviewQuestion> selectedQuestions = new ArrayList<>();

    private String roleTitleHint;
    private String seniorityHint;
    private String domainHint;

    private List<String> mustHaveSkills = new ArrayList<>();
    private List<String> niceToHaveSkills = new ArrayList<>();
    private boolean generatedQuestionsUsed;
    private List<String> planningNotes = new ArrayList<>();

    public String getNormalizedJobText() {
        return normalizedJobText;
    }

    public void setNormalizedJobText(String normalizedJobText) {
        this.normalizedJobText = normalizedJobText;
    }

    public List<String> getExtractedSkills() {
        return extractedSkills;
    }

    public void setExtractedSkills(List<String> extractedSkills) {
        this.extractedSkills = extractedSkills;
    }

    public List<String> getPrioritizedSkills() {
        return prioritizedSkills;
    }

    public void setPrioritizedSkills(List<String> prioritizedSkills) {
        this.prioritizedSkills = prioritizedSkills;
    }

    public List<InterviewQuestion> getSelectedQuestions() {
        return selectedQuestions;
    }

    public void setSelectedQuestions(List<InterviewQuestion> selectedQuestions) {
        this.selectedQuestions = selectedQuestions;
    }

    public String getRoleTitleHint() {
        return roleTitleHint;
    }

    public void setRoleTitleHint(String roleTitleHint) {
        this.roleTitleHint = roleTitleHint;
    }

    public String getSeniorityHint() {
        return seniorityHint;
    }

    public void setSeniorityHint(String seniorityHint) {
        this.seniorityHint = seniorityHint;
    }

    public String getDomainHint() {
        return domainHint;
    }

    public void setDomainHint(String domainHint) {
        this.domainHint = domainHint;
    }

    public List<String> getMustHaveSkills() {
        return mustHaveSkills;
    }

    public void setMustHaveSkills(List<String> mustHaveSkills) {
        this.mustHaveSkills = mustHaveSkills;
    }

    public List<String> getNiceToHaveSkills() {
        return niceToHaveSkills;
    }

    public void setNiceToHaveSkills(List<String> niceToHaveSkills) {
        this.niceToHaveSkills = niceToHaveSkills;
    }

    public boolean isGeneratedQuestionsUsed() {
        return generatedQuestionsUsed;
    }

    public void setGeneratedQuestionsUsed(boolean generatedQuestionsUsed) {
        this.generatedQuestionsUsed = generatedQuestionsUsed;
    }

    public List<String> getPlanningNotes() {
        return planningNotes;
    }

    public void setPlanningNotes(List<String> planningNotes) {
        this.planningNotes = planningNotes;
    }
}