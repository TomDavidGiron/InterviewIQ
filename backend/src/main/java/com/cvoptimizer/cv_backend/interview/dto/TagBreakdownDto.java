package com.cvoptimizer.cv_backend.interview.dto;

public class TagBreakdownDto {

    private String tag;
    private int count;
    private int mistakes;
    private int avgScore;     // 0-100
    private String severity;  // STRONG / OK / WEAK
    private boolean passed;   // avgScore >= 70

    public TagBreakdownDto() {}

    public TagBreakdownDto(String tag, int count, int mistakes, int avgScore, String severity, boolean passed) {
        this.tag = tag;
        this.count = count;
        this.mistakes = mistakes;
        this.avgScore = avgScore;
        this.severity = severity;
        this.passed = passed;
    }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public int getMistakes() { return mistakes; }
    public void setMistakes(int mistakes) { this.mistakes = mistakes; }

    public int getAvgScore() { return avgScore; }
    public void setAvgScore(int avgScore) { this.avgScore = avgScore; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
}