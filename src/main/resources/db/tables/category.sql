CREATE TABLE IF NOT EXISTS category
(
    id            smallint       NOT NULL,
    name          varchar(40)    NOT NULL,
    description   varchar(200)   NOT NULL,
    parent_id     smallint,

    CONSTRAINT category_pk PRIMARY KEY (id) USING INDEX TABLESPACE point_ts,
    CONSTRAINT category_parent_id_fk FOREIGN KEY (parent_id) REFERENCES category (id)
)
TABLESPACE point_ts;