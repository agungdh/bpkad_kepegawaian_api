-- Add columns to pegawai
ALTER TABLE pegawai ADD COLUMN skpd_id BIGINT;
ALTER TABLE pegawai ADD COLUMN bidang_id BIGINT;

-- Foreign keys
ALTER TABLE pegawai ADD CONSTRAINT fk_pegawai_skpd FOREIGN KEY (skpd_id) REFERENCES skpd(id);
ALTER TABLE pegawai ADD CONSTRAINT fk_pegawai_bidang FOREIGN KEY (bidang_id) REFERENCES bidang(id);

-- Trigger function to validate: if bidang_id is set, skpd_id must match bidang.skpd_id
CREATE OR REPLACE FUNCTION validate_pegawai_bidang_skpd()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.bidang_id IS NOT NULL THEN
        -- Get skpd_id from bidang table
        PERFORM 1 FROM bidang WHERE id = NEW.bidang_id AND deleted_at IS NULL;

        IF NOT FOUND THEN
            RAISE EXCEPTION 'Bidang with id % not found or deleted', NEW.bidang_id;
        END IF;

        -- Validate skpd_id matches
        IF NEW.skpd_id IS NULL THEN
            RAISE EXCEPTION 'skpd_id must be set when bidang_id is set';
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM bidang WHERE id = NEW.bidang_id AND skpd_id = NEW.skpd_id AND deleted_at IS NULL
        ) THEN
            RAISE EXCEPTION 'skpd_id does not match bidang.skpd_id';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger
CREATE TRIGGER trg_pegawai_bidang_skpd_validate
    BEFORE INSERT OR UPDATE OF skpd_id, bidang_id ON pegawai
    FOR EACH ROW EXECUTE FUNCTION validate_pegawai_bidang_skpd();
