package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;
import com.cvoptimizer.cv_backend.interview.model.QuestionType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class JobSpecificQuestionGeneratorService {

    public List<InterviewQuestion> generateGapCoverageQuestions(
            String roleHint,
            String seniorityHint,
            List<String> missingSkills
    ) {
        List<InterviewQuestion> generated = new ArrayList<>();

        if (missingSkills == null || missingSkills.isEmpty()) {
            return generated;
        }

        for (String skill : missingSkills) {
            InterviewQuestion q = new InterviewQuestion();
            q.setId("generated-" + UUID.randomUUID());
            q.setType(QuestionType.OPEN);
            q.setCritical(false);
            q.setText(buildQuestion(roleHint, seniorityHint, skill));
            q.setTags(buildTags(skill));
            q.setRequiredKeywords(buildRequiredKeywords(skill));
            generated.add(q);
        }

        return generated;
    }

    private String buildQuestion(String roleHint, String seniorityHint, String skill) {
        String role = (roleHint == null || roleHint.isBlank()) ? "software engineering" : roleHint;
        String level = (seniorityHint == null || seniorityHint.isBlank()) ? "mid-level" : seniorityHint;

        return "For a " + level + " " + role +
                " role, explain how you would use " + skill +
                " in a real production scenario. What trade-offs, best practices, and common pitfalls would you consider?";
    }

    private Set<String> buildTags(String skill) {
        Set<String> tags = new LinkedHashSet<>();
        if (skill != null && !skill.isBlank()) {
            tags.add(skill.trim());
            tags.add(skill.trim().toLowerCase());
        }
        tags.add("job-specific");
        return tags;
    }

    private Set<String> buildRequiredKeywords(String skill) {
        Set<String> keywords = new LinkedHashSet<>();

        if (skill != null && !skill.isBlank()) {
            keywords.add(skill.toLowerCase());
        }

        keywords.add("trade-off");
        keywords.add("best practice");
        keywords.add("production");

        return keywords;
    }
}