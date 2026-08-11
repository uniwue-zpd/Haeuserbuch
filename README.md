# Haeuserbuch

The application runs with Docker Compose. Development and production use separate Compose configurations.

## Requirements

- Docker Desktop or Docker Engine with Compose v2
- At least 4 GB of memory available to Docker
- The project map data

## Map data

`tileserver/config.json` defines the expected datasets and is included in the repository. The MBTiles archives are operator-provided and are not committed because they are large and may have redistribution restrictions.

Obtain the files from the project's authorized storage and place them in `./tiles`:

```text
tiles/
├── NW08051_new.mbtiles
└── NW08151.mbtiles
```

The filenames must match `tileserver/config.json`. To store them elsewhere, set `TILES_DIR` in the relevant environment file. The configuration is mounted separately and does not need to be copied into that directory.

## Development

The checked-in defaults are enough to start development. If no `.env` exists and you need to override them, create one from the example:

```bash
cp .env.example .env
```

Start the stack:

```bash
docker compose up --build --wait
```

Open <http://127.0.0.1:3000>. Source changes are picked up automatically; rebuild after changing dependencies or Dockerfiles.

```bash
# Follow logs
docker compose logs -f

# Stop the stack without deleting data
docker compose down
```

PostgreSQL uses the `postgres_data` volume, and uploads use the `uploads_data` volume. Do not run `docker compose down --volumes` unless this development data should be deleted.

### Initialize the database from a dump

Place the current project dump at `./dump.sql` before the first startup when the historical research dataset should be imported. Development Compose mounts it at:

```yaml
- ./dump.sql:/docker-entrypoint-initdb.d/05_dump.sql:ro
```

PostgreSQL imports the dump only when creating an empty `postgres_data` volume. It does not import into an existing database. Without imported data, Flyway still creates an empty but complete application schema by applying all migrations in order. Retain any old database data until the restored application has been verified.

PostGIS is a database infrastructure prerequisite. The Compose database initializer enables it before either the dump import or Flyway runs. Operators using an externally managed PostgreSQL instance must enable the `postgis` extension before starting the backend; Flyway itself does not require extension-management privileges.

### Database migrations

`V1__baseline_schema.sql` represents the schema in the checked-in legacy dump. `V2__add_file_upload_schema.sql` adds the file-upload tables implemented after that dump. These are currently the only active migrations.

For an existing database restored from the legacy project dump, enable `FLYWAY_BASELINE_ON_MIGRATE` for its first Flyway-managed startup. Flyway records that schema as baseline version `1`, skips the V1 schema creation, and applies V2. Disable the setting again as soon as the history table exists; leaving it enabled removes Flyway's protection against accidentally adopting an unrelated non-empty database. A new empty database runs V1 and V2 normally and does not require baselining.

Development Compose enables the legacy-baseline switch by default because it mounts `dump.sql`. Production defaults it to `false` and requires an explicit, temporary opt-in. Flyway owns only the `public` schema, validates migration names and checksums on startup, and cannot run `clean`. Hibernate validates the resulting schema in development, tests, and production and does not modify it automatically.

For every future schema change, add a new versioned migration. Never edit or rename a migration that has already been applied to a shared database. Check `flyway_schema_history` when diagnosing migration state.

## Production

Create the production environment file and replace its placeholder password:

```bash
cp .env.prod.example .env.prod
```

Also configure `TILES_DIR` and `BACKUP_DIR` there if their default directories should not be used. The configured PostGIS image supports both AMD64 and ARM64.

Production mounts `dump.sql` automatically. Its objects are owned by `haeuserbuch_user`; the included initializer prepares that role and grants it to the configured `DB_USER` before importing the dump. PostgreSQL only runs these initialization scripts for a new `postgres_data` volume.

For the first deployment against the existing unversioned server database:

1. Take and verify a database backup.
2. Set `FLYWAY_BASELINE_ON_MIGRATE=true` in `.env.prod`.
3. Start the new backend and wait for its health check.
4. Verify that `flyway_schema_history` contains V1 as `BASELINE` and V2 as `SQL`:

```bash
docker compose --env-file .env.prod -f compose.prod.yaml exec -T database \
  sh -lc 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c \
  "SELECT installed_rank, version, description, type, success FROM public.flyway_schema_history ORDER BY installed_rank"'
```

5. Change `FLYWAY_BASELINE_ON_MIGRATE=false` and recreate the backend container. Re-enable it only when deliberately adopting another legacy database without Flyway history.

If a production volume was initialized before this setup, or its initialization logs contain an error, it may contain only a partial database. If that volume does not contain data that must be retained, remove only the database volume and start the stack again:

```bash
docker compose --env-file .env.prod \
  -f compose.prod.yaml \
  -f compose.prod.build.yaml \
  down
docker volume rm haeuserbuch-prod_postgres_data
```

Use the project name from `COMPOSE_PROJECT_NAME` when it differs from `haeuserbuch-prod`. Take a backup before removing a volume that may contain production changes.

Build and start the production stack:

```bash
docker compose --env-file .env.prod \
  -f compose.prod.yaml \
  -f compose.prod.build.yaml \
  up -d --build --wait
```

Verify it through the published port:

```bash
curl --fail http://127.0.0.1:3000/
curl --fail http://127.0.0.1:3000/api/actuator/health
curl --fail http://127.0.0.1:3000/tiles/index.json
```

Only this frontend port is published. Configure the server's Apache instance to proxy the public application to it. Apache handles TLS and optional authentication.

Stop production without deleting its named database and upload volumes:

```bash
docker compose --env-file .env.prod \
  -f compose.prod.yaml \
  -f compose.prod.build.yaml \
  down
```

### Prebuilt images

CI publishes these images:

```text
ghcr.io/uniwue-zpd/haeuserbuch-backend
ghcr.io/uniwue-zpd/haeuserbuch-frontend
ghcr.io/uniwue-zpd/haeuserbuch-db-backup
```

Pushes to `dev` publish `dev`, `dev-<commit>`, and `sha-<commit>`. Create a versioned release with:

```bash
git tag v1.2.3
git push origin v1.2.3
```

This publishes `1.2.3`, `1.2`, `1`, `latest`, and `sha-<commit>`. To deploy prebuilt images, set the versions in `.env.prod`:

```dotenv
HAEUSERBUCH_BACKEND_IMAGE=ghcr.io/uniwue-zpd/haeuserbuch-backend:1.2.3
HAEUSERBUCH_FRONTEND_IMAGE=ghcr.io/uniwue-zpd/haeuserbuch-frontend:1.2.3
HAEUSERBUCH_BACKUP_IMAGE=ghcr.io/uniwue-zpd/haeuserbuch-db-backup:1.2.3
```

```bash
docker compose --env-file .env.prod -f compose.prod.yaml up -d --wait
```

### Database backups

Start the optional backup service with production:

```bash
docker compose --env-file .env.prod \
  -f compose.prod.yaml \
  -f compose.prod.build.yaml \
  --profile backup up -d --build db_backup
```

It writes a compressed dump at startup and daily at 05:00 to `BACKUP_DIR`. Copy backups off the application server and test restoring them regularly.
