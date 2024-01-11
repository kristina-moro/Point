CREATE TABLE IF NOT EXISTS user_portfolio
(
    id          SERIAL                    NOT NULL,
    user_id     uuid                      NOT NULL,
    sort_order  smallint,
    description varchar(250),
    image       bytea                     NOT NULL,

    CONSTRAINT user_portfolio_pk PRIMARY KEY (id) USING INDEX TABLESPACE point_ts,
    CONSTRAINT user_portfolio_user_fk FOREIGN KEY (user_id) REFERENCES public.users (id)
)
TABLESPACE point_ts;