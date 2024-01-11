DROP INDEX IF EXISTS user_profile__user_id__ui;
CREATE UNIQUE INDEX user_profile__user_id__ui ON user_profile (user_id);

DROP INDEX IF EXISTS user_profile__geo__idx;
CREATE INDEX user_profile__geo__idx ON user_profile (geo_latitude, geo_longitude);