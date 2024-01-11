CREATE TABLE IF NOT EXISTS user_profile
(
    user_id        uuid                  NOT NULL,
    lang           varchar(4)            NOT NULL,               -- код языка ISO 639-1  ru fr en
    phone_number   varchar(20),
    email          varchar(70)           NOT NULL,               -- зачем тут email, если он есть в users?
    avatar         bytea,
    description    varchar(70),
    about          varchar(1000),
    address        varchar,                                       -- для показа на фронте, если пользователь является исполнителем и хочет показывать свой адрес
    geo_latitude   double precision,                              -- преднастроенная локация для поиска предложений данного исполнителя, если пользователь является исполнителем
    geo_longitude  double precision,

    opening_hours varchar,                                       -- нужно только для исполнителей
    is_active     boolean,                                       -- активный или заблокированный профиль. Почему бы не перенести на уровено пользователя в таблицу USERS ?
    is_hidden     boolean,

    CONSTRAINT user_profile_pk PRIMARY KEY (user_id) USING INDEX TABLESPACE point_ts,
    CONSTRAINT user_profile_user_fk FOREIGN KEY (user_id) REFERENCES users (id)
)
TABLESPACE point_ts;

-- нужно ли пользователю несколько профилей с возможностью активации и деактивации?
-- тогда name должно быть атрибутом profile ?