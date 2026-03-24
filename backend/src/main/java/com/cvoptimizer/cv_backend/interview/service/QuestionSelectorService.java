package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;
import com.cvoptimizer.cv_backend.interview.model.QuestionType;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionSelectorService {

    public List<InterviewQuestion> selectQuestions(Set<String> jobSkills,
                                                   List<InterviewQuestion> bank,
                                                   int n) {

        if (bank == null || bank.isEmpty() || n <= 0) {
            return List.of();
        }

        Set<String> safeSkills = (jobSkills == null) ? Set.of() : jobSkills;

        List<InterviewQuestion> ranked = bank.stream()
                .sorted((a, b) -> Integer.compare(score(b, safeSkills), score(a, safeSkills)))
                .collect(Collectors.toList());

        if (ranked.size() <= n) {
            return ranked;
        }

        List<InterviewQuestion> result = new ArrayList<>();
        Set<String> addedKeys = new HashSet<>();
        Set<String> coveredSkills = new HashSet<>();

        // 1) ensure diversity by type
        addIfNotExists(result, addedKeys, pickFirstByType(ranked, QuestionType.MCQ));
        addIfNotExists(result, addedKeys, pickFirstByType(ranked, QuestionType.CODE));

        // update covered skills after forced additions
        for (InterviewQuestion q : result) {
            if (q.getTags() != null) {
                for (String tag : q.getTags()) {
                    if (safeSkills.contains(tag)) {
                        coveredSkills.add(tag);
                    }
                }
            }
        }

        // 2) try to cover as many different matched skills as possible
        for (String skill : safeSkills) {
            if (result.size() >= n) {
                break;
            }

            InterviewQuestion bestForSkill = ranked.stream()
                    .filter(q -> q.getTags() != null && q.getTags().contains(skill))
                    .filter(q -> !addedKeys.contains(uniqueKey(q)))
                    .findFirst()
                    .orElse(null);

            if (bestForSkill != null) {
                addIfNotExists(result, addedKeys, bestForSkill);
                coveredSkills.add(skill);
            }
        }

        // 3) fill remaining by score
        for (InterviewQuestion q : ranked) {
            if (result.size() >= n) {
                break;
            }
            addIfNotExists(result, addedKeys, q);
        }

        return result.size() > n ? result.subList(0, n) : result;
    }

    private int score(InterviewQuestion q, Set<String> jobSkills) {
        if (q == null) {
            return 0;
        }

        int score = 0;

        if (q.isCritical()) {
            score += 2;
        }

        if (q.getTags() != null && jobSkills != null) {
            for (String tag : q.getTags()) {
                if (jobSkills.contains(tag)) {
                    score += 5;
                }
            }
        }

        if (q.getType() == QuestionType.CODE) {
            score += 1;
        }

        if (q.getRequiredKeywords() != null && !q.getRequiredKeywords().isEmpty()) {
            score += 1;
        }

        return score;
    }

    private InterviewQuestion pickFirstByType(List<InterviewQuestion> list, QuestionType type) {
        return list.stream()
                .filter(q -> q != null && q.getType() == type)
                .findFirst()
                .orElse(null);
    }

    private void addIfNotExists(List<InterviewQuestion> result,
                                Set<String> addedKeys,
                                InterviewQuestion q) {
        if (q == null) {
            return;
        }

        String key = uniqueKey(q);
        if (addedKeys.add(key)) {
            result.add(q);
        }
    }

    private String uniqueKey(InterviewQuestion q) {
        if (q.getId() != null && !q.getId().isBlank()) {
            return q.getId();
        }
        return q.getType() + "::" + q.getText();
    }
}