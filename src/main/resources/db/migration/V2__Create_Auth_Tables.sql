-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    pegawai_id BIGINT NOT NULL,
    created_at BIGINT,
    updated_at BIGINT,
    deleted_at BIGINT,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_by BIGINT,
    CONSTRAINT fk_users_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai(id)
);

-- Roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at BIGINT,
    updated_at BIGINT,
    deleted_at BIGINT,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_by BIGINT
);

-- User roles join table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Index for username lookup
CREATE UNIQUE INDEX idx_users_username_active ON users (username) WHERE deleted_at IS NULL;

-- Partial unique index untuk pegawai_id (exclude soft deleted)
CREATE UNIQUE INDEX idx_users_pegawai_id_active ON users (pegawai_id) WHERE deleted_at IS NULL;
