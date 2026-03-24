package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.dto.InterviewAttemptDto;
import com.cvoptimizer.cv_backend.interview.dto.InterviewHistoryItemDto;
import com.cvoptimizer.cv_backend.interview.model.InterviewStatus;
import com.cvoptimizer.cv_backend.interview.persistence.entity.InterviewAttemptEntity;
import com.cvoptimizer.cv_backend.interview.persistence.entity.InterviewSessionEntity;
import com.cvoptimizer.cv_backend.interview.persistence.repo.InterviewAttemptRepository;
import com.cvoptimizer.cv_backend.interview.persistence.repo.InterviewSessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InterviewHistoryService {

    private final InterviewSessionRepository sessionRepo;
    private final InterviewAttemptRepository attemptRepo;

    public InterviewHistoryService(InterviewSessionRepository sessionRepo, InterviewAttemptRepository attemptRepo) {
        this.sessionRepo = sessionRepo;
        this.attemptRepo = attemptRepo;
    }

    public InterviewSessionEntity createSession(String roleHint, String levelHint) {
        InterviewSessionEntity s = new InterviewSessionEntity();
        s.setId(UUID.randomUUID().toString());
        s.setRoleHint(roleHint);
        s.setLevelHint(levelHint);
        s.setStatus(InterviewSessionEntity.SessionStatus.IN_PROGRESS);
        s.setCreatedAt(Instant.now());
        s.setScore(0);
        s.setTotalPossibleScore(0);
        return sessionRepo.save(s);
    }

    public InterviewSessionEntity getRequiredSession(String sessionId) {
        return sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
    }

    public void appendAttempt(
            String sessionId,
            String questionId,
            String questionText,
            String answerText,
            String feedback,
            boolean passed,
            int earnedPoints,
            int maxPoints,
            List<String> missingKeywords
    ) {
        InterviewSessionEntity session = getRequiredSession(sessionId);

        InterviewAttemptEntity a = new InterviewAttemptEntity();
        a.setId(UUID.randomUUID().toString());
        a.setSession(session);
        a.setQuestionId(questionId);
        a.setQuestionText(questionText);
        a.setAnswerText(answerText);
        a.setFeedback(feedback);
        a.setPassed(passed);
        a.setEarnedPoints(earnedPoints);
        a.setMaxPoints(maxPoints);
        a.setMissingKeywordsCsv(toCsv(missingKeywords));
        a.setCreatedAt(Instant.now());
        attemptRepo.save(a);

        // keep running totals in the session row too
        session.setScore(safeInt(session.getScore()) + earnedPoints);
        session.setTotalPossibleScore(safeInt(session.getTotalPossibleScore()) + maxPoints);
        sessionRepo.save(session);
    }

    public void finalizeSession(String sessionId, InterviewStatus status, String failReason) {
        InterviewSessionEntity session = getRequiredSession(sessionId);
        session.setStatus(mapStatus(status));
        session.setFailReason(failReason);
        session.setEndedAt(Instant.now());
        sessionRepo.save(session);
    }

    public List<InterviewHistoryItemDto> getHistory(int page, int size) {
        PageRequest pr = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<InterviewSessionEntity> p = sessionRepo.findAll(pr);

        List<InterviewHistoryItemDto> out = new ArrayList<>();
        for (InterviewSessionEntity s : p.getContent()) {
            out.add(new InterviewHistoryItemDto(
                    s.getId(),
                    s.getRoleHint(),
                    s.getLevelHint(),
                    s.getStatus() == null ? null : s.getStatus().name(),
                    s.getCreatedAt(),
                    s.getEndedAt(),
                    safeInt(s.getScore()),
                    safeInt(s.getTotalPossibleScore()),
                    s.getFailReason()
            ));
        }
        return out;
    }

    public List<InterviewAttemptDto> getSessionDetails(String sessionId) {
        List<InterviewAttemptEntity> attempts = attemptRepo.findBySession_IdOrderByCreatedAtAsc(sessionId);
        List<InterviewAttemptDto> out = new ArrayList<>();
        for (InterviewAttemptEntity a : attempts) {
            out.add(new InterviewAttemptDto(
                    a.getId(),
                    a.getQuestionId(),
                    a.getQuestionText(),
                    a.getAnswerText(),
                    a.getFeedback(),
                    a.isPassed(),
                    a.getEarnedPoints(),
                    a.getMaxPoints(),
                    fromCsv(a.getMissingKeywordsCsv()),
                    a.getCreatedAt()
            ));
        }
        return out;
    }

    private InterviewSessionEntity.SessionStatus mapStatus(InterviewStatus status) {
        if (status == null) return InterviewSessionEntity.SessionStatus.IN_PROGRESS;
        return switch (status) {
            case PASSED -> InterviewSessionEntity.SessionStatus.PASSED;
            case FAILED -> InterviewSessionEntity.SessionStatus.FAILED;
            case IN_PROGRESS -> InterviewSessionEntity.SessionStatus.IN_PROGRESS;
        };
    }

    private int safeInt(Integer n) {
        return (n == null) ? 0 : n;
    }

    private String toCsv(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return String.join(",", list);
    }

    private List<String> fromCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        String[] parts = csv.split(",");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }
}
