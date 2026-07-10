package com.cvoptimizer.cv_backend.interview.rag.service;

import com.cvoptimizer.cv_backend.interview.rag.config.RagProperties;
import com.cvoptimizer.cv_backend.interview.rag.model.KnowledgeChunk;
import com.cvoptimizer.cv_backend.interview.rag.repository.KnowledgeChunkVectorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private final EmbeddingService embeddingService;
    private final KnowledgeChunkVectorRepository repository;
    private final RagProperties ragProperties;

    public RagService(
            EmbeddingService embeddingService,
            KnowledgeChunkVectorRepository repository,
            RagProperties ragProperties
    ) {
        this.embeddingService = embeddingService;
        this.repository = repository;
        this.ragProperties = ragProperties;
    }

    public List<KnowledgeChunk> retrieveRelevantChunks(String query, Integer limit) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        if (!embeddingService.isConfigured()) {
            // Hash embeddings produce meaningless similarity scores — skip RAG entirely.
            return List.of();
        }

        int actualLimit = (limit == null || limit <= 0)
                ? ragProperties.getSearchDefaultLimit()
                : limit;

        float[] embedding = embeddingService.embed(query);
        return repository.searchSimilar(embedding, actualLimit);
    }

    public List<String> retrieveContext(String query, Integer limit) {
        return retrieveRelevantChunks(query, limit)
                .stream()
                .map(KnowledgeChunk::getContent)
                .toList();
    }

    public void indexChunk(
            String chunkKey,
            String content,
            String topic,
            String difficulty,
            String source,
            String sourceType,
            String metadataJson
    ) {
        float[] embedding = embeddingService.embed(content);

        repository.upsertChunk(
                chunkKey,
                content,
                topic,
                difficulty,
                source,
                sourceType,
                metadataJson,
                embedding
        );
    }

    public int countChunks() {
        return repository.count();
    }
}