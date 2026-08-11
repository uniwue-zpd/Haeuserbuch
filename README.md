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

PostGIS is a database infrastructure prerequisite. The Compose database initializer enables it before either the dump import or Flyway runs. Operators using an externally managed PostgreSQL instance must enable the `postgis` extension before starting the backend. Some migrations may enable additional trusted PostgreSQL extensions, so the migration role must own the database or otherwise have the required `CREATE` privilege.

### Database migrations

Versioned migrations live in `backend/src/main/resources/db/migration`. Their filenames and Flyway's `flyway_schema_history` table are the authoritative record of available and applied schema changes; the README deliberately does not duplicate that version history.

For an existing database restored from an unversioned legacy dump, enable `FLYWAY_BASELINE_ON_MIGRATE` for its first Flyway-managed startup. Flyway records the configured baseline, skips migrations represented by that existing schema, and applies every pending migration after it. Disable the setting again as soon as the history table exists; leaving it enabled removes Flyway's protection against accidentally adopting an unrelated non-empty database. A new empty database runs the complete migration chain and does not require baselining.

Development Compose enables the legacy-baseline switch by default because it mounts `dump.sql`. Production defaults it to `false` and requires an explicit, temporary opt-in. Flyway owns only the `public` schema, validates migration names and checksums on startup, and cannot run `clean`. Hibernate validates the resulting schema in development, tests, and production and does not modify it automatically.

For every future schema change, add a new versioned migration. Never edit or rename a migration that has already been applied to a shared database. Check `flyway_schema_history` when diagnosing migration state.

### Global search maintenance

The normalized `global_search_document` is refreshed asynchronously after a successful transaction whose service method is marked with `@SearchIndexAffecting`. Mark every new mutation that changes a searchable title, metadata value, relationship, or full-text value explicitly; refresh behavior does not depend on method naming.

Imports and maintenance scripts that write directly to PostgreSQL bypass application events. After such a write completes, an `admin` or `api-service` may queue a concurrent rebuild with `POST /search/refresh`. A `202 Accepted` response means the rebuild was queued and may be coalesced with other pending refresh requests.

Full-text support is document-driven: any future search-document branch that provides both `full_text` and a compatible `full_text_vector` participates in full-text matching without a repository change. Add its schema and normalized search-document branch in a new Flyway migration.

## Production

Create the production environment file and replace its placeholder password:

```bash
cp .env.prod.example .env.prod
```

Also configure `TILES_DIR` and `BACKUP_DIR` there if their default directories should not be used. The configured PostGIS image supports both AMD64 and ARM64.

Normal production startup does not require or mount a database dump. To initialize a fresh production volume from an unversioned legacy dump, place it at `./dump.sql` and add `compose.prod.restore.yaml` to the first startup command. The restore overlay prepares the dump owner and mounts the dump; PostgreSQL runs these initialization scripts only while creating an empty `postgres_data` volume. Omit the restore overlay from every subsequent startup.

For the first deployment after restoring an unversioned legacy dump:

1. Take and verify a database backup.
2. Set `FLYWAY_BASELINE_ON_MIGRATE=true` in `.env.prod`.
3. Initialize the new volume and start the application with the restore overlay:

```bash
docker compose --env-file .env.prod \
  -f compose.prod.yaml \
  -f compose.prod.build.yaml \
  -f compose.prod.restore.yaml \
  up -d --build --wait
```

4. Verify that `flyway_schema_history` contains the expected baseline and that every subsequent migration completed successfully:

```bash
docker compose --env-file .env.prod -f compose.prod.yaml exec -T database \
  sh -lc 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c \
  "SELECT installed_rank, version, description, type, success FROM public.flyway_schema_history ORDER BY installed_rank"'
```

5. Change `FLYWAY_BASELINE_ON_MIGRATE=false` and recreate the application without `compose.prod.restore.yaml`:

```bash
docker compose --env-file .env.prod \
  -f compose.prod.yaml \
  -f compose.prod.build.yaml \
  up -d --force-recreate --wait
```

Re-enable baselining and the restore overlay only when deliberately initializing another empty volume from a known legacy dump without Flyway history.

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
