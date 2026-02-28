-- Schema creation and initialization for source
CREATE SCHEMA IF NOT EXISTS source_schema;

CREATE TABLE IF NOT EXISTS source_schema.customer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255)
);

-- Delete existing data for repeatable demo
TRUNCATE TABLE source_schema.customer RESTART IDENTITY CASCADE;

-- Insert sample data
INSERT INTO source_schema.customer (first_name, last_name, email) VALUES
    ('John', 'Doe', 'john.doe@example.com'),
    ('Jane', 'Smith', 'jane.smith@example.com'),
    ('Alice', 'Johnson', 'alice.j@example.com'),
    ('Bob', 'Williams', 'bob.w@example.com'),
    ('Charlie', 'Brown', 'charlie.b@example.com');
