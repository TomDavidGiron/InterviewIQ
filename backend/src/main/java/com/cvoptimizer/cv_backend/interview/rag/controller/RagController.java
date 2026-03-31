package com.cvoptimizer.cv_backend.interview.rag.controller;

import com.cvoptimizer.cv_backend.interview.rag.dto.KnowledgeChunkDto;
import com.cvoptimizer.cv_backend.interview.rag.dto.RagSearchRequest;
import com.cvoptimizer.cv_backend.interview.rag.dto.RagSearchResponse;
import com.cvoptimizer.cv_backend.interview.rag.model.KnowledgeChunk;
import com.cvoptimizer.cv_backend.interview.rag.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/search")
    public ResponseEntity<RagSearchResponse> search(@RequestBody RagSearchRequest request) {
        String query = request == null ? null : request.getQuery();
        Integer limit = request == null ? null : request.getLimit();

        List<KnowledgeChunk> chunks = ragService.retrieveRelevantChunks(query, limit);

        List<KnowledgeChunkDto> chunkDtos = chunks.stream()
                .map(chunk -> new KnowledgeChunkDto(
                        chunk.getId(),
                        chunk.getContent(),
                        chunk.getTopic(),
                        chunk.getDifficulty(),
                        chunk.getSource(),
                        chunk.getSourceType(),
                        chunk.getSimilarity()
                ))
                .toList();

        List<String> context = chunks.stream()
                .map(KnowledgeChunk::getContent)
                .toList();

        RagSearchResponse response = new RagSearchResponse(query, chunkDtos, context);
        return ResponseEntity.ok(response);
    }
}