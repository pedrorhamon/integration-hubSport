CREATE TABLE IF NOT EXISTS webhook_event_logs (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100),
    subscription_type VARCHAR(100),
    object_id BIGINT,
    change_source VARCHAR(100),
    change_flag VARCHAR(100),
    raw_payload TEXT,
    received_at TIMESTAMP
    );
