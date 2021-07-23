#!/bin/sh
read -p "Database host: " dbhost
read -p "Database name: " dbname
read -p "Database username: " dbusername
read -p "Database password: " dbpassword

for file in *.sql; do
    echo "Executing $f.."
    mysql --user=dbusername --password=dbpassword --host=dbhost dbname < $f > migration-log.txt
done
