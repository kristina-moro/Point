CREATE TABLE IF NOT EXISTS event_log
(
    id         SERIAL                     NOT NULL,
    user_id    uuid                       NOT NULL,
    event_type varchar(30)                NOT NULL,  -- todo: сделать справочник ивентов
    note       varchar,
    datetime   timestamp with time zone   NOT NULL DEFAULT NOW(),

    CONSTRAINT event_log_pk PRIMARY KEY (id) USING INDEX TABLESPACE point_ts,
    CONSTRAINT event_log_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
)
TABLESPACE point_ts;