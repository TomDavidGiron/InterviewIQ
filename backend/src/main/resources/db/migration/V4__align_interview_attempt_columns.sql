ALTER TABLE interview_attempt
ADD COLUMN IF NOT EXISTS answer_text TEXT;

ALTER TABLE interview_attempt
ADD COLUMN IF NOT EXISTS feedback TEXT;

ALTER TABLE interview_attempt
ADD COLUMN IF NOT EXISTS passed BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE interview_attempt
ADD COLUMN IF NOT EXISTS earned_points INTEGER NOT NULL DEFAULT 0;

ALTER TABLE interview_attempt
ADD COLUMN IF NOT EXISTS max_points INTEGER NOT NULL DEFAULT 0;

ALTER TABLE interview_attempt
ADD COLUMN IF NOT EXISTS missing_keywords_csv TEXT;

ALTER TABLE interview_attempt
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;