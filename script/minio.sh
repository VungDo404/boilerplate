#!/bin/sh

echo "Waiting for MinIO to start..."
until curl -s -o /dev/null http://127.0.0.1:9000/minio/health/live; do
  sleep 2
done

echo "MinIO is up. Configuring mc..."
mc alias set local http://127.0.0.1:9000 minioadmin minioadmin

echo "Checking if bucket exists..."
if ! mc ls local/public >/dev/null 2>&1; then
  echo "Creating bucket..."
  mc mb local/public
  echo "Setting public access..."
  mc anonymous set public local/public
else
  echo "Bucket already exists. Skipping creation."
fi

echo "✅ MinIO initialization complete."
