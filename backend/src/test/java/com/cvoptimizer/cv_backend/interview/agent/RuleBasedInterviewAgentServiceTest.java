package com.cvoptimizer.cv_backend.interview.agent;

import com.cvoptimizer.cv_backend.interview.agent.model.AgentAction;
import com.cvoptimizer.cv_backend.interview.agent.model.AgentContext;
import com.cvoptimizer.cv_backend.interview.agent.model.AgentDecision;
import com.cvoptimizer.cv_backend.interview.agent.service.RuleBasedInterviewAgentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RuleBasedInterviewAgentServiceTest {

    private RuleBasedInterviewAgentService agent;

    @BeforeEach
    void setUp() {
        agent = new RuleBasedInterviewAgentService();
    }

    @Test
    void decide_nullContext_returnsFinish() {
        AgentDecision decision = agent.decide(null);
        assertThat(decision.getAction()).isEqualTo(AgentAction.FINISH_INTERVIEW);
    }

    @Test
    void decide_nearEndWithLowScore_returnsFinish() {
        AgentContext ctx = baseContext();
        ctx.setQuestionIndex(8);
        ctx.setTotalQuestions(10);
        ctx.setCurrentPercent(35); // below 40%
        ctx.setLastEvaluationPassed(false);

        AgentDecision decision = agent.decide(ctx);
        assertThat(decision.getAction()).isEqualTo(AgentAction.FINISH_INTERVIEW);
    }

    @Test
    void decide_consecutivePassStreak_increasesDifficulty() {
        AgentContext ctx = baseContext();
        ctx.setLastEvaluationPassed(true);
        ctx.setConsecutivePasses(2);
        ctx.setCurrentDifficulty(1);

        AgentDecision decision = agent.decide(ctx);
        assertThat(decision.getAction()).isEqualTo(AgentAction.INCREASE_DIFFICULTY);
        assertThat(decision.getDifficultyDelta()).isEqualTo(1);
    }

    @Test
    void decide_consecutiveFailStreak_decreasesDifficulty() {
        AgentContext ctx = baseContext();
        ctx.setLastEvaluationPassed(false);
        ctx.setConsecutiveFails(2);
        ctx.setCurrentDifficulty(3);

        AgentDecision decision = agent.decide(ctx);
        assertThat(decision.getAction()).isEqualTo(AgentAction.DECREASE_DIFFICULTY);
        assertThat(decision.getDifficultyDelta()).isEqualTo(-1);
    }

    @Test
    void decide_failedWithMissingConcepts_asksFollowUp() {
        AgentContext ctx = baseContext();
        ctx.setLastEvaluationPassed(false);
        ctx.setConsecutiveFails(1);
        ctx.setMissingConcepts(List.of("idempotency"));
        ctx.setFollowUpCount(0);
        ctx.setLastAgentAction(AgentAction.ASK_NEXT_QUESTION);

        AgentDecision decision = agent.decide(ctx);
        assertThat(decision.getAction()).isEqualTo(AgentAction.ASK_FOLLOW_UP);
        assertThat(decision.getFollowUpPrompt()).contains("idempotency");
    }

    @Test
    void decide_veryStrongPerformance_increasesDifficulty() {
        AgentContext ctx = baseContext();
        ctx.setLastEvaluationPassed(true);
        ctx.setConsecutivePasses(1);
        ctx.setCurrentPercent(90); // above 85%
        ctx.setCurrentDifficulty(2);

        AgentDecision decision = agent.decide(ctx);
        assertThat(decision.getAction()).isEqualTo(AgentAction.INCREASE_DIFFICULTY);
    }

    @Test
    void decide_normalProgression_returnsNextQuestion() {
        AgentContext ctx = baseContext();
        ctx.setLastEvaluationPassed(true);
        ctx.setConsecutivePasses(1);
        ctx.setCurrentPercent(70);
        ctx.setCurrentDifficulty(2);

        AgentDecision decision = agent.decide(ctx);
        assertThat(decision.getAction()).isEqualTo(AgentAction.ASK_NEXT_QUESTION);
    }

    private AgentContext baseContext() {
        AgentContext ctx = new AgentContext();
        ctx.setQuestionIndex(3);
        ctx.setTotalQuestions(10);
        ctx.setCurrentPercent(70);
        ctx.setCurrentDifficulty(2);
        ctx.setConsecutivePasses(0);
        ctx.setConsecutiveFails(0);
        ctx.setFollowUpCount(0);
        ctx.setLastAgentAction(AgentAction.ASK_NEXT_QUESTION);
        return ctx;
    }
}
