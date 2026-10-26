package com.cvoptimizer.cv_backend.interview.rag.service;

import com.cvoptimizer.cv_backend.interview.rag.config.RagProperties;
import com.cvoptimizer.cv_backend.interview.rag.repository.KnowledgeChunkVectorRepository;
import com.cvoptimizer.cv_backend.interview.rag.repository.KnowledgeChunkVectorRepository.IndexState;
import com.cvoptimizer.cv_backend.interview.rag.service.KnowledgeNoteLoader.KnowledgeNote;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Keeps knowledge_chunks in sync with the concept notes: on startup it embeds only notes that are
 * new, edited, or were embedded with a different model, and removes notes deleted from the files.
 */
@Component
public class KnowledgeIndexer {

    static final String SOURCE_TYPE = "concept_note";
    private static final String SOURCE = "knowledge_notes";
    private static final int BATCH_SIZE = 100;

    private final KnowledgeNoteLoader loader;
    private final KnowledgeChunkVectorRepository repository;
    private final EmbeddingService embeddingService;
    private final RagProperties ragProperties;
    private final ObjectMapper objectMapper;

    public record IndexResult(int notes, int embedded, int removed) {}

    public KnowledgeIndexer(KnowledgeNoteLoader loader,
                            KnowledgeChunkVectorRepository repository,
                            EmbeddingService embeddingService,
                            RagProperties ragProperties,
                            ObjectMapper objectMapper) {
        this.loader = loader;
        this.repository = repository;
        this.embeddingService = embeddingService;
        this.ragProperties = ragProperties;
        this.objectMapper = objectMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void indexOnStartup() {
        if (!ragProperties.isBootstrapEnabled()) {
            return;
        }
        if (!embeddingService.isConfigured()) {
            System.out.println("[RAG_INDEX] No OpenAI API key — skipping knowledge indexing (RAG disabled)");
            return;
        }
        // Off the startup thread, so a slow embeddings call can never delay the health check
        Thread.ofVirtual().name("knowledge-indexer").start(() -> {
            try {
                IndexResult result = index();
                System.out.println("[RAG_INDEX] " + result.notes() + " notes: embedded " + result.embedded()
                        + ", removed " + result.removed() + ", unchanged " + (result.notes() - result.embedded()));
            } catch (RuntimeException e) {
                // Nothing placeholder-ish is stored; notes that weren't embedded are retried next startup
                System.err.println("[RAG_INDEX] Knowledge indexing failed: " + e.getMessage());
            }
        });
    }

    public IndexResult index() {
        List<KnowledgeNote> notes = loader.loadAll();
        Map<String, IndexState> existing = repository.findIndexState(SOURCE_TYPE);
        String model = embeddingService.modelName();

        List<KnowledgeNote> toEmbed = notes.stream()
                .filter(note -> !isCurrent(existing.get(note.chunkKey()), note, model))
                .toList();

        for (int start = 0; start < toEmbed.size(); start += BATCH_SIZE) {
            List<KnowledgeNote> batch = toEmbed.subList(start, Math.min(start + BATCH_SIZE, toEmbed.size()));
            List<float[]> vectors = embeddingService.embedBatch(batch.stream().map(KnowledgeNote::content).toList());
            for (int i = 0; i < batch.size(); i++) {
                KnowledgeNote note = batch.get(i);
                repository.upsertChunk(note.chunkKey(), note.content(), note.topic(), note.tags(), SOURCE,
                        SOURCE_TYPE, metadata(note), note.contentHash(), model, vectors.get(i));
            }
        }

        Set<String> currentKeys = notes.stream().map(KnowledgeNote::chunkKey).collect(Collectors.toSet());
        Set<String> removedKeys = new HashSet<>(existing.keySet());
        removedKeys.removeAll(currentKeys);
        repository.deleteByChunkKeys(removedKeys);

        return new IndexResult(notes.size(), toEmbed.size(), removedKeys.size());
    }

    private boolean isCurrent(IndexState state, KnowledgeNote note, String model) {
        return state != null
                && note.contentHash().equals(state.contentHash())
                && model.equals(state.embeddingModel());
    }

    private String metadata(KnowledgeNote note) {
        try {
            return objectMapper.writeValueAsString(Map.of("title", note.title()));
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
