-- Schema creation for target
CREATE SCHEMA IF NOT EXISTS target_schema;

CREATE TABLE IF NOT EXISTS target_schema.customer (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255)
);

-- Delete existing data for repeatable demo
TRUNCATE TABLE target_schema.customer;
