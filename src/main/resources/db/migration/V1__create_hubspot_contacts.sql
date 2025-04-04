CREATE TABLE IF NOT EXISTS hubspot_contacts (
    id BIGINT PRIMARY KEY,
    email VARCHAR(255),
    firstname VARCHAR(100),
    lastname VARCHAR(100),
    full_name VARCHAR(255),
    lifecycle_stage VARCHAR(100),
    email_domain VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
    );
