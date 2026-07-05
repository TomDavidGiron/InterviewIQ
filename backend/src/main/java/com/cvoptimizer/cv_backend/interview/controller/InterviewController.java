package com.cvoptimizer.cv_backend.interview.controller;

import com.cvoptimizer.cv_backend.interview.dto.*;
import com.cvoptimizer.cv_backend.interview.service.InterviewEngineService;
import com.cvoptimizer.cv_backend.interview.service.InterviewHistoryService;
import com.cvoptimizer.cv_backend.interview.skillgraph.dto.SkillGraphResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cvoptimizer.cv_backend.interview.service.SkillGraphService;

import java.util.List;

@RestController
@RequestMapping("/api/interview")
@Tag(name = "Interview", description = "Core interview flow — start, answer, summarise, history")
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
    @Operation(summary = "Get skill graph for a user", description = "Returns per-tag scores aggregated across all sessions for the given user ID.")
    public ResponseEntity<SkillGraphResponse> skillGraphByUser(@PathVariable String userId) {
        return ResponseEntity.ok(skillGraphService.getSkillGraph(userId));
    }

    @DeleteMapping("/users/{userId}/skill-graph")
    @Operation(summary = "Reset skill graph", description = "Deletes all skill score history for the given user ID.")
    public ResponseEntity<Void> resetSkillGraph(@PathVariable String userId) {
        skillGraphService.resetSkillGraph(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sessionId}/skill-graph")
    @Operation(summary = "Get skill graph by session", description = "Returns per-tag scores for the user who owns the given session.")
    public ResponseEntity<SkillGraphResponse> skillGraphBySession(@PathVariable String sessionId) {
        return ResponseEntity.ok(skillGraphService.getSkillGraphBySessionId(sessionId));
    }

    @PostMapping("/start")
    @Operation(summary = "Start a general interview", description = "Creates a new 10-question session. Optionally filter by topic. Returns session ID and first question.")
    public ResponseEntity<InterviewStartResponse> start(@Valid @RequestBody InterviewStartRequest request) {
        return ResponseEntity.ok(engine.start(request));
    }

    @PostMapping("/job-specific")
    @Operation(summary = "Start a job-specific interview", description = "Extracts skills from a job URL, pasted text, or OCR output and tailors the question set to that role.")
    public ResponseEntity<InterviewStartResponse> startJobSpecific(@Valid @RequestBody InterviewStartRequest request) {
        return ResponseEntity.ok(engine.startJobSpecificInterview(request));
    }

    @PostMapping("/{sessionId}/answer")
    @Operation(summary = "Submit an answer", description = "Evaluates the answer (AI or keyword), updates the skill graph, runs the adaptive agent, and returns the next question or a finish signal.")
    public ResponseEntity<InterviewAnswerResponse> answer(
            @PathVariable String sessionId,
            @Valid @RequestBody InterviewAnswerRequest request
    ) {
        return ResponseEntity.ok(engine.answer(sessionId, request));
    }

    @GetMapping("/{sessionId}/summary")
    @Operation(summary = "Get session summary", description = "Returns overall score, AI diagnosis, study plan, strengths, weaknesses, and weak tags. Triggers AI feedback generation on first call.")
    public ResponseEntity<InterviewSummaryResponse> summary(@PathVariable String sessionId) {
        return ResponseEntity.ok(engine.summary(sessionId));
    }

    @GetMapping("/history")
    @Operation(summary = "Get interview history", description = "Returns paginated past sessions for the given userId, most recent first.")
    public ResponseEntity<List<InterviewHistoryItemDto>> history(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Browser guest ID from localStorage") @RequestParam(required = false) String userId
    ) {
        return ResponseEntity.ok(history.getHistory(userId, page, size));
    }

    @GetMapping("/{sessionId}/details")
    @Operation(summary = "Get per-question attempt breakdown", description = "Returns each question, the user's answer, score, feedback, and missing keywords for the session.")
    public ResponseEntity<List<InterviewAttemptDto>> details(@PathVariable String sessionId) {
        return ResponseEntity.ok(history.getSessionDetails(sessionId));
    }

    @GetMapping("/topics")
    @Operation(summary = "List available topics", description = "Returns all distinct question topics that can be passed to /start.")
    public ResponseEntity<List<String>> topics() {
        return ResponseEntity.ok(engine.getAvailableTopics());
    }
}
