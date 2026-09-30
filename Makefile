SHELL := /bin/bash

.PHONY: start stop test package

start: package
	docker compose -f infra/docker/docker-compose.yml up -d --build

stop:
	docker compose -f infra/docker/docker-compose.yml down

package:
	mvn -B -DskipTests package

test:
	mvn -B test
	cd frontend && npm install && npm run lint && npm run build
