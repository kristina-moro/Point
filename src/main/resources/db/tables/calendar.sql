CREATE TABLE IF NOT EXISTS calendar
(
    id             SERIAL                     NOT NULL,
    user_id        uuid                       NOT NULL,
    type           varchar                    NOT NULL,  -- рабочий день или какой-то event или запись извне
    date_start     timestamp with time zone   NOT NULL,
    date_end       timestamp with time zone   NOT NULL,
    note           varchar(200),

    CONSTRAINT calendar_pk PRIMARY KEY (id) USING INDEX TABLESPACE point_ts,
    CONSTRAINT calendar_user_fk FOREIGN KEY (user_id) REFERENCES users (id)
)
TABLESPACE point_ts;