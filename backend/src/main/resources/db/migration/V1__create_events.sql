CREATE TABLE events (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL
        CONSTRAINT chk_events_name_not_blank
        CHECK (name ~ '[^[:space:]]'),
    starts_at TIMESTAMP WITH TIME ZONE NOT NULL
);