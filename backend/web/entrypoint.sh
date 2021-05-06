#!/bin/bash
echo "Waiting for postgres..."
while ! nc -z db 5432; do
sleep 0.1
done

flask db init
flask db migrate
flask db upgrade
flask run --host=0.0.0.0