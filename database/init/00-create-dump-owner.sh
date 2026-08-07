#!/bin/sh

set -eu

# The plain SQL dump contains ownership statements for this role. Keep the
# owner non-login and grant it to the configured database role so those
# statements can be applied without requiring a login for the dump owner.
dump_owner='haeuserbuch_user'

psql \
  --username "$POSTGRES_USER" \
  --dbname "$POSTGRES_DB" \
  --set=ON_ERROR_STOP=1 \
  --set=dump_owner="$dump_owner" \
  --set=app_user="$POSTGRES_USER" <<'SQL'
SELECT format('CREATE ROLE %I NOLOGIN', :'dump_owner')
WHERE NOT EXISTS (
    SELECT FROM pg_catalog.pg_roles
    WHERE rolname = :'dump_owner'
)\gexec

SELECT format('GRANT %I TO %I', :'dump_owner', :'app_user')
WHERE :'app_user' <> :'dump_owner'\gexec
SQL
