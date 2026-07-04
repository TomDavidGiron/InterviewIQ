package com.cvoptimizer.cv_backend.interview.controller;

import com.cvoptimizer.cv_backend.interview.dto.*;
import com.cvoptimizer.cv_backend.interview.service.InterviewEngineService;
import com.cvoptimizer.cv_backend.interview.service.InterviewHistoryService;
import com.cvoptimizer.cv_backend.interview.skillgraph.dto.SkillGraphResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cvoptimizer.cv_backend.interview.service.SkillGraphService;

import java.util.List;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewEngineService engine;
    private final InterviewHistoryService history;
    private final SkillGraphService skillGraphService;

    public InterviewController(InterviewEngineService engine, InterviewHistoryService history, SkillGraphService skillGraphService) {
        this.engine = engine;
        this.history = history;
        this.skillGraphService = skillGraphService;
    }

    @GetMapping("/users/{userId}/skill-graph")
    public ResponseEntity<SkillGraphResponse> skillGraphByUser(@PathVariable String userId) {
        return ResponseEntity.ok(skillGraphService.getSkillGraph(userId));
    }

    @DeleteMapping("/users/{userId}/skill-graph")
    public ResponseEntity<Void> resetSkillGraph(@PathVariable String userId) {
        skillGraphService.resetSkillGraph(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sessionId}/skill-graph")
    public ResponseEntity<SkillGraphResponse> skillGraphBySession(@PathVariable String sessionId) {
        return ResponseEntity.ok(skillGraphService.getSkillGraphBySessionId(sessionId));
    }

    @PostMapping("/start")
    public ResponseEntity<InterviewStartResponse> start(@Valid @RequestBody InterviewStartRequest request) {
        return ResponseEntity.ok(engine.start(request));
    }

    @PostMapping("/job-specific")
    public ResponseEntity<InterviewStartResponse> startJobSpecific(@Valid @RequestBody InterviewStartRequest request) {
        return ResponseEntity.ok(engine.startJobSpecificInterview(request));
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<InterviewAnswerResponse> answer(
            @PathVariable String sessionId,
            @Valid @RequestBody InterviewAnswerRequest request
    ) {
        return ResponseEntity.ok(engine.answer(sessionId, request));
    }

    @GetMapping("/{sessionId}/summary")
    public ResponseEntity<InterviewSummaryResponse> summary(@PathVariable String sessionId) {
        return ResponseEntity.ok(engine.summary(sessionId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<InterviewHistoryItemDto>> history(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String userId
    ) {
        return ResponseEntity.ok(history.getHistory(userId, page, size));
    }

    @GetMapping("/{sessionId}/details")
    public ResponseEntity<List<InterviewAttemptDto>> details(@PathVariable String sessionId) {
        return ResponseEntity.ok(history.getSessionDetails(sessionId));
    }

    @GetMapping("/topics")
    public ResponseEntity<List<String>> topics() {
        return ResponseEntity.ok(engine.getAvailableTopics());
    }
}
