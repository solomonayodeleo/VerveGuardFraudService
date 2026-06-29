ALTER TABLE transaction_logs
    ADD COLUMN IF NOT EXISTS card_number_hash VARCHAR(225),
    ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45)