package com.cvoptimizer.cv_backend.interview.rag;

import com.cvoptimizer.cv_backend.interview.rag.config.RagProperties;
import com.cvoptimizer.cv_backend.interview.rag.model.KnowledgeChunk;
import com.cvoptimizer.cv_backend.interview.rag.repository.KnowledgeChunkVectorRepository;
import com.cvoptimizer.cv_backend.interview.rag.service.EmbeddingService;
import com.cvoptimizer.cv_backend.interview.rag.service.RagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RagServiceTest {

    @Mock private EmbeddingService embeddingService;
    @Mock private KnowledgeChunkVectorRepository repository;
    @Mock private RagProperties ragProperties;

    private RagService service;

    @BeforeEach
    void setUp() {
        service = new RagService(embeddingService, repository, ragProperties);
    }

    @Test
    void retrieveRelevantChunks_blankQuery_returnsEmpty() {
        assertThat(service.retrieveRelevantChunks("  ", 5)).isEmpty();
        assertThat(service.retrieveRelevantChunks(null, 5)).isEmpty();
        verifyNoInteractions(embeddingService, repository);
    }

    @Test
    void retrieveRelevantChunks_embeddingNotConfigured_returnsEmpty() {
        when(embeddingService.isConfigured()).thenReturn(false);
        assertThat(service.retrieveRelevantChunks("What is a HashMap?", 5)).isEmpty();
        verifyNoInteractions(repository);
    }

    @Test
    void retrieveRelevantChunks_configured_callsEmbedAndRepository() {
        when(embeddingService.isConfigured()).thenReturn(true);
        when(ragProperties.getSearchDefaultLimit()).thenReturn(5);
        float[] fakeEmbedding = new float[]{0.1f, 0.2f, 0.3f};
        when(embeddingService.embed("What is a HashMap?")).thenReturn(fakeEmbedding);

        KnowledgeChunk chunk = new KnowledgeChunk();
        chunk.setContent("A HashMap stores key-value pairs using a hash table.");
        when(repository.searchSimilar(fakeEmbedding, 5)).thenReturn(List.of(chunk));

        List<KnowledgeChunk> result = service.retrieveRelevantChunks("What is a HashMap?", null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).contains("HashMap");
        verify(embeddingService).embed("What is a HashMap?");
        verify(repository).searchSimilar(fakeEmbedding, 5);
    }

    @Test
    void retrieveRelevantChunks_customLimit_usedInsteadOfDefault() {
        when(embeddingService.isConfigured()).thenReturn(true);
        when(embeddingService.embed(any())).thenReturn(new float[]{0.5f});
        when(repository.searchSimilar(any(), eq(3))).thenReturn(List.of());

        service.retrieveRelevantChunks("concurrency", 3);

        verify(repository).searchSimilar(any(), eq(3));
        verify(ragProperties, never()).getSearchDefaultLimit();
    }

    @Test
    void retrieveContext_returnsContentStrings() {
        when(embeddingService.isConfigured()).thenReturn(true);
        when(ragProperties.getSearchDefaultLimit()).thenReturn(5);
        when(embeddingService.embed(any())).thenReturn(new float[]{0.1f});

        KnowledgeChunk c1 = new KnowledgeChunk();
        c1.setContent("Transactions ensure ACID properties.");
        KnowledgeChunk c2 = new KnowledgeChunk();
        c2.setContent("Rollback undoes uncommitted changes.");
        when(repository.searchSimilar(any(), anyInt())).thenReturn(List.of(c1, c2));

        List<String> context = service.retrieveContext("What are transactions?", null);

        assertThat(context).containsExactly(
                "Transactions ensure ACID properties.",
                "Rollback undoes uncommitted changes."
        );
    }

    @Test
    void retrieveContext_notConfigured_returnsEmptyStrings() {
        when(embeddingService.isConfigured()).thenReturn(false);
        assertThat(service.retrieveContext("anything", 5)).isEmpty();
    }
}
