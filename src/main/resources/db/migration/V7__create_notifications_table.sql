CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    recipient_username VARCHAR(100) NOT NULL,
    recipient_role VARCHAR(50),
    title VARCHAR(200) NOT NULL,
    message TEXT,
    type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notification_recipient_username ON notifications(recipient_username);
CREATE INDEX idx_notification_recipient_role ON notifications(recipient_role);
CREATE INDEX idx_notification_created_at ON notifications(created_at);
CREATE INDEX idx_notification_is_read ON notifications(recipient_username, is_read);
