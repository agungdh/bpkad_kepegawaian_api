-- Table pegawai dengan audit columns
CREATE TABLE pegawai (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    nip VARCHAR(50) NOT NULL,
    nama VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    deleted_by BIGINT
);

-- Partial unique index untuk nip (exclude soft deleted)
CREATE UNIQUE INDEX idx_pegawai_nip_active ON pegawai (nip) WHERE deleted_at IS NULL;

-- Index untuk uuid (hash untuk frontend lookup)
CREATE INDEX idx_pegawai_uuid ON pegawai USING HASH (uuid);

-- Apply audit trigger
SELECT apply_audit('pegawai');
