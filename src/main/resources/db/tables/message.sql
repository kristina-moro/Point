CREATE TABLE IF NOT EXISTS message
(
    id           SERIAL                     NOT NULL,
    sender       uuid                       NOT NULL,
    recipient    uuid                       NOT NULL,
    content      varchar(1000),
    img          bytea,
    at           timestamp with time zone   NOT NULL DEFAULT NOW(),
    is_new       boolean,
    is_deleted   boolean,

    CONSTRAINT messages_pk PRIMARY KEY (id) USING INDEX TABLESPACE point_ts,
    CONSTRAINT messages_sender_fk FOREIGN KEY (sender) REFERENCES users (id),
    CONSTRAINT messages_recipient_fk FOREIGN KEY (recipient) REFERENCES users (id)
)
TABLESPACE point_ts;