ALTER TABLE app_starter.tickets
    ADD COLUMN external_reference TEXT;

ALTER TABLE app_starter.tickets
    ADD COLUMN priority TEXT;

ALTER TABLE app_starter.tickets
    ADD COLUMN updated_at TIMESTAMPTZ;

ALTER TABLE app_starter.tickets
    ADD COLUMN deleted_at TIMESTAMPTZ;

UPDATE app_starter.tickets
SET priority = 'normal'
WHERE priority IS NULL;

UPDATE app_starter.tickets
SET updated_at = created_at
WHERE updated_at IS NULL;

ALTER TABLE app_starter.tickets
    ALTER COLUMN priority SET NOT NULL;

ALTER TABLE app_starter.tickets
    ALTER COLUMN priority SET DEFAULT 'normal';

ALTER TABLE app_starter.tickets
    ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE app_starter.tickets
    ALTER COLUMN updated_at SET DEFAULT now();

ALTER TABLE app_starter.tickets
    ADD CONSTRAINT tickets_priority_check
    CHECK (priority IN ('low', 'normal', 'high', 'urgent'));

ALTER TABLE app_starter.tickets
    ADD CONSTRAINT tickets_external_reference_not_blank_check
    CHECK (external_reference IS NULL OR btrim(external_reference) <> '');

ALTER TABLE app_starter.tickets
    ADD CONSTRAINT tickets_deleted_after_created_check
    CHECK (deleted_at IS NULL OR deleted_at >= created_at);

ALTER TABLE app_starter.tickets
    ADD CONSTRAINT tickets_external_reference_unique
    UNIQUE (external_reference);

CREATE INDEX tickets_active_status_created_at_idx
    ON app_starter.tickets (status, created_at DESC)
    WHERE deleted_at IS NULL;
