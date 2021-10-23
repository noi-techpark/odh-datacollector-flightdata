#!/bin/bash

SCRIPT_ABSOLUTE_DIR=$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")

docker run -it --rm --name postgres -p 5432:5432 -v "$SCRIPT_ABSOLUTE_DIR/../docker/postgres-init.sql":/docker-entrypoint-initdb.d/postgres-init.sql -e POSTGRES_PASSWORD=flightdata_pass -e POSTGRES_USER=flightdata_user -e POSTGRES_DB=flightdata postgres:13.4
