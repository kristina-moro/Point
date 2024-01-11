CREATE TABLE IF NOT EXISTS favourites
(
    id                SERIAL         NOT NULL,
    user_id           uuid           NOT NULL,
    performer_id      uuid           NOT NULL,
    name              varchar(40),

    CONSTRAINT favourites_pk PRIMARY KEY (id) USING INDEX TABLESPACE point_ts,
    CONSTRAINT favourites_user_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT favourites_performer_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
)
TABLESPACE point_ts;