DROP INDEX IF EXISTS favourites__ui;
CREATE UNIQUE INDEX favourites__ui ON favourites (user_id, performer_id);

DROP INDEX IF EXISTS favourites__user__idx;
CREATE INDEX favourites__user__idx ON favourites (user_id);