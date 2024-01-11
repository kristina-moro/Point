CREATE TABLE IF NOT EXISTS users
(
    id uuid NOT NULL,
    name character varying(20) NOT NULL,
    login character varying(30) NOT NULL,      -- необязательно при аутентификации через SSO
    password character varying(100) NOT NULL,  -- необязательно при аутентификации через SSO
    is_active boolean DEFAULT true,
    CONSTRAINT users_pk PRIMARY KEY (id) USING INDEX TABLESPACE point_ts
)
TABLESPACE point_ts;