CREATE TABLE if NOT EXISTS blacklist_merchants (
    id BIGSERIAL PRIMARY KEY,
    merchant_id VARCHAR(100) NOT NULL UNIQUE,
    reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transaction_logs (
    id BIGSERIAL PRIMARY KEY,
    trace_id VARCHAR(100) NOT NULL UNIQUE,
    client_id VARCHAR(50),
    client_tier VARCHAR(20),
    rules_evaluated JSONB,
    rules_scores JSONB,
    aggregate_score INT NOT NULL,
    decision VARCHAR NOT NULL,
    execution_time_ms BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);