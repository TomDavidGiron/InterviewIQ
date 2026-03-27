package com.cvoptimizer.cv_backend.interview.model;

import java.util.List;
import java.util.Set;

public class InterviewQuestion {
    private String id;
    private String text;
    private Set<String> tags;              // e.g. sql, spring, docker
    private boolean critical;              // if true -> can fail session
    private Set<String> requiredKeywords;  // rubric for OPEN/CODE (MVP)

    // Phase C
    private QuestionType type = QuestionType.OPEN; // default
    private List<String> options;                  // for MCQ
    private Integer correctOptionIndex;            // for MCQ (0-based)
    private String starterCode;                    // for CODE (optional)

    public InterviewQuestion() {}

    // ✅ Existing constructor (OPEN question) - keeps your current bank working
    public InterviewQuestion(String id,
                             String text,
                             Set<String> tags,
                             boolean critical,
                             Set<String> requiredKeywords) {
        this.id = id;
        this.text = text;
        this.tags = tags;
        this.critical = critical;
        this.requiredKeywords = requiredKeywords;
        this.type = QuestionType.OPEN;
    }

    // ✅ NEW constructor for MCQ (this fixes your error)
    public InterviewQuestion(String id,
                             String text,
                             Set<String> tags,
                             boolean critical,
                             List<String> options,
                             int correctOptionIndex) {
        this.id = id;
        this.text = text;
        this.tags = tags;
        this.critical = critical;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.requiredKeywords = Set.of(); // MCQ doesn't use keywords
        this.type = QuestionType.MCQ;
    }

    // ✅ NEW constructor for CODE (minimal: still keyword-based evaluation)
    public InterviewQuestion(String id,
                             String text,
                             Set<String> tags,
                             boolean critical,
                             String starterCode,
                             Set<String> requiredKeywords) {
        this.id = id;
        this.text = text;
        this.tags = tags;
        this.critical = critical;
        this.starterCode = starterCode;
        this.requiredKeywords = requiredKeywords;
        this.type = QuestionType.CODE;
    }

    // --- getters/setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public boolean isCritical() { return critical; }
    public void setCritical(boolean critical) { this.critical = critical; }

    public Set<String> getRequiredKeywords() { return requiredKeywords; }
    public void setRequiredKeywords(Set<String> requiredKeywords) { this.requiredKeywords = requiredKeywords; }

    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public Integer getCorrectOptionIndex() { return correctOptionIndex; }
    public void setCorrectOptionIndex(Integer correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }

    public String getStarterCode() { return starterCode; }
    public void setStarterCode(String starterCode) { this.starterCode = starterCode; }
}