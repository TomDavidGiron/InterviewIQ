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

        Set<String> safeSkills = normalizeSkills(jobSkills);

        List<InterviewQuestion> shuffled = new ArrayList<>(bank);
        Collections.shuffle(shuffled);

        List<InterviewQuestion> ranked = shuffled.stream()
                .sorted((a, b) -> Integer.compare(score(b, safeSkills), score(a, safeSkills)))
                .collect(Collectors.toList());

        if (ranked.size() <= n) {
            return ranked;
        }

        List<InterviewQuestion> result = new ArrayList<>();
        Set<String> addedKeys = new HashSet<>();
        Set<String> coveredSkills = new HashSet<>();

        // 1) ensure diversity by type — but prefer a skill-matching question of that type
        // over a random one, so type diversity never overrides relevance to the job.
        addIfNotExists(result, addedKeys, pickFirstByType(ranked, QuestionType.MCQ, safeSkills));
        addIfNotExists(result, addedKeys, pickFirstByType(ranked, QuestionType.CODE, safeSkills));

        // update covered skills after forced additions
        for (InterviewQuestion q : result) {
            if (q.getTags() != null) {
                for (String tag : q.getTags()) {
                    if (tag != null && safeSkills.contains(tag.toLowerCase())) {
                        coveredSkills.add(tag.toLowerCase());
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
                    .filter(q -> q.getTags() != null && q.getTags().stream()
                            .anyMatch(t -> t != null && t.equalsIgnoreCase(skill)))
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
                if (tag != null && jobSkills.contains(tag.toLowerCase())) {
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

    private InterviewQuestion pickFirstByType(List<InterviewQuestion> list, QuestionType type, Set<String> jobSkills) {
        if (jobSkills != null && !jobSkills.isEmpty()) {
            InterviewQuestion matched = list.stream()
                    .filter(q -> q != null && q.getType() == type)
                    .filter(q -> q.getTags() != null && q.getTags().stream()
                            .anyMatch(t -> t != null && jobSkills.contains(t.toLowerCase())))
                    .findFirst()
                    .orElse(null);
            if (matched != null) {
                return matched;
            }
        }

        // No skill-matching question of this type exists — fall back to the top-ranked one
        // of that type so we still get format diversity, just without a relevance guarantee.
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

    private Set<String> normalizeSkills(Set<String> skills) {
        if (skills == null) {
            return Set.of();
        }
        Set<String> normalized = new LinkedHashSet<>();
        for (String skill : skills) {
            if (skill != null && !skill.isBlank()) {
                normalized.add(skill.trim().toLowerCase());
            }
        }
        return normalized;
    }

    private String uniqueKey(InterviewQuestion q) {
        if (q.getId() != null && !q.getId().isBlank()) {
            return q.getId();
        }
        return q.getType() + "::" + q.getText();
    }
}