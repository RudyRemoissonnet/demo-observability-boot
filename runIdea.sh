#!/usr/bin/env bash

# run services
docker compose up -d

# run idea
docker compose exec ide idea
