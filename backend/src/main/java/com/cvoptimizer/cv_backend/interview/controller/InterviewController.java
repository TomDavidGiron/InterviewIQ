package com.cvoptimizer.cv_backend.interview.controller;

import com.cvoptimizer.cv_backend.interview.dto.*;
import com.cvoptimizer.cv_backend.interview.service.InterviewEngineService;
import com.cvoptimizer.cv_backend.interview.service.InterviewHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewEngineService engine;
    private final InterviewHistoryService history;

    public InterviewController(InterviewEngineService engine, InterviewHistoryService history) {
        this.engine = engine;
        this.history = history;
    }

    @PostMapping("/start")
    public ResponseEntity<InterviewStartResponse> start(@RequestBody InterviewStartRequest request) {
        return ResponseEntity.ok(engine.start(request));
    }

    @PostMapping("/job-specific")
    public ResponseEntity<InterviewStartResponse> startJobSpecific(@RequestBody InterviewStartRequest request) {
        return ResponseEntity.ok(engine.startJobSpecificInterview(request));
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<InterviewAnswerResponse> answer(
            @PathVariable String sessionId,
            @RequestBody InterviewAnswerRequest request
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
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(history.getHistory(page, size));
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