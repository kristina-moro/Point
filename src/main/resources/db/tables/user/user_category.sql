CREATE TABLE IF NOT EXISTS user_category
(
    user_id       uuid       NOT NULL,
    category_id   smallint   NOT NULL,

    CONSTRAINT user_category_user_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT user_category_category_fk FOREIGN KEY (category_id) REFERENCES category (id)
)
TABLESPACE point_ts;