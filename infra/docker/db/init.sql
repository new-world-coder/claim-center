CREATE TABLE IF NOT EXISTS tenants (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    tier VARCHAR(16) NOT NULL,
    db_name VARCHAR(64),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_tenants_tenant_id ON tenants (tenant_id);

CREATE TABLE IF NOT EXISTS tenant_settings (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL UNIQUE,
    currency VARCHAR(8) NOT NULL,
    timezone VARCHAR(64) NOT NULL,
    claim_auto_approve_limit NUMERIC(14, 2) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_tenant_settings_tenant_id ON tenant_settings (tenant_id);

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    tenant_tier VARCHAR(16) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    enabled BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_users_tenant_id ON users (tenant_id);

CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_customers_tenant_id ON customers (tenant_id);

CREATE TABLE IF NOT EXISTS policies (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    policy_number VARCHAR(64) NOT NULL,
    customer_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    coverage_amount NUMERIC(14, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_policies_tenant_id ON policies (tenant_id);
CREATE INDEX IF NOT EXISTS idx_policies_policy_number ON policies (policy_number);

CREATE TABLE IF NOT EXISTS claims (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    claim_number VARCHAR(64) NOT NULL,
    policy_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    amount NUMERIC(14, 2) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    status VARCHAR(32) NOT NULL,
    fraud_flag BOOLEAN NOT NULL,
    fraud_score INTEGER NOT NULL DEFAULT 0,
    fraud_band VARCHAR(16) NOT NULL DEFAULT 'LOW',
    fraud_reasons VARCHAR(2000) NOT NULL DEFAULT '',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_claims_tenant_id ON claims (tenant_id);
CREATE INDEX IF NOT EXISTS idx_claims_policy_id ON claims (policy_id);
CREATE INDEX IF NOT EXISTS idx_claims_claim_number ON claims (claim_number);

CREATE TABLE IF NOT EXISTS documents (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(128) NOT NULL,
    claim_id UUID,
    storage_key VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_documents_tenant_id ON documents (tenant_id);
CREATE INDEX IF NOT EXISTS idx_documents_claim_id ON documents (claim_id);

CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    channel VARCHAR(32) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_notifications_tenant_id ON notifications (tenant_id);

CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    actor VARCHAR(255) NOT NULL,
    action VARCHAR(128) NOT NULL,
    resource_name VARCHAR(128) NOT NULL,
    details VARCHAR(2000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_audit_logs_tenant_id ON audit_logs (tenant_id);

ALTER TABLE claims ADD COLUMN IF NOT EXISTS fraud_score INTEGER NOT NULL DEFAULT 0;
ALTER TABLE claims ADD COLUMN IF NOT EXISTS fraud_band VARCHAR(16) NOT NULL DEFAULT 'LOW';
ALTER TABLE claims ADD COLUMN IF NOT EXISTS fraud_reasons VARCHAR(2000) NOT NULL DEFAULT '';
