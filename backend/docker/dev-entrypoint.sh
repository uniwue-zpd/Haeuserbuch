#!/usr/bin/env bash

set -Eeuo pipefail

app_pid=""

source_signature() {
  {
    find src -type f \( \
      -name '*.java' -o \
      -name '*.xml' -o \
      -name '*.yml' -o \
      -name '*.yaml' -o \
      -name '*.properties' -o \
      -name '*.sql' \
    \) -print0 | sort -z | xargs -0 sha256sum
    sha256sum pom.xml
  } | sha256sum | awk '{ print $1 }'
}

stop_app() {
  if [[ -n "$app_pid" ]] && kill -0 "$app_pid" 2>/dev/null; then
    kill -TERM "$app_pid"
    wait "$app_pid" || true
  fi
}

shutdown() {
  stop_app
  exit 0
}

trap shutdown INT TERM

while true; do
  previous_signature="$(source_signature)"
  mvn --batch-mode spring-boot:run &
  app_pid=$!

  while kill -0 "$app_pid" 2>/dev/null; do
    sleep 1
    current_signature="$(source_signature)"
    if [[ "$current_signature" != "$previous_signature" ]]; then
      break
    fi
  done

  if ! kill -0 "$app_pid" 2>/dev/null; then
    if wait "$app_pid"; then
      exit 0
    else
      exit $?
    fi
  fi

  echo "Source change detected; restarting Spring Boot."
  stop_app
  sleep 0.5
done
