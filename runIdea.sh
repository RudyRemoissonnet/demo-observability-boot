#!/usr/bin/env bash

# run services
docker compose --profile ide up -d

# run idea
docker compose exec ide idea
