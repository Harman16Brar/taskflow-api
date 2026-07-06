CREATE TABLE activity_logs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id     UUID NOT NULL REFERENCES tasks(id),
    user_id     UUID NOT NULL REFERENCES users(id),
    action      VARCHAR(50) NOT NULL,
    detail      TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_activity_logs_task_id ON activity_logs(task_id);