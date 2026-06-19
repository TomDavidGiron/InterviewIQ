package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.ai.service.AiEvaluationService;
import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;
import com.cvoptimizer.cv_backend.interview.rag.service.RagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InterviewEvaluatorServiceTest {

    @Mock
    private RagService ragService;

    @Mock
    private AiEvaluationService aiEvaluationService;

    private InterviewEvaluatorService evaluator;

    @BeforeEach
    void setUp() {
        when(ragService.retrieveContext(any(), any())).thenReturn(List.of());
        when(aiEvaluationService.evaluate(any())).thenReturn(Optional.empty());
        evaluator = new InterviewEvaluatorService(ragService, aiEvaluationService);
    }

    @Test
    void evaluate_nullQuestion_returnsFail() {
        var result = evaluator.evaluate(null, "some answer");
        assertThat(result.isPassed()).isFalse();
    }

    @Test
    void evaluate_emptyAnswer_returnsFail() {
        InterviewQuestion q = openQuestion(Set.of("transaction", "rollback"));
        var result = evaluator.evaluate(q, "");
        assertThat(result.isPassed()).isFalse();
        assertThat(result.getEarnedPoints()).isEqualTo(0);
    }

    @Test
    void evaluate_answerCoversAllKeywords_returnsPass() {
        InterviewQuestion q = openQuestion(Set.of("cache", "eviction"));
        var result = evaluator.evaluate(q, "The cache uses an LRU eviction policy to remove old entries.");
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getEarnedPoints()).isGreaterThan(0);
    }

    @Test
    void evaluate_answerMissesAllKeywords_returnsFail() {
        InterviewQuestion q = openQuestion(Set.of("transaction", "rollback", "commit"));
        var result = evaluator.evaluate(q, "I am not sure about this.");
        assertThat(result.isPassed()).isFalse();
        assertThat(result.getMissingKeywords()).isNotEmpty();
    }

    @Test
    void evaluate_mcqCorrectAnswer_returnsPass() {
        InterviewQuestion q = mcqQuestion(List.of("O(1)", "O(n)", "O(log n)", "O(n^2)"), 0);
        var result = evaluator.evaluate(q, "0");
        assertThat(result.isPassed()).isTrue();
    }

    @Test
    void evaluate_mcqWrongAnswer_returnsFail() {
        InterviewQuestion q = mcqQuestion(List.of("O(1)", "O(n)", "O(log n)", "O(n^2)"), 0);
        var result = evaluator.evaluate(q, "2");
        assertThat(result.isPassed()).isFalse();
    }

    @Test
    void evaluate_noRubric_returnsBasicPass() {
        InterviewQuestion q = openQuestion(Set.of());
        var result = evaluator.evaluate(q, "some answer");
        assertThat(result.isPassed()).isTrue();
    }

    private InterviewQuestion openQuestion(Set<String> keywords) {
        return new InterviewQuestion("q1", "Explain caching.", Set.of("backend"), false, keywords);
    }

    private InterviewQuestion mcqQuestion(List<String> options, int correctIndex) {
        return new InterviewQuestion("q2", "What is the time complexity of HashMap.get()?",
                Set.of("java"), false, options, correctIndex);
    }
}
