#!/bin/sh

set -e

TIMESTAMP_DATE=$(date +"%Y-%m-%d")
TIMESTAMP_TIME=$(date +"%H%M")

BACKUP_DIR=/backups

mkdir -p "$BACKUP_DIR/$TIMESTAMP_DATE"

BACKUP_FILE="$BACKUP_DIR/$TIMESTAMP_DATE/haeuserbuch_db_backup_$TIMESTAMP_DATE-$TIMESTAMP_TIME.sql"

until pg_isready -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME"; do
  echo "Waiting for database..."
  echo ""
  sleep 5
done

pg_dump -U "${DB_USER}" -h "${DB_HOST}" -d "${DB_NAME}" -f "$BACKUP_FILE"

gzip "$BACKUP_FILE"

echo "[$TIMESTAMP_DATE $TIMESTAMP_TIME] Backup completed: $BACKUP_FILE.gz"
echo ""
