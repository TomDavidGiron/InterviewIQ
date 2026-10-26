package com.cvoptimizer.cv_backend.interview.rag.repository;

import com.cvoptimizer.cv_backend.interview.rag.model.KnowledgeChunk;
import com.pgvector.PGvector;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class KnowledgeChunkVectorRepository {

    private final JdbcTemplate jdbcTemplate;

    public KnowledgeChunkVectorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** What is already stored for a chunk, so the indexer can skip unchanged ones. */
    public record IndexState(String contentHash, String embeddingModel) {}

    public void upsertChunk(
            String chunkKey,
            String content,
            String topic,
            List<String> tags,
            String source,
            String sourceType,
            String metadataJson,
            String contentHash,
            String embeddingModel,
            float[] embedding
    ) {
        String sql = """
            INSERT INTO knowledge_chunks
            (chunk_key, content, topic, tags, source, source_type, metadata_json, content_hash, embedding_model, embedding)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (chunk_key)
            DO UPDATE SET
                content = EXCLUDED.content,
                topic = EXCLUDED.topic,
                tags = EXCLUDED.tags,
                source = EXCLUDED.source,
                source_type = EXCLUDED.source_type,
                metadata_json = EXCLUDED.metadata_json,
                content_hash = EXCLUDED.content_hash,
                embedding_model = EXCLUDED.embedding_model,
                embedding = EXCLUDED.embedding
            """;

        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql);
            ps.setString(1, chunkKey);
            ps.setString(2, content);
            ps.setString(3, topic);
            ps.setArray(4, connection.createArrayOf("text", tags.toArray(new String[0])));
            ps.setString(5, source);
            ps.setString(6, sourceType);
            ps.setString(7, metadataJson);
            ps.setString(8, contentHash);
            ps.setString(9, embeddingModel);
            ps.setObject(10, new PGvector(embedding));
            return ps;
        });
    }

    public Map<String, IndexState> findIndexState(String sourceType) {
        Map<String, IndexState> state = new HashMap<>();
        jdbcTemplate.query(
                "SELECT chunk_key, content_hash, embedding_model FROM knowledge_chunks WHERE source_type = ?",
                rs -> {
                    state.put(rs.getString("chunk_key"),
                            new IndexState(rs.getString("content_hash"), rs.getString("embedding_model")));
                },
                sourceType
        );
        return state;
    }

    public int deleteByChunkKeys(Collection<String> chunkKeys) {
        if (chunkKeys == null || chunkKeys.isEmpty()) {
            return 0;
        }
        return jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement("DELETE FROM knowledge_chunks WHERE chunk_key = ANY (?)");
            ps.setArray(1, connection.createArrayOf("text", chunkKeys.toArray(new String[0])));
            return ps;
        });
    }

    public List<KnowledgeChunk> searchSimilar(float[] queryEmbedding, int limit) {
        String sql = """
            SELECT
                id,
                chunk_key,
                content,
                topic,
                tags,
                difficulty,
                source,
                source_type,
                metadata_json,
                1 - (embedding <=> ?) AS similarity
            FROM knowledge_chunks
            ORDER BY embedding <=> ?
            LIMIT ?
            """;

        PGvector vector = new PGvector(queryEmbedding);

        return jdbcTemplate.query(sql, new KnowledgeChunkRowMapper(), vector, vector, limit);
    }

    public int count() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM knowledge_chunks", Integer.class);
        return count == null ? 0 : count;
    }

    private static class KnowledgeChunkRowMapper implements RowMapper<KnowledgeChunk> {
        @Override
        public KnowledgeChunk mapRow(ResultSet rs, int rowNum) throws SQLException {
            KnowledgeChunk chunk = new KnowledgeChunk(
                    rs.getLong("id"),
                    rs.getString("chunk_key"),
                    rs.getString("content"),
                    rs.getString("topic"),
                    rs.getString("difficulty"),
                    rs.getString("source"),
                    rs.getString("source_type"),
                    rs.getString("metadata_json"),
                    rs.getDouble("similarity")
            );
            chunk.setTags(toList(rs.getArray("tags")));
            return chunk;
        }

        private List<String> toList(Array array) throws SQLException {
            if (array == null) {
                return List.of();
            }
            return Arrays.asList((String[]) array.getArray());
        }
    }
}
