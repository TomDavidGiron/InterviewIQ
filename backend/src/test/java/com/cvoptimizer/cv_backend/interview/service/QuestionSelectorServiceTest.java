package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;
import com.cvoptimizer.cv_backend.interview.model.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionSelectorServiceTest {

    private QuestionSelectorService selector;

    @BeforeEach
    void setUp() {
        selector = new QuestionSelectorService();
    }

    // ── edge cases ────────────────────────────────────────────────────────────

    @Test
    void selectQuestions_nullBank_returnsEmpty() {
        assertThat(selector.selectQuestions(Set.of("java"), null, 5)).isEmpty();
    }

    @Test
    void selectQuestions_emptyBank_returnsEmpty() {
        assertThat(selector.selectQuestions(Set.of("java"), List.of(), 5)).isEmpty();
    }

    @Test
    void selectQuestions_nIsZero_returnsEmpty() {
        List<InterviewQuestion> bank = List.of(openQ("q1", Set.of("java")));
        assertThat(selector.selectQuestions(Set.of("java"), bank, 0)).isEmpty();
    }

    @Test
    void selectQuestions_bankSmallerThanN_returnsAll() {
        List<InterviewQuestion> bank = List.of(
                openQ("q1", Set.of("java")),
                openQ("q2", Set.of("spring"))
        );
        List<InterviewQuestion> result = selector.selectQuestions(Set.of("java"), bank, 10);
        assertThat(result).hasSize(2);
    }

    // ── count correctness ────────────────────────────────────────────────────

    @Test
    void selectQuestions_returnsExactlyNQuestions() {
        List<InterviewQuestion> bank = buildBank(20);
        List<InterviewQuestion> result = selector.selectQuestions(Set.of("java"), bank, 10);
        assertThat(result).hasSize(10);
    }

    @Test
    void selectQuestions_noDuplicates() {
        List<InterviewQuestion> bank = buildBank(20);
        List<InterviewQuestion> result = selector.selectQuestions(Set.of("java"), bank, 10);

        Set<String> ids = new HashSet<>();
        for (InterviewQuestion q : result) {
            assertThat(ids.add(q.getId())).as("duplicate id: " + q.getId()).isTrue();
        }
    }

    // ── skill matching ───────────────────────────────────────────────────────

    @Test
    void selectQuestions_skillMatchingQuestionsArePreferred() {
        List<InterviewQuestion> bank = new ArrayList<>();
        // 5 java-tagged questions
        for (int i = 0; i < 5; i++) {
            bank.add(openQ("java-" + i, Set.of("java")));
        }
        // 15 unrelated questions
        for (int i = 0; i < 15; i++) {
            bank.add(openQ("other-" + i, Set.of("unrelated")));
        }

        List<InterviewQuestion> result = selector.selectQuestions(Set.of("java"), bank, 5);

        long javaCount = result.stream().filter(q -> q.getTags().contains("java")).count();
        assertThat(javaCount).isGreaterThanOrEqualTo(4);
    }

    @Test
    void selectQuestions_nullJobSkills_stillReturnsNQuestions() {
        List<InterviewQuestion> bank = buildBank(15);
        List<InterviewQuestion> result = selector.selectQuestions(null, bank, 5);
        assertThat(result).hasSize(5);
    }

    // ── type diversity ───────────────────────────────────────────────────────

    @Test
    void selectQuestions_includesMcqAndCodeTypeWhenAvailable() {
        List<InterviewQuestion> bank = new ArrayList<>();
        bank.add(mcqQ("mcq-1", Set.of("java")));
        bank.add(codeQ("code-1", Set.of("java")));
        for (int i = 0; i < 8; i++) {
            bank.add(openQ("open-" + i, Set.of("java")));
        }

        List<InterviewQuestion> result = selector.selectQuestions(Set.of("java"), bank, 5);

        long mcqCount = result.stream().filter(q -> q.getType() == QuestionType.MCQ).count();
        long codeCount = result.stream().filter(q -> q.getType() == QuestionType.CODE).count();
        assertThat(mcqCount).isGreaterThanOrEqualTo(1);
        assertThat(codeCount).isGreaterThanOrEqualTo(1);
    }

    // ── critical flag ────────────────────────────────────────────────────────

    @Test
    void selectQuestions_criticalQuestionsScoreHigher() {
        List<InterviewQuestion> bank = new ArrayList<>();
        InterviewQuestion critical = criticalQ("critical-1", Set.of("niche-skill"));
        bank.add(critical);
        for (int i = 0; i < 15; i++) {
            bank.add(openQ("normal-" + i, Set.of("unrelated")));
        }

        // Select 5 with no matching skills — critical flag still boosts score
        List<InterviewQuestion> result = selector.selectQuestions(Set.of(), bank, 5);

        boolean hasCritical = result.stream().anyMatch(q -> q.getId().equals("critical-1"));
        assertThat(hasCritical).isTrue();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private List<InterviewQuestion> buildBank(int n) {
        List<InterviewQuestion> bank = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            bank.add(openQ("q" + i, Set.of("java", "spring")));
        }
        return bank;
    }

    private InterviewQuestion openQ(String id, Set<String> tags) {
        return new InterviewQuestion(id, "Explain " + id, tags, false, Set.of("keyword"));
    }

    private InterviewQuestion criticalQ(String id, Set<String> tags) {
        InterviewQuestion q = new InterviewQuestion(id, "Critical: " + id, tags, true, Set.of("keyword"));
        return q;
    }

    private InterviewQuestion mcqQ(String id, Set<String> tags) {
        return new InterviewQuestion(id, "MCQ: " + id, tags, false,
                List.of("A", "B", "C", "D"), 0);
    }

    private InterviewQuestion codeQ(String id, Set<String> tags) {
        return new InterviewQuestion(id, "Code: " + id, tags, false,
                "// starter", Set.of("keyword"));
    }
}
