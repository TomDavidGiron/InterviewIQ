package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.dto.TagBreakdownDto;
import com.cvoptimizer.cv_backend.interview.persistence.entity.InterviewSessionEntity;
import com.cvoptimizer.cv_backend.interview.persistence.entity.UserSkillScoreEntity;
import com.cvoptimizer.cv_backend.interview.persistence.repo.InterviewSessionRepository;
import com.cvoptimizer.cv_backend.interview.persistence.repo.UserSkillScoreRepository;
import com.cvoptimizer.cv_backend.interview.skillgraph.dto.SkillGraphResponse;
import com.cvoptimizer.cv_backend.interview.skillgraph.dto.SkillScoreDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class SkillGraphService {

    private static final double PREVIOUS_WEIGHT = 0.7;
    private static final double CURRENT_WEIGHT = 0.3;

    private final UserSkillScoreRepository userSkillScoreRepository;
    private final InterviewSessionRepository interviewSessionRepository;

    public SkillGraphService(UserSkillScoreRepository userSkillScoreRepository,
                             InterviewSessionRepository interviewSessionRepository) {
        this.userSkillScoreRepository = userSkillScoreRepository;
        this.interviewSessionRepository = interviewSessionRepository;
    }

    @Transactional
    public void updateAfterInterview(String userId, List<TagBreakdownDto> tagBreakdown) {
        String safeUserId = normalizeUserId(userId);
        if (tagBreakdown == null || tagBreakdown.isEmpty()) {
            return;
        }

        Instant now = Instant.now();

        for (TagBreakdownDto tag : tagBreakdown) {
            if (tag == null || tag.getTag() == null || tag.getTag().isBlank()) {
                continue;
            }

            int currentScore = clamp(tag.getAvgScore());

            Optional<UserSkillScoreEntity> existingOpt =
                    userSkillScoreRepository.findByUserIdAndSkill(safeUserId, tag.getTag());

            UserSkillScoreEntity entity = existingOpt.orElseGet(UserSkillScoreEntity::new);
            entity.setUserId(safeUserId);
            entity.setSkill(tag.getTag());

            if (existingOpt.isPresent() && existingOpt.get().getScore() != null) {
                int previous = clamp(existingOpt.get().getScore());
                int merged = (int) Math.round((previous * PREVIOUS_WEIGHT) + (currentScore * CURRENT_WEIGHT));
                entity.setScore(clamp(merged));
            } else {
                entity.setScore(currentScore);
            }

            entity.setLastUpdated(now);
            userSkillScoreRepository.save(entity);
        }
    }

    @Transactional(readOnly = true)
    public SkillGraphResponse getSkillGraph(String userId) {
        String safeUserId = normalizeUserId(userId);
        List<UserSkillScoreEntity> entities = userSkillScoreRepository.findByUserIdOrderByScoreDescSkillAsc(safeUserId);

        List<SkillScoreDto> skills = new ArrayList<>();
        for (UserSkillScoreEntity entity : entities) {
            skills.add(new SkillScoreDto(
                    entity.getSkill(),
                    clamp(entity.getScore() == null ? 0 : entity.getScore()),
                    entity.getLastUpdated()
            ));
        }

        int overallAverage = 0;
        if (!skills.isEmpty()) {
            int sum = 0;
            for (SkillScoreDto s : skills) {
                sum += s.getScore();
            }
            overallAverage = Math.round(sum / (float) skills.size());
        }

        List<String> strongest = skills.stream()
                .sorted(Comparator.comparingInt(SkillScoreDto::getScore).reversed())
                .limit(3)
                .map(SkillScoreDto::getSkill)
                .toList();

        List<String> weakest = skills.stream()
                .sorted(Comparator.comparingInt(SkillScoreDto::getScore))
                .limit(3)
                .map(SkillScoreDto::getSkill)
                .toList();

        return new SkillGraphResponse(
                safeUserId,
                overallAverage,
                skills,
                strongest,
                weakest
        );
    }

    @Transactional(readOnly = true)
    public SkillGraphResponse getSkillGraphBySessionId(String sessionId) {
        InterviewSessionEntity session = interviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        return getSkillGraph(session.getUserId());
    }

    private String normalizeUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return "anonymous";
        }
        return userId.trim();
    }

    private int clamp(int score) {
        if (score < 0) return 0;
        if (score > 100) return 100;
        return score;
    }
}