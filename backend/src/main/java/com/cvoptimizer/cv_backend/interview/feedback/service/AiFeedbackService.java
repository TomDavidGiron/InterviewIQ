package com.cvoptimizer.cv_backend.interview.feedback.service;

import com.cvoptimizer.cv_backend.interview.feedback.dto.*;
import org.springframework.stereotype.Service;

@Service
public class AiFeedbackService {

    public AiFeedbackResponse generateFeedback(AiFeedbackRequest request) {
        AiFeedbackResponse res = new AiFeedbackResponse();

        res.setDiagnosis("Candidate shows solid fundamentals but lacks depth in key areas.");
        res.setStrengths(request.getStrengths());
        res.setWeaknesses(request.getWeaknesses());

        res.setStudyPlan("""
                1. Focus on weak topics first
                2. Practice real interview questions
                3. Review system design basics
        """);

        res.setFeedbackSummary("Good progress, but needs improvement in weak areas.");

        return res;
    }
}