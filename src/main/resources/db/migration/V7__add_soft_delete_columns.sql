ALTER TABLE projects            ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE projects            ADD COLUMN deleted_at TIMESTAMP;

ALTER TABLE workspaces          ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE workspaces          ADD COLUMN deleted_at TIMESTAMP;

ALTER TABLE workspace_members   ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE workspace_members   ADD COLUMN deleted_at TIMESTAMP;

ALTER TABLE tasks               ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE tasks               ADD COLUMN deleted_at TIMESTAMP;

ALTER TABLE comments            ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE comments            ADD COLUMN deleted_at TIMESTAMP;

ALTER TABLE users               ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE users               ADD COLUMN deleted_at TIMESTAMP;
