CREATE TABLE IF NOT EXISTS review
(
    id              SERIAL                    NOT NULL,
    performer_id    uuid                      NOT NULL,
    customer_id     uuid                      NOT NULL,
    rate            smallint                  NOT NULL,
    content         varchar,
    at              timestamp with time zone  NOT NULL DEFAULT NOW(),
    appointment_id  UUID                      NOT NULL,
    category_id     smallint,

    CONSTRAINT review_pk PRIMARY KEY (id) USING INDEX TABLESPACE point_ts,
    CONSTRAINT review_performer_fk FOREIGN KEY (performer_id) REFERENCES users (id),
    CONSTRAINT review_customer_fk FOREIGN KEY (customer_id) REFERENCES users (id)
)
TABLESPACE point_ts;