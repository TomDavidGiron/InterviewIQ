package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.model.InterviewSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InterviewSessionStore {

    private final Map<String, InterviewSession> sessions = new ConcurrentHashMap<>();

    // TTL default: 60 minutes
    @Value("${interview.session.ttl-ms:3600000}")
    private long ttlMs;

    // Cleanup default: every 5 minutes
    // Note: fixedDelay means "wait X ms after the previous cleanup finished"
    @Scheduled(fixedDelayString = "${interview.session.cleanup-interval-ms:300000}")
    public void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        sessions.entrySet().removeIf(e -> isExpired(e.getValue(), now));
    }

    public void put(InterviewSession session) {
        if (session == null || session.getSessionId() == null) return;
        touch(session);
        sessions.put(session.getSessionId(), session);
    }

    public InterviewSession get(String sessionId) {
        if (sessionId == null) return null;
        InterviewSession s = sessions.get(sessionId);
        if (s != null) {
            touch(s);
        }
        return s;
    }

    public void remove(String sessionId) {
        if (sessionId == null) return;
        sessions.remove(sessionId);
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
        if (last == 0) return false; // if somehow missing timestamps, don't delete
        return (now - last) > ttlMs;
    }
}
