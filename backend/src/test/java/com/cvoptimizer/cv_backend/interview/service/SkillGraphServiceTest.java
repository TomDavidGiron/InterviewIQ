package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.dto.TagBreakdownDto;
import com.cvoptimizer.cv_backend.interview.persistence.entity.InterviewSessionEntity;
import com.cvoptimizer.cv_backend.interview.persistence.entity.UserSkillScoreEntity;
import com.cvoptimizer.cv_backend.interview.persistence.repo.InterviewSessionRepository;
import com.cvoptimizer.cv_backend.interview.persistence.repo.UserSkillScoreRepository;
import com.cvoptimizer.cv_backend.interview.skillgraph.dto.SkillGraphResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillGraphServiceTest {

    @Mock private UserSkillScoreRepository skillScoreRepo;
    @Mock private InterviewSessionRepository sessionRepo;

    private SkillGraphService service;

    @BeforeEach
    void setUp() {
        service = new SkillGraphService(skillScoreRepo, sessionRepo);
    }

    // --- updateAfterInterview ---

    @Test
    void updateAfterInterview_newSkill_savedDirectly() {
        when(skillScoreRepo.findByUserIdAndSkill(eq("user1"), eq("java"))).thenReturn(Optional.empty());

        service.updateAfterInterview("user1", List.of(tag("java", 80)));

        ArgumentCaptor<UserSkillScoreEntity> captor = ArgumentCaptor.forClass(UserSkillScoreEntity.class);
        verify(skillScoreRepo).save(captor.capture());
        assertThat(captor.getValue().getSkill()).isEqualTo("java");
        assertThat(captor.getValue().getScore()).isEqualTo(80);
    }

    @Test
    void updateAfterInterview_existingSkill_weightedAverage() {
        UserSkillScoreEntity existing = skillEntity("java", 100);
        when(skillScoreRepo.findByUserIdAndSkill(eq("user1"), eq("java"))).thenReturn(Optional.of(existing));

        service.updateAfterInterview("user1", List.of(tag("java", 40)));

        // expected: round(100 * 0.7 + 40 * 0.3) = round(70 + 12) = 82
        ArgumentCaptor<UserSkillScoreEntity> captor = ArgumentCaptor.forClass(UserSkillScoreEntity.class);
        verify(skillScoreRepo).save(captor.capture());
        assertThat(captor.getValue().getScore()).isEqualTo(82);
    }

    @Test
    void updateAfterInterview_nullTag_skipped() {
        service.updateAfterInterview("user1", List.of(tag(null, 70)));
        verify(skillScoreRepo, never()).save(any());
    }

    @Test
    void updateAfterInterview_emptyBreakdown_nothingSaved() {
        service.updateAfterInterview("user1", List.of());
        verifyNoInteractions(skillScoreRepo);
    }

    @Test
    void updateAfterInterview_nullUserId_usesAnonymous() {
        when(skillScoreRepo.findByUserIdAndSkill(eq("anonymous"), eq("spring"))).thenReturn(Optional.empty());
        service.updateAfterInterview(null, List.of(tag("spring", 60)));

        ArgumentCaptor<UserSkillScoreEntity> captor = ArgumentCaptor.forClass(UserSkillScoreEntity.class);
        verify(skillScoreRepo).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo("anonymous");
    }

    // --- getSkillGraph ---

    @Test
    void getSkillGraph_computesCorrectAverage() {
        when(skillScoreRepo.findByUserIdOrderByScoreDescSkillAsc("user1"))
                .thenReturn(List.of(skillEntity("java", 80), skillEntity("sql", 60)));

        SkillGraphResponse graph = service.getSkillGraph("user1");

        assertThat(graph.getOverallAverage()).isEqualTo(70);
        assertThat(graph.getSkills()).hasSize(2);
    }

    @Test
    void getSkillGraph_strongestAndWeakest_correctlyOrdered() {
        when(skillScoreRepo.findByUserIdOrderByScoreDescSkillAsc("user1"))
                .thenReturn(List.of(
                        skillEntity("java", 90),
                        skillEntity("sql", 70),
                        skillEntity("docker", 40),
                        skillEntity("k8s", 30)
                ));

        SkillGraphResponse graph = service.getSkillGraph("user1");

        assertThat(graph.getStrongestSkills()).containsExactly("java", "sql", "docker");
        assertThat(graph.getWeakestSkills()).containsExactly("k8s", "docker", "sql");
    }

    @Test
    void getSkillGraph_empty_returnsZeroAverage() {
        when(skillScoreRepo.findByUserIdOrderByScoreDescSkillAsc("user1")).thenReturn(List.of());
        SkillGraphResponse graph = service.getSkillGraph("user1");
        assertThat(graph.getOverallAverage()).isEqualTo(0);
        assertThat(graph.getSkills()).isEmpty();
    }

    // --- getSkillGraphBySessionId ---

    @Test
    void getSkillGraphBySessionId_delegatesToGetSkillGraph() {
        InterviewSessionEntity session = new InterviewSessionEntity();
        session.setUserId("user1");
        when(sessionRepo.findById("session-abc")).thenReturn(Optional.of(session));
        when(skillScoreRepo.findByUserIdOrderByScoreDescSkillAsc("user1"))
                .thenReturn(List.of(skillEntity("java", 75)));

        SkillGraphResponse graph = service.getSkillGraphBySessionId("session-abc");

        assertThat(graph.getUserId()).isEqualTo("user1");
        assertThat(graph.getSkills()).hasSize(1);
    }

    // --- resetSkillGraph ---

    @Test
    void resetSkillGraph_callsDeleteByUserId() {
        service.resetSkillGraph("user1");
        verify(skillScoreRepo).deleteByUserId("user1");
    }

    @Test
    void resetSkillGraph_nullUserId_deletesAnonymous() {
        service.resetSkillGraph(null);
        verify(skillScoreRepo).deleteByUserId("anonymous");
    }

    // --- helpers ---

    private TagBreakdownDto tag(String tag, int avgScore) {
        TagBreakdownDto dto = new TagBreakdownDto();
        dto.setTag(tag);
        dto.setAvgScore(avgScore);
        return dto;
    }

    private UserSkillScoreEntity skillEntity(String skill, int score) {
        UserSkillScoreEntity e = new UserSkillScoreEntity();
        e.setUserId("user1");
        e.setSkill(skill);
        e.setScore(score);
        e.setLastUpdated(Instant.now());
        return e;
    }
}
