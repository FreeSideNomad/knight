-- Initialize PostgreSQL schemas for Knight Platform
-- Each bounded context gets its own schema

\connect knight;

-- Service Profile Management
CREATE SCHEMA IF NOT EXISTS spm;
GRANT ALL ON SCHEMA spm TO knight;

-- Indirect Client Management
CREATE SCHEMA IF NOT EXISTS indirect_clients;
GRANT ALL ON SCHEMA indirect_clients TO knight;

-- Users
CREATE SCHEMA IF NOT EXISTS users;
GRANT ALL ON SCHEMA users TO knight;

-- Policy
CREATE SCHEMA IF NOT EXISTS policy;
GRANT ALL ON SCHEMA policy TO knight;

-- Approval Workflows
CREATE SCHEMA IF NOT EXISTS approvals;
GRANT ALL ON SCHEMA approvals TO knight;

-- Set default search path
ALTER DATABASE knight SET search_path TO public, spm, indirect_clients, users, policy, approvals;

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";

-- Success message
\echo 'Knight Platform schemas created successfully!'
