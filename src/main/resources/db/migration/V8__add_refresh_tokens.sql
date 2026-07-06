CREATE TABLE refresh_tokens(
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
token_hash VARCHAR(64) NOT NULL UNIQUE,
user_id UUID NOT NULL REFERENCES users(id),
expires_at TIMESTAMP NOT NULL,
revoked BOOLEAN DEFAULT FALSE,
created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);