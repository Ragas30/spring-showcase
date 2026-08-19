CREATE TABLE webhook_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    url VARCHAR(500) NOT NULL,
    secret_key VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE webhook_logs (
    id BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT NOT NULL REFERENCES webhook_subscriptions(id) ON DELETE CASCADE,
    event_name VARCHAR(50) NOT NULL,
    payload TEXT,
    response TEXT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_webhook_log_subscription ON webhook_logs(subscription_id);
CREATE INDEX idx_webhook_log_event ON webhook_logs(event_name);
CREATE INDEX idx_webhook_log_status ON webhook_logs(status);
