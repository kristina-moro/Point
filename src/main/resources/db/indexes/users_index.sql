DROP INDEX IF EXISTS users__login__ui;

CREATE UNIQUE INDEX users__login__ui ON users (login);