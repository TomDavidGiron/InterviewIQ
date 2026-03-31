CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS knowledge_chunks (
    id BIGSERIAL PRIMARY KEY,
    chunk_key VARCHAR(255) UNIQUE NOT NULL,
    content TEXT NOT NULL,
    topic VARCHAR(100),
    difficulty VARCHAR(50),
    source VARCHAR(100),
    source_type VARCHAR(100),
    metadata_json TEXT,
    embedding vector(1536) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_topic
    ON knowledge_chunks(topic);

CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_source
    ON knowledge_chunks(source);

CREATE INDEX IF NOT EXISTS idx_knowledge_chunks_source_type
    ON knowledge_chunks(source_type);