package com.cvoptimizer.cv_backend.interview.rag.service;

import com.cvoptimizer.cv_backend.interview.rag.config.RagProperties;
import com.cvoptimizer.cv_backend.interview.rag.model.KnowledgeChunk;
import com.cvoptimizer.cv_backend.interview.rag.repository.KnowledgeChunkVectorRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RagService {

    // Fetch extra candidates so topic preference and the similarity floor still leave enough results
    private static final int CANDIDATE_MULTIPLIER = 3;

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
        return retrieveRelevantChunks(query, Set.of(), limit);
    }

    /**
     * Returns up to {@code limit} chunks similar to the query, dropping any below the configured
     * similarity floor (so unrelated notes are never injected into a prompt) and ranking chunks
     * tagged with one of {@code preferredTags} ahead of equally relevant chunks from other topics.
     */
    public List<KnowledgeChunk> retrieveRelevantChunks(String query, Collection<String> preferredTags, Integer limit) {
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
        List<KnowledgeChunk> candidates = repository.searchSimilar(embedding, actualLimit * CANDIDATE_MULTIPLIER);

        Set<String> tags = normalize(preferredTags);
        double minSimilarity = ragProperties.getSearchMinSimilarity();

        return candidates.stream()
                .filter(chunk -> chunk.getSimilarity() >= minSimilarity)
                // stable sort: keeps similarity order within the on-topic and off-topic groups
                .sorted(Comparator.comparing((KnowledgeChunk chunk) -> !matchesAny(chunk, tags)))
                .limit(actualLimit)
                .toList();
    }

    public List<String> retrieveContext(String query, Integer limit) {
        return retrieveContext(query, Set.of(), limit);
    }

    public List<String> retrieveContext(String query, Collection<String> preferredTags, Integer limit) {
        return retrieveRelevantChunks(query, preferredTags, limit)
                .stream()
                .map(KnowledgeChunk::getContent)
                .toList();
    }

    public int countChunks() {
        return repository.count();
    }

    private boolean matchesAny(KnowledgeChunk chunk, Set<String> tags) {
        if (tags.isEmpty() || chunk.getTags() == null) {
            return false;
        }
        return chunk.getTags().stream().anyMatch(tag -> tags.contains(tag.toLowerCase(Locale.ROOT)));
    }

    private Set<String> normalize(Collection<String> tags) {
        if (tags == null) {
            return Set.of();
        }
        return tags.stream()
                .filter(t -> t != null && !t.isBlank())
                .map(t -> t.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }
}
