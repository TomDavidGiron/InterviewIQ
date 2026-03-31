package com.cvoptimizer.cv_backend.interview.rag.repository;

import com.cvoptimizer.cv_backend.interview.rag.model.KnowledgeChunk;
import com.pgvector.PGvector;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class KnowledgeChunkVectorRepository {

    private final JdbcTemplate jdbcTemplate;

    public KnowledgeChunkVectorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsertChunk(
            String chunkKey,
            String content,
            String topic,
            String difficulty,
            String source,
            String sourceType,
            String metadataJson,
            float[] embedding
    ) {
        String sql = """
            INSERT INTO knowledge_chunks
            (chunk_key, content, topic, difficulty, source, source_type, metadata_json, embedding)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (chunk_key)
            DO UPDATE SET
                content = EXCLUDED.content,
                topic = EXCLUDED.topic,
                difficulty = EXCLUDED.difficulty,
                source = EXCLUDED.source,
                source_type = EXCLUDED.source_type,
                metadata_json = EXCLUDED.metadata_json,
                embedding = EXCLUDED.embedding
            """;

        jdbcTemplate.update(
                sql,
                chunkKey,
                content,
                topic,
                difficulty,
                source,
                sourceType,
                metadataJson,
                new PGvector(embedding)
        );
    }

    public List<KnowledgeChunk> searchSimilar(float[] queryEmbedding, int limit) {
        String sql = """
            SELECT
                id,
                chunk_key,
                content,
                topic,
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
            return new KnowledgeChunk(
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
        }
    }
}