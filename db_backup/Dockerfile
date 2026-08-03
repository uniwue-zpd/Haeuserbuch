FROM postgres:17-alpine

COPY backup.sh /usr/local/bin/backup.sh
RUN chmod +x /usr/local/bin/backup.sh

RUN mkdir -p /backups

COPY cron /etc/crontabs/root

CMD ["crond", "-f", "-l", "2"]
