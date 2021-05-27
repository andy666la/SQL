#!/bin/bash
psql -h localhost -p $PGPORT $USER"_DB" < create_tables.sql > /dev/null
sleep 5

psql -h localhost -p $PGPORT $USER"_DB" < create_indexes.sql

printf "Query time for unclustered index"
cat <(echo '\timing') queries.sql |psql -h localhost -p $PGPORT $USER"_DB" | grep Time | awk -F "Time" '{print $2;}'

psql -h localhost -p $PGPORT $USER"_DB" < cluster.sql

printf "Query time for clustered index"
cat <(echo '\timing') queries.sql |psql -h localhost -p $PGPORT $USER"_DB" | grep Time | awk -F "Time" '{print $2;}'

