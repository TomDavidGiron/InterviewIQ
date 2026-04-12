package com.cvoptimizer.cv_backend.interview.agent.service;

import com.cvoptimizer.cv_backend.interview.agent.model.AgentAction;
import com.cvoptimizer.cv_backend.interview.agent.model.AgentContext;
import com.cvoptimizer.cv_backend.interview.agent.model.AgentDecision;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class RuleBasedInterviewAgentService implements InterviewAgentService {

    private static final int MAX_FOLLOW_UPS_PER_SESSION = 2;
    private static final int HARD_PUSH_STREAK = 2;
    private static final int EASY_DROP_STREAK = 2;
    private static final int VERY_LOW_PERCENT_NEAR_END = 40;
    private static final int VERY_STRONG_PERCENT = 85;

    @Override
    public AgentDecision decide(AgentContext context) {
        if (context == null) {
            return AgentDecision.finish("Agent context was null");
        }

        boolean nearEnd = context.getQuestionIndex() >= Math.max(0, context.getTotalQuestions() - 2);
        boolean failed = !context.isLastEvaluationPassed();
        boolean passed = context.isLastEvaluationPassed();

        boolean hasMissingConcepts =
                context.getMissingConcepts() != null && !context.getMissingConcepts().isEmpty();
        boolean hasWeakTopics =
                context.getWeakTopics() != null && !context.getWeakTopics().isEmpty();

        boolean canAskFollowUp = context.getFollowUpCount() < MAX_FOLLOW_UPS_PER_SESSION
                && context.getLastAgentAction() != AgentAction.ASK_FOLLOW_UP;

        if (nearEnd && context.getCurrentPercent() < VERY_LOW_PERCENT_NEAR_END) {
            return AgentDecision.finish("Low score near end of interview");
        }

        if (passed && context.getConsecutivePasses() >= HARD_PUSH_STREAK && context.getCurrentDifficulty() < 3) {
            AgentDecision d = new AgentDecision(AgentAction.INCREASE_DIFFICULTY,
                    "Strong streak detected, increasing difficulty");
            d.setDifficultyDelta(1);
            return d;
        }

        if (failed && context.getConsecutiveFails() >= EASY_DROP_STREAK && context.getCurrentDifficulty() > 1) {
            AgentDecision d = new AgentDecision(AgentAction.DECREASE_DIFFICULTY,
                    "Repeated misses detected, lowering difficulty");
            d.setDifficultyDelta(-1);
            return d;
        }

        if (failed && hasMissingConcepts && canAskFollowUp) {
            String concept = context.getMissingConcepts().get(0);
            String prompt = "Follow-up: you missed the concept '" + concept
                    + "'. Explain it with a practical backend example and when it matters in production.";
            return AgentDecision.followUp("User missed an important concept", prompt, true);
        }

        if (failed && hasWeakTopics && context.getConsecutiveFails() >= 1) {
            String nextTopic = context.getWeakTopics().stream()
                    .sorted(Comparator.naturalOrder())
                    .findFirst()
                    .orElse(null);

            if (nextTopic != null && !nextTopic.isBlank()) {
                return AgentDecision.switchTopic("Weak topic needs reinforcement", nextTopic);
            }
        }

        if (passed && context.getCurrentPercent() >= VERY_STRONG_PERCENT && context.getCurrentDifficulty() < 3) {
            AgentDecision d = new AgentDecision(AgentAction.INCREASE_DIFFICULTY,
                    "Very strong performance, increasing challenge");
            d.setDifficultyDelta(1);
            return d;
        }

        return AgentDecision.nextQuestion("Default progression");
    }
}