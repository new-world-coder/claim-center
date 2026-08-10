SHELL := /bin/zsh
KIND_CLUSTER := claim-center-local

.PHONY: start stop build up down kind-up kind-down k8s-apply k8s-delete test

start: build up kind-up k8s-apply

stop: k8s-delete kind-down down

build:
	docker compose -f infra/docker/docker-compose.yml build

up:
	docker compose -f infra/docker/docker-compose.yml up -d

down:
	docker compose -f infra/docker/docker-compose.yml down

kind-up:
	kind get clusters | rg -q "^$(KIND_CLUSTER)$$" || kind create cluster --name $(KIND_CLUSTER) --config infra/k8s/kind-config.yaml

kind-down:
	kind delete cluster --name $(KIND_CLUSTER) || true

k8s-apply:
	kubectl apply -f infra/k8s/base
	kubectl apply -f infra/k8s/hpa

k8s-delete:
	kubectl delete -f infra/k8s/hpa --ignore-not-found
	kubectl delete -f infra/k8s/base --ignore-not-found

test:
	k6 run tests/load/tenants-100-users-1000.js

