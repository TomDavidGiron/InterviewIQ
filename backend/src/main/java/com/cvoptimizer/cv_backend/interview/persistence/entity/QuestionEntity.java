package com.cvoptimizer.cv_backend.interview.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "question")
public class QuestionEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(length = 10, nullable = false)
    private String type = "OPEN";

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private String[] tags;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "required_keywords", columnDefinition = "text[]")
    private String[] requiredKeywords;

    private boolean critical;

    @Column(columnDefinition = "TEXT")
    private String options;

    @Column(name = "correct_option_index")
    private Integer correctOptionIndex;

    @Column(name = "starter_code", columnDefinition = "TEXT")
    private String starterCode;

    @Column(length = 50, nullable = false)
    private String source = "question_bank";

    @Column(length = 20, nullable = false)
    private String status = "ACTIVE";

    @Column(nullable = false)
    private Instant createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }

    public String[] getRequiredKeywords() { return requiredKeywords; }
    public void setRequiredKeywords(String[] requiredKeywords) { this.requiredKeywords = requiredKeywords; }

    public boolean isCritical() { return critical; }
    public void setCritical(boolean critical) { this.critical = critical; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    public Integer getCorrectOptionIndex() { return correctOptionIndex; }
    public void setCorrectOptionIndex(Integer correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }

    public String getStarterCode() { return starterCode; }
    public void setStarterCode(String starterCode) { this.starterCode = starterCode; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Set<String> parsedTags() {
        return toSet(tags);
    }

    public Set<String> parsedKeywords() {
        return toSet(requiredKeywords);
    }

    private static Set<String> toSet(String[] values) {
        if (values == null) return new LinkedHashSet<>();
        return Arrays.stream(values)
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
