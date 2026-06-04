UPDATE app_starter.tickets
SET title = 'Ohne Titel'
WHERE title IS NULL OR btrim(title) = '';

UPDATE app_starter.tickets
SET status = 'open'
WHERE status IS NULL OR status NOT IN ('open', 'waiting', 'closed');

ALTER TABLE app_starter.tickets
    ALTER COLUMN title SET NOT NULL;

ALTER TABLE app_starter.tickets
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE app_starter.tickets
    ADD CONSTRAINT tickets_status_check
    CHECK (status IN ('open', 'waiting', 'closed'));
