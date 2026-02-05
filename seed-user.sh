#!/bin/bash

# Seed script untuk membuat user admin pertama
# Usage: ./seed-user.sh [username] [password] [nama]

DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-bpkad_kepegawaian}
DB_USER=${DB_USER:-postgres}
DB_PASSWORD=${DB_PASSWORD:-postgres}

USERNAME=${1:-admin}
PASSWORD=${2:-admin}
NAMA=${3:-Administrator}

# Generate BCrypt hash menggunakan Maven/Java
echo "Generating password hash..."
PASSWORD_HASH=$(./mvnw -q exec:java -Dexec.mainClass="id.my.agungdh.bpkadkepegawaian.util.PasswordHashGenerator" -Dexec.args="$PASSWORD" 2>/dev/null | grep "BCrypt Hash:" | cut -d' ' -f3)

if [ -z "$PASSWORD_HASH" ]; then
    echo "Error: Gagal generate password hash"
    exit 1
fi

echo "Password hash generated: $PASSWORD_HASH"

# Current timestamp in milliseconds
TIMESTAMP=$(date +%s)000

# SQL commands
SQL="
-- Insert pegawai dulu (karena users punya foreign key ke pegawai)
DO \$\$
DECLARE
    v_pegawai_id BIGINT;
    v_user_id BIGINT;
    v_role_id BIGINT;
BEGIN
    -- Cek/insert pegawai
    SELECT id INTO v_pegawai_id FROM pegawai WHERE nip = '$USERNAME' AND deleted_at IS NULL LIMIT 1;

    IF v_pegawai_id IS NULL THEN
        INSERT INTO pegawai (nip, nama, email, created_at, updated_at)
        VALUES ('$USERNAME', '$NAMA', '$USERNAME@bpkad.local', $TIMESTAMP, $TIMESTAMP)
        RETURNING id INTO v_pegawai_id;
    END IF;

    IF v_pegawai_id IS NOT NULL THEN
        -- Cek/insert user
        SELECT id INTO v_user_id FROM users WHERE username = '$USERNAME' AND deleted_at IS NULL LIMIT 1;

        IF v_user_id IS NULL THEN
            INSERT INTO users (username, password, enabled, pegawai_id, created_at, updated_at)
            VALUES ('$USERNAME', '$PASSWORD_HASH', TRUE, v_pegawai_id, $TIMESTAMP, $TIMESTAMP)
            RETURNING id INTO v_user_id;
        END IF;

        -- Cek/insert role ADMIN
        SELECT id INTO v_role_id FROM roles WHERE name = 'ADMIN' AND deleted_at IS NULL LIMIT 1;

        IF v_role_id IS NULL THEN
            INSERT INTO roles (name, created_at, updated_at)
            VALUES ('ADMIN', $TIMESTAMP, $TIMESTAMP)
            RETURNING id INTO v_role_id;
        END IF;

        -- Assign role ke user
        INSERT INTO user_roles (user_id, role_id)
        VALUES (v_user_id, v_role_id)
        ON CONFLICT (user_id, role_id) DO NOTHING;

        RAISE NOTICE 'User berhasil dibuat!';
        RAISE NOTICE 'Username: %', '$USERNAME';
        RAISE NOTICE 'Password: %', '$PASSWORD';
        RAISE NOTICE 'Nama: %', '$NAMA';
    ELSE
        RAISE NOTICE 'Gagal membuat pegawai';
    END IF;
END \$\$;
"

# Execute SQL
PGPASSWORD=$DB_PASSWORD psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "$SQL"

echo ""
echo "Selesai! User admin telah dibuat."
echo "Username: $USERNAME"
echo "Password: $PASSWORD"
