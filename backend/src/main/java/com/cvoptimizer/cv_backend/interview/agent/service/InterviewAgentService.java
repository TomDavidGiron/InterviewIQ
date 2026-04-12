package com.cvoptimizer.cv_backend.interview.agent.service;

import com.cvoptimizer.cv_backend.interview.agent.model.AgentContext;
import com.cvoptimizer.cv_backend.interview.agent.model.AgentDecision;

public interface InterviewAgentService {
    AgentDecision decide(AgentContext context);
}