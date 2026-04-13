CREATE TABLE IF NOT EXISTS job_descriptions (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    company VARCHAR(255),
    location VARCHAR(255),
    description VARCHAR(5000)
);

CREATE TABLE IF NOT EXISTS job_requirements (
    job_id BIGINT NOT NULL,
    requirement TEXT,
    CONSTRAINT fk_job_requirements_job
        FOREIGN KEY (job_id) REFERENCES job_descriptions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_job_requirements_job_id
    ON job_requirements(job_id);