-- Extensions are infrastructure prerequisites and must be installed by the
-- database bootstrap user rather than by the application's Flyway user.
CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;
