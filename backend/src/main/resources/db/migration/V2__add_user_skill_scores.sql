ALTER TABLE interview_session
    ADD COLUMN IF NOT EXISTS user_id VARCHAR(100);

UPDATE interview_session
SET user_id = 'anonymous'
WHERE user_id IS NULL OR TRIM(user_id) = '';

ALTER TABLE interview_session
    ALTER COLUMN user_id SET NOT NULL;

CREATE TABLE IF NOT EXISTS user_skill_scores (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    skill VARCHAR(120) NOT NULL,
    score INTEGER NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    CONSTRAINT uk_user_skill_scores_user_skill UNIQUE (user_id, skill)
);

CREATE INDEX IF NOT EXISTS idx_user_skill_scores_user_id
    ON user_skill_scores(user_id);

CREATE INDEX IF NOT EXISTS idx_user_skill_scores_user_id_score
    ON user_skill_scores(user_id, score DESC);