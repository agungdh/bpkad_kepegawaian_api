-- Table pegawai dengan audit columns (epoch millis, nullable for direct DB injection)
CREATE TABLE pegawai (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    nip VARCHAR(50) NOT NULL,
    nama VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    created_at BIGINT,
    updated_at BIGINT,
    deleted_at BIGINT,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_by BIGINT
);

-- Partial unique index untuk nip (exclude soft deleted)
CREATE UNIQUE INDEX idx_pegawai_nip_active ON pegawai (nip) WHERE deleted_at IS NULL;

-- Index untuk uuid (hash untuk frontend lookup)
CREATE INDEX idx_pegawai_uuid ON pegawai USING HASH (uuid);
