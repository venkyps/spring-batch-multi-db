-- Schema creation and initialization for source
CREATE SCHEMA IF NOT EXISTS source_schema;

CREATE TABLE IF NOT EXISTS source_schema.customer (
    id UUID PRIMARY KEY,
    ref_no VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- Basic sample data
INSERT INTO source_schema.customer (id, ref_no, status) 
VALUES 
    ('11111111-1111-1111-1111-111111111111', 'REF-001', 'ACTIVE'),
    ('22222222-2222-2222-2222-222222222222', 'REF-002', 'PENDING')
ON CONFLICT (id) DO NOTHING;
