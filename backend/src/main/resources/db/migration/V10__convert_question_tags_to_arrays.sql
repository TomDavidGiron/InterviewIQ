-- Tags and required_keywords were stored as comma-separated TEXT, which can't be
-- queried relationally (e.g. "WHERE tags @> ARRAY['java']"). Convert both to native
-- Postgres TEXT[] columns, splitting existing CSV values on the comma.

ALTER TABLE question
    ALTER COLUMN tags TYPE TEXT[] USING
        CASE
            WHEN tags IS NULL OR tags = '' THEN '{}'::TEXT[]
            ELSE string_to_array(tags, ',')
        END;

ALTER TABLE question
    ALTER COLUMN required_keywords TYPE TEXT[] USING
        CASE
            WHEN required_keywords IS NULL OR required_keywords = '' THEN '{}'::TEXT[]
            ELSE string_to_array(required_keywords, ',')
        END;
