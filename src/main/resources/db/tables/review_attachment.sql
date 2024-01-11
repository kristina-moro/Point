CREATE TABLE IF NOT EXISTS review_attachment
(
    id          SERIAL    NOT NULL,
    review_id   integer   NOT NULL,
    image       bytea     NOT NULL,

    CONSTRAINT review_attachments_pk PRIMARY KEY (id) USING INDEX TABLESPACE point_ts,
    CONSTRAINT review_attachment_review_fk FOREIGN KEY (review_id) REFERENCES review (id)
)
TABLESPACE point_ts;