package com.cvoptimizer.cv_backend.interview.rag.service;

import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;
import com.cvoptimizer.cv_backend.interview.service.QuestionBankService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

@Component
public class QuestionBankKnowledgeSeeder implements ApplicationRunner {

    private final RagService ragService;
    private final QuestionBankService questionBankService;

    public QuestionBankKnowledgeSeeder(RagService ragService, QuestionBankService questionBankService) {
        this.ragService = ragService;
        this.questionBankService = questionBankService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (ragService.countChunks() > 0) {
            return;
        }

        List<InterviewQuestion> questions = questionBankService.getBackendJuniorBank();

        for (InterviewQuestion question : questions) {
            String chunkKey = "question_bank:" + question.getId();
            String content = buildContent(question);
            String topic = firstTag(question.getTags());
            String difficulty = "junior";
            String source = "QuestionBankService";
            String sourceType = "question_bank";
            String metadataJson = buildMetadata(question);

            ragService.indexChunk(
                    chunkKey,
                    content,
                    topic,
                    difficulty,
                    source,
                    sourceType,
                    metadataJson
            );
        }
    }

    private String buildContent(InterviewQuestion question) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("Question: " + safe(question.getText()));
        joiner.add("Type: " + (question.getType() == null ? "UNKNOWN" : question.getType().name()));
        joiner.add("Tags: " + String.join(", ", safeSet(question.getTags())));
        joiner.add("Required keywords: " + String.join(", ", safeSet(question.getRequiredKeywords())));

        if (question.getStarterCode() != null && !question.getStarterCode().isBlank()) {
            joiner.add("Starter code: " + question.getStarterCode());
        }

        return joiner.toString();
    }

    private String buildMetadata(InterviewQuestion question) {
        String questionId = safe(question.getId());
        String type = question.getType() == null ? "UNKNOWN" : question.getType().name();
        return "{\"questionId\":\"" + questionId + "\",\"type\":\"" + type + "\"}";
    }

    private String firstTag(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "general";
        }
        return tags.iterator().next();
    }

    private Set<String> safeSet(Set<String> values) {
        return values == null ? Set.of() : values;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}