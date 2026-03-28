package com.cvoptimizer.cv_backend.interview.dto;

import com.cvoptimizer.cv_backend.interview.model.QuestionType;

import java.util.List;

public class QuestionDto {
    private String id;
    private String text;

    // Phase C
    private QuestionType type;
    private List<String> options;
    private String starterCode;

    public QuestionDto() {}

    public QuestionDto(String id, String text) {
        this.id = id;
        this.text = text;
        this.type = QuestionType.OPEN;
    }

    public QuestionDto(String id, String text, QuestionType type, List<String> options, String starterCode) {
        this.id = id;
        this.text = text;
        this.type = (type == null) ? QuestionType.OPEN : type;
        this.options = options;
        this.starterCode = starterCode;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public String getStarterCode() { return starterCode; }
    public void setStarterCode(String starterCode) { this.starterCode = starterCode; }
}