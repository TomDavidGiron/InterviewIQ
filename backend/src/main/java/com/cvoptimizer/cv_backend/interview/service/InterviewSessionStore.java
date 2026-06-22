package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.model.InterviewSession;
import com.cvoptimizer.cv_backend.interview.persistence.entity.InterviewSessionEntity;
import com.cvoptimizer.cv_backend.interview.persistence.repo.InterviewSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InterviewSessionStore {

    private final Map<String, InterviewSession> sessions = new ConcurrentHashMap<>();
    private final InterviewSessionRepository sessionRepo;
    private final ObjectMapper objectMapper;

    @Value("${interview.session.ttl-ms:3600000}")
    private long ttlMs;

    public InterviewSessionStore(InterviewSessionRepository sessionRepo, ObjectMapper objectMapper) {
        this.sessionRepo = sessionRepo;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${interview.session.cleanup-interval-ms:300000}")
    public void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        sessions.entrySet().removeIf(e -> isExpired(e.getValue(), now));
    }

    public void put(InterviewSession session) {
        if (session == null || session.getSessionId() == null) return;
        touch(session);
        sessions.put(session.getSessionId(), session);
        persistState(session);
    }

    public InterviewSession get(String sessionId) {
        if (sessionId == null) return null;

        InterviewSession s = sessions.get(sessionId);
        if (s != null) {
            touch(s);
            return s;
        }

        // Cache miss — try to restore from DB
        return loadFromDb(sessionId);
    }

    public void remove(String sessionId) {
        if (sessionId == null) return;
        sessions.remove(sessionId);
    }

    private void persistState(InterviewSession session) {
        try {
            String json = objectMapper.writeValueAsString(session);
            Optional<InterviewSessionEntity> opt = sessionRepo.findById(session.getSessionId());
            if (opt.isPresent()) {
                InterviewSessionEntity entity = opt.get();
                entity.setSessionState(json);
                sessionRepo.save(entity);
            }
        } catch (Exception e) {
            System.err.println("[SESSION_STORE] Failed to persist session state: " + e.getMessage());
        }
    }

    private InterviewSession loadFromDb(String sessionId) {
        try {
            Optional<InterviewSessionEntity> opt = sessionRepo.findById(sessionId);
            if (opt.isEmpty()) return null;

            String json = opt.get().getSessionState();
            if (json == null || json.isBlank()) return null;

            InterviewSession restored = objectMapper.readValue(json, InterviewSession.class);
            touch(restored);
            sessions.put(sessionId, restored);
            System.out.println("[SESSION_STORE] Restored session from DB: " + sessionId);
            return restored;
        } catch (Exception e) {
            System.err.println("[SESSION_STORE] Failed to restore session from DB: " + e.getMessage());
            return null;
        }
    }

    private void touch(InterviewSession session) {
        long now = System.currentTimeMillis();
        if (session.getCreatedAtEpochMs() == 0) {
            session.setCreatedAtEpochMs(now);
        }
        session.setLastAccessEpochMs(now);
    }

    private boolean isExpired(InterviewSession session, long now) {
        if (session == null) return true;
        long last = session.getLastAccessEpochMs();
        if (last == 0) last = session.getCreatedAtEpochMs();
        if (last == 0) return false;
        return (now - last) > ttlMs;
    }
}
