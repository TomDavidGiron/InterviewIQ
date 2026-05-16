package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.dto.JobDescriptionAnalysis;
import com.cvoptimizer.cv_backend.interview.dto.JobSpecificInterviewPlan;
import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;
import com.cvoptimizer.cv_backend.service.JobSkillExtractorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobSpecificInterviewPlannerService {

    private final JobSkillExtractorService jobSkillExtractorService;
    private final QuestionBankService questionBankService;
    private final QuestionSelectorService questionSelectorService;
    private final JobSpecificQuestionGeneratorService jobSpecificQuestionGeneratorService;

    public JobSpecificInterviewPlannerService(
            JobSkillExtractorService jobSkillExtractorService,
            QuestionBankService questionBankService,
            QuestionSelectorService questionSelectorService,
            JobSpecificQuestionGeneratorService jobSpecificQuestionGeneratorService
    ) {
        this.jobSkillExtractorService = jobSkillExtractorService;
        this.questionBankService = questionBankService;
        this.questionSelectorService = questionSelectorService;
        this.jobSpecificQuestionGeneratorService = jobSpecificQuestionGeneratorService;
    }

    public JobSpecificInterviewPlan buildPlan(String jobText, String topicOverride) {
        JobSpecificInterviewPlan plan = new JobSpecificInterviewPlan();

        JobDescriptionAnalysis analysis = jobSkillExtractorService.analyzeJobDescription(jobText);

        plan.setNormalizedJobText(analysis.getNormalizedJobText());
        plan.setExtractedSkills(analysis.getExtractedSkills());
        plan.setMustHaveSkills(analysis.getMustHaveSkills());
        plan.setNiceToHaveSkills(analysis.getNiceToHaveSkills());
        plan.setRoleTitleHint(analysis.getRoleHint());
        plan.setSeniorityHint(analysis.getSeniorityHint());
        plan.setDomainHint(analysis.getDomainHint());
        plan.setPlanningNotes(analysis.getPlanningNotes());

        List<String> prioritized = prioritizeSkills(analysis, topicOverride);
        plan.setPrioritizedSkills(prioritized);

        Set<String> prioritizedSkillSet = prioritized.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<InterviewQuestion> questionBank = questionBankService.getAllFromDatabase();

        List<InterviewQuestion> bankQuestions = questionSelectorService.selectQuestions(
                prioritizedSkillSet,
                questionBank,
                5
        );

        Set<String> coveredSkills = extractCoveredSkills(bankQuestions, prioritizedSkillSet);

        List<String> missingCoverage = prioritized.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .filter(skill -> !containsIgnoreCase(coveredSkills, skill))
                .limit(2)
                .collect(Collectors.toList());

        List<InterviewQuestion> generated = jobSpecificQuestionGeneratorService.generateGapCoverageQuestions(
                analysis.getRoleHint(),
                analysis.getSeniorityHint(),
                missingCoverage
        );

        List<InterviewQuestion> finalQuestions = new ArrayList<>(bankQuestions);
        finalQuestions.addAll(generated);

        plan.setGeneratedQuestionsUsed(!generated.isEmpty());
        plan.setSelectedQuestions(finalQuestions);

        return plan;
    }

    private List<String> prioritizeSkills(JobDescriptionAnalysis analysis, String topicOverride) {
        LinkedHashSet<String> prioritized = new LinkedHashSet<>();

        if (topicOverride != null && !topicOverride.isBlank()) {
            prioritized.add(topicOverride.trim());
        }

        if (analysis.getMustHaveSkills() != null) {
            prioritized.addAll(analysis.getMustHaveSkills());
        }

        if (analysis.getExtractedSkills() != null) {
            prioritized.addAll(analysis.getExtractedSkills());
        }

        if (analysis.getNiceToHaveSkills() != null) {
            prioritized.addAll(analysis.getNiceToHaveSkills());
        }

        return prioritized.stream().limit(8).collect(Collectors.toList());
    }

    private Set<String> extractCoveredSkills(List<InterviewQuestion> questions, Set<String> prioritizedSkills) {
        Set<String> covered = new LinkedHashSet<>();

        if (questions == null || prioritizedSkills == null || prioritizedSkills.isEmpty()) {
            return covered;
        }

        for (InterviewQuestion question : questions) {
            if (question == null || question.getTags() == null) {
                continue;
            }

            for (String tag : question.getTags()) {
                for (String prioritizedSkill : prioritizedSkills) {
                    if (tag != null && prioritizedSkill != null && tag.equalsIgnoreCase(prioritizedSkill)) {
                        covered.add(prioritizedSkill);
                    }
                }
            }
        }

        return covered;
    }

    private boolean containsIgnoreCase(Set<String> values, String target) {
        if (values == null || target == null) {
            return false;
        }

        for (String value : values) {
            if (value != null && value.equalsIgnoreCase(target)) {
                return true;
            }
        }

        return false;
    }
}