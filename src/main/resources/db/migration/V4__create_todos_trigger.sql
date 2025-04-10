CREATE OR REPLACE FUNCTION update_todos_modified_at()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.last_modified_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER todos_update_trigger
    BEFORE UPDATE ON todos
    FOR EACH ROW
EXECUTE FUNCTION update_todos_modified_at();
