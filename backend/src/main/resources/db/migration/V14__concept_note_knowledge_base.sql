-- The RAG knowledge base now comes from curated concept notes (resources/knowledge/*.md).
-- The old chunks were the question bank itself, so retrieval mostly returned the question being
-- asked, and some may hold hash-placeholder vectors written while no working API key was available.
DELETE FROM knowledge_chunks WHERE source_type = 'question_bank';

-- Tags let retrieval prefer notes about the question's topic.
ALTER TABLE knowledge_chunks ADD COLUMN IF NOT EXISTS tags TEXT[] NOT NULL DEFAULT '{}';

-- content_hash + embedding_model let the indexer re-embed only notes that changed or were
-- embedded with a different model, instead of re-indexing everything on each startup.
ALTER TABLE knowledge_chunks ADD COLUMN IF NOT EXISTS content_hash VARCHAR(64);
ALTER TABLE knowledge_chunks ADD COLUMN IF NOT EXISTS embedding_model VARCHAR(100);
