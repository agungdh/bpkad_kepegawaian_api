CREATE TABLE bidang (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    skpd_id BIGINT NOT NULL,
    nama VARCHAR(255) NOT NULL,
    created_at BIGINT,
    updated_at BIGINT,
    deleted_at BIGINT,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_by BIGINT,
    CONSTRAINT fk_bidang_skpd FOREIGN KEY (skpd_id) REFERENCES skpd(id)
);
