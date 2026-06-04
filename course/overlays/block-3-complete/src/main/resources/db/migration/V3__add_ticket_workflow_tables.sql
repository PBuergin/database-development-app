ALTER TABLE app_starter.tickets
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

CREATE TABLE app_starter.ticket_comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticket_id BIGINT NOT NULL REFERENCES app_starter.tickets (id) ON DELETE CASCADE,
    body TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE app_starter.ticket_events (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticket_id BIGINT NOT NULL REFERENCES app_starter.tickets (id) ON DELETE CASCADE,
    event_type TEXT NOT NULL,
    old_status TEXT,
    new_status TEXT,
    reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ticket_events_type_check
        CHECK (event_type IN ('ticket_created', 'status_changed')),
    CONSTRAINT ticket_events_status_changed_check
        CHECK (
            event_type <> 'status_changed'
            OR (old_status IS NOT NULL AND new_status IS NOT NULL)
        )
);

CREATE INDEX ticket_comments_ticket_id_idx
    ON app_starter.ticket_comments (ticket_id);

CREATE INDEX ticket_events_ticket_id_idx
    ON app_starter.ticket_events (ticket_id);
