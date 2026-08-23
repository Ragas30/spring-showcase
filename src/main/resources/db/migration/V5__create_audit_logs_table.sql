-- ================================================
-- V5: Create audit_logs table
-- V1 was skipped because the database was baselined,
-- so this table was never created.
-- ================================================

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL,
    old_values TEXT,
    new_values TEXT,
    performed_by VARCHAR(100),
    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_performed_at ON audit_logs(performed_at);
CREATE INDEX IF NOT EXISTS idx_audit_action ON audit_logs(action);
