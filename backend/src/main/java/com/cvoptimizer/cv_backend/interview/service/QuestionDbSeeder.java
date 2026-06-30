package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;
import com.cvoptimizer.cv_backend.interview.model.QuestionType;
import com.cvoptimizer.cv_backend.interview.persistence.entity.QuestionEntity;
import com.cvoptimizer.cv_backend.interview.persistence.repo.QuestionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Component
@Order(1)
public class QuestionDbSeeder implements ApplicationRunner {

    private final QuestionRepository questionRepository;
    private final QuestionBankService questionBankService;
    private final ObjectMapper objectMapper;

    public QuestionDbSeeder(QuestionRepository questionRepository,
                            QuestionBankService questionBankService,
                            ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.questionBankService = questionBankService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<InterviewQuestion> all = questionBankService.getBackendJuniorBank();
        int saved = 0;
        int skipped = 0;

        for (InterviewQuestion q : all) {
            try {
                if (questionRepository.existsById(q.getId())) {
                    skipped++;
                    continue;
                }
                questionRepository.save(toEntity(q));
                saved++;
            } catch (Exception e) {
                System.err.println("[QuestionDbSeeder] Skipping question " + q.getId() + ": " + e.getMessage());
                skipped++;
            }
        }

        System.out.println("[QuestionDbSeeder] Seeded " + saved + " questions. Skipped " + skipped + " (duplicates or errors).");
    }

    private QuestionEntity toEntity(InterviewQuestion q) throws Exception {
        QuestionEntity e = new QuestionEntity();
        e.setId(q.getId());
        e.setText(q.getText());
        e.setType(q.getType() != null ? q.getType().name() : QuestionType.OPEN.name());
        e.setTags(toArray(q.getTags()));
        e.setRequiredKeywords(toArray(q.getRequiredKeywords()));
        e.setCritical(q.isCritical());
        e.setCorrectOptionIndex(q.getCorrectOptionIndex());
        e.setStarterCode(q.getStarterCode());
        e.setSource("question_bank");
        e.setStatus("ACTIVE");
        e.setCreatedAt(Instant.now());

        if (q.getOptions() != null && !q.getOptions().isEmpty()) {
            e.setOptions(objectMapper.writeValueAsString(q.getOptions()));
        }

        return e;
    }

    private String[] toArray(Set<String> set) {
        if (set == null || set.isEmpty()) return new String[0];
        return set.toArray(new String[0]);
    }
}
