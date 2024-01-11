CREATE TABLE IF NOT EXISTS request
(
    id           uuid                      NOT NULL,
    type         varchar(20)               NOT NULL,
    user_id      uuid                      NOT NULL,
    user_name    varchar(70)               NOT NULL,
    login        varchar(70)               NOT NULL,
    password     varchar                   NOT NULL,
    valid_from   timestamp with time zone  NOT NULL,
    valid_to     timestamp with time zone  NOT NULL,

    CONSTRAINT request_pk PRIMARY KEY (id) USING INDEX TABLESPACE point_ts
)
TABLESPACE point_ts;