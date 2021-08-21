#!/bin/bash
set -e

for s in {1..10}; do
  schema=$((SCHEMA_BASE_ID + s))
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL

    CREATE SCHEMA shredder${schema};
    CREATE SEQUENCE shredder${schema}.shredder${schema}_seq;

    CREATE OR REPLACE FUNCTION shredder${schema}.next_id(OUT result bigint) AS \$\$
    DECLARE
        our_epoch bigint := 1577836800000; -- Jan 1st 2020
        seq_id bigint;
        now_millis bigint;
        shard_id int := ${schema};
    BEGIN
        SELECT nextval('shredder${schema}_seq') % 1024 INTO seq_id;
        SELECT FLOOR(EXTRACT(EPOCH FROM clock_timestamp()) * 1000) INTO now_millis;
        result := (now_millis - our_epoch) << 23;
        result := result | (shard_id << 10);
        result := result | (seq_id);
    END;
    \$\$ LANGUAGE PLPGSQL;

    CREATE TABLE shredder${schema}.appuser(
      userid bigint default shredder${schema}.next_id(),
      handle varchar
    );
EOSQL
done