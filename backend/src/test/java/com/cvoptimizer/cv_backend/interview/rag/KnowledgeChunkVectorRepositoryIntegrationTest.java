package com.cvoptimizer.cv_backend.interview.rag;

import com.cvoptimizer.cv_backend.TestcontainersConfiguration;
import com.cvoptimizer.cv_backend.interview.rag.model.KnowledgeChunk;
import com.cvoptimizer.cv_backend.interview.rag.repository.KnowledgeChunkVectorRepository;
import com.cvoptimizer.cv_backend.interview.rag.repository.KnowledgeChunkVectorRepository.IndexState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class KnowledgeChunkVectorRepositoryIntegrationTest {

    private static final String SOURCE_TYPE = "concept_note_test";

    @Autowired
    private KnowledgeChunkVectorRepository repository;

    @AfterEach
    void cleanUp() {
        repository.deleteByChunkKeys(repository.findIndexState(SOURCE_TYPE).keySet());
    }

    @Test
    void upsertSearchAndDelete_roundTripAgainstPgvector() {
        repository.upsertChunk("test:indexes", "Indexes speed up lookups", "sql", List.of("sql", "postgresql"),
                "test", SOURCE_TYPE, "{}", "hash-1", "model-a", vector(1f, 0f));
        repository.upsertChunk("test:css", "Flexbox lays out items", "css", List.of("css"),
                "test", SOURCE_TYPE, "{}", "hash-2", "model-a", vector(0f, 1f));

        // Re-upserting the same key updates it in place
        repository.upsertChunk("test:indexes", "Indexes speed up lookups (edited)", "sql", List.of("sql", "postgresql"),
                "test", SOURCE_TYPE, "{}", "hash-1b", "model-a", vector(1f, 0f));

        Map<String, IndexState> state = repository.findIndexState(SOURCE_TYPE);
        assertThat(state).containsEntry("test:indexes", new IndexState("hash-1b", "model-a"));
        assertThat(state).hasSize(2);

        List<KnowledgeChunk> results = repository.searchSimilar(vector(1f, 0f), 1);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getContent()).isEqualTo("Indexes speed up lookups (edited)");
        assertThat(results.get(0).getTags()).containsExactly("sql", "postgresql");
        assertThat(results.get(0).getSimilarity()).isGreaterThan(0.99);

        assertThat(repository.deleteByChunkKeys(List.of("test:css"))).isEqualTo(1);
        assertThat(repository.findIndexState(SOURCE_TYPE)).containsOnlyKeys("test:indexes");
    }

    private float[] vector(float first, float second) {
        float[] v = new float[1536];
        v[0] = first;
        v[1] = second;
        return v;
    }
}
