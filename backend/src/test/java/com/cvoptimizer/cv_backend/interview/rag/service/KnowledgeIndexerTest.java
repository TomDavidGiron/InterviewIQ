package com.cvoptimizer.cv_backend.interview.rag.service;

import com.cvoptimizer.cv_backend.interview.rag.config.RagProperties;
import com.cvoptimizer.cv_backend.interview.rag.repository.KnowledgeChunkVectorRepository;
import com.cvoptimizer.cv_backend.interview.rag.repository.KnowledgeChunkVectorRepository.IndexState;
import com.cvoptimizer.cv_backend.interview.rag.service.KnowledgeNoteLoader.KnowledgeNote;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KnowledgeIndexerTest {

    private static final String MODEL = "text-embedding-3-small";

    private final KnowledgeNoteLoader loader = mock(KnowledgeNoteLoader.class);
    private final KnowledgeChunkVectorRepository repository = mock(KnowledgeChunkVectorRepository.class);
    private final EmbeddingService embeddings = mock(EmbeddingService.class);
    private KnowledgeIndexer indexer;

    private final KnowledgeNote indexes = note("note:sql:indexes", "hash-indexes");
    private final KnowledgeNote joins = note("note:sql:joins", "hash-joins");

    @BeforeEach
    void setUp() {
        indexer = new KnowledgeIndexer(loader, repository, embeddings, new RagProperties(), new ObjectMapper());
        when(embeddings.modelName()).thenReturn(MODEL);
        when(embeddings.embedBatch(anyList())).thenAnswer(inv -> {
            List<String> texts = inv.getArgument(0);
            return texts.stream().map(t -> new float[]{1f}).toList();
        });
        when(loader.loadAll()).thenReturn(List.of(indexes, joins));
    }

    @Test
    void newNotes_areEmbeddedInOneBatchAndStored() {
        when(repository.findIndexState(KnowledgeIndexer.SOURCE_TYPE)).thenReturn(Map.of());

        KnowledgeIndexer.IndexResult result = indexer.index();

        assertThat(result.embedded()).isEqualTo(2);
        verify(embeddings, times(1)).embedBatch(List.of(indexes.content(), joins.content()));
        verify(repository).upsertChunk(eq("note:sql:indexes"), anyString(), eq("sql"), eq(List.of("sql")),
                anyString(), eq(KnowledgeIndexer.SOURCE_TYPE), anyString(), eq("hash-indexes"), eq(MODEL), any());
    }

    @Test
    void unchangedNotes_areSkipped_andOnlyEditedOnesReembedded() {
        when(repository.findIndexState(KnowledgeIndexer.SOURCE_TYPE)).thenReturn(Map.of(
                "note:sql:indexes", new IndexState("hash-indexes", MODEL),
                "note:sql:joins", new IndexState("old-hash", MODEL)));

        KnowledgeIndexer.IndexResult result = indexer.index();

        assertThat(result.embedded()).isEqualTo(1);
        verify(embeddings).embedBatch(List.of(joins.content()));
    }

    @Test
    void notesEmbeddedWithAnotherModel_areReembedded() {
        when(repository.findIndexState(KnowledgeIndexer.SOURCE_TYPE)).thenReturn(Map.of(
                "note:sql:indexes", new IndexState("hash-indexes", "some-old-model"),
                "note:sql:joins", new IndexState("hash-joins", MODEL)));

        assertThat(indexer.index().embedded()).isEqualTo(1);
        verify(embeddings).embedBatch(List.of(indexes.content()));
    }

    @Test
    void notesDeletedFromFiles_areRemoved() {
        when(repository.findIndexState(KnowledgeIndexer.SOURCE_TYPE)).thenReturn(Map.of(
                "note:sql:indexes", new IndexState("hash-indexes", MODEL),
                "note:sql:joins", new IndexState("hash-joins", MODEL),
                "note:sql:removed-section", new IndexState("x", MODEL)));

        KnowledgeIndexer.IndexResult result = indexer.index();

        assertThat(result.embedded()).isZero();
        assertThat(result.removed()).isEqualTo(1);
        verify(repository).deleteByChunkKeys(Set.of("note:sql:removed-section"));
        verify(embeddings, never()).embedBatch(anyList());
    }

    @Test
    void embeddingFailure_storesNothing() {
        when(repository.findIndexState(KnowledgeIndexer.SOURCE_TYPE)).thenReturn(Map.of());
        when(embeddings.embedBatch(anyList())).thenThrow(new IllegalStateException("429 insufficient_quota"));

        assertThatThrownBy(() -> indexer.index()).hasMessageContaining("insufficient_quota");
        verify(repository, never()).upsertChunk(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    private static KnowledgeNote note(String key, String hash) {
        return new KnowledgeNote(key, "sql", List.of("sql"), "SQL", "content of " + key, hash);
    }
}
