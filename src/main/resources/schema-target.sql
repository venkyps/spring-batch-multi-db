-- Schema creation for target
CREATE SCHEMA IF NOT EXISTS target_schema;

CREATE TABLE IF NOT EXISTS target_schema.customer (
    id UUID PRIMARY KEY,
    ref_no VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    host_status VARCHAR(50),
    created_at TIMESTAMP,
    target_timestamp TIMESTAMP
);

-- Delete existing data for repeatable demo
TRUNCATE TABLE target_schema.customer;
