-- Auto-update updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Generic audit trigger function (created_by, updated_by, deleted_by)
CREATE OR REPLACE FUNCTION audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        NEW.created_at = CURRENT_TIMESTAMP;
        NEW.updated_at = CURRENT_TIMESTAMP;
        IF NEW.created_by IS NULL THEN
            NEW.created_by = current_setting('app.current_user_id', true)::BIGINT;
        END IF;
        IF NEW.updated_by IS NULL THEN
            NEW.updated_by = current_setting('app.current_user_id', true)::BIGINT;
        END IF;
        RETURN NEW;
    ELSIF (TG_OP = 'UPDATE') THEN
        NEW.updated_at = CURRENT_TIMESTAMP;
        NEW.updated_by = current_setting('app.current_user_id', true)::BIGINT;
        RETURN NEW;
    ELSIF (TG_OP = 'DELETE') THEN
        NEW.deleted_at = CURRENT_TIMESTAMP;
        NEW.deleted_by = current_setting('app.current_user_id', true)::BIGINT;
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Helper: Apply audit trigger to table
CREATE OR REPLACE FUNCTION apply_audit(table_name TEXT)
RETURNS VOID AS $$
BEGIN
    EXECUTE format(
        'CREATE TRIGGER %I_audit_trigger
         BEFORE INSERT OR UPDATE OR DELETE ON %I
         FOR EACH ROW EXECUTE FUNCTION audit_trigger()',
        table_name, table_name
    );
END;
$$ LANGUAGE plpgsql;
