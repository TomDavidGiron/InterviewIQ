CREATE TABLE IF NOT EXISTS interview_session (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    role_hint VARCHAR(255),
    level_hint VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    completed_at TIMESTAMP,
    score INTEGER,
    total_possible_score INTEGER,
    fail_reason TEXT
);

CREATE TABLE IF NOT EXISTS interview_attempt (
    id VARCHAR(36) PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    question_id VARCHAR(255),
    question_text TEXT,
    answer_text TEXT,
    feedback TEXT,
    passed BOOLEAN NOT NULL,
    earned_points INTEGER NOT NULL,
    max_points INTEGER NOT NULL,
    missing_keywords_csv TEXT,
    created_at TIMESTAMP,
    CONSTRAINT fk_interview_attempt_session
        FOREIGN KEY (session_id) REFERENCES interview_session(id) ON DELETE CASCADE
);