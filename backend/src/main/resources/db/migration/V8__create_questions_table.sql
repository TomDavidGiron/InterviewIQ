CREATE TABLE IF NOT EXISTS question (
    id               VARCHAR(50)  PRIMARY KEY,
    text             TEXT         NOT NULL,
    type             VARCHAR(10)  NOT NULL DEFAULT 'OPEN',
    tags             TEXT,
    required_keywords TEXT,
    critical         BOOLEAN      NOT NULL DEFAULT FALSE,
    options          TEXT,
    correct_option_index INTEGER,
    starter_code     TEXT,
    source           VARCHAR(50)  NOT NULL DEFAULT 'question_bank',
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at       TIMESTAMP    NOT NULL
);
