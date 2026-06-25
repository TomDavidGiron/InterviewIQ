-- Lookup columns that were queried but never indexed, causing full table scans
-- on interview_session(user_id) and interview_attempt(session_id/question_id).

CREATE INDEX IF NOT EXISTS idx_interview_session_user_id
    ON interview_session(user_id);

CREATE INDEX IF NOT EXISTS idx_interview_attempt_session_id
    ON interview_attempt(session_id);

CREATE INDEX IF NOT EXISTS idx_interview_attempt_question_id
    ON interview_attempt(question_id);
