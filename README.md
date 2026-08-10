# Claim Center

Claim Center is a production-grade multi-tenant SaaS reference platform for insurance operations, built with a React SPA and Spring Boot microservices.

## Architecture

- Frontend: React + Axios + role-aware routing
- Backend: Spring Boot microservices with OAuth2 JWT validation
- Data: PostgreSQL with hybrid tenancy strategy
- Async: Event-driven workflows (Kafka-compatible topic contracts)
- Observability: Prometheus + Grafana + Spring Actuator
- Platform: Docker Compose (local), Kubernetes (local/prod), GKE (prod)
- IaC: Terraform (Google Cloud)
- CI/CD: GitHub Actions

## Services

- `api-gateway`
- `auth-service`
- `tenant-service`
- `policy-service`
- `claim-service`
- `customer-service`
- `document-service`
- `notification-service`
- `audit-service`
- `frontend`

## Multi-Tenancy Model

- Small tenants: shared PostgreSQL schema with `tenant_id` row isolation.
- Large tenants: dedicated database per tenant.
- Tenant context is extracted from JWT and enforced in service filters/repositories.

## Quick Start

Prerequisites:

- Docker
- Kind or Minikube
- `kubectl`
- Make

Run:

```bash
make start
```

This command builds local artifacts, starts all containers, creates a local Kubernetes cluster (Kind by default), and applies manifests.

## Repository Layout

- `services/` Spring Boot microservices
- `frontend/` React SPA
- `infra/docker/` Docker Compose
- `infra/k8s/` Kubernetes manifests
- `infra/terraform/gcp/` GCP infrastructure
- `.github/workflows/` CI/CD pipelines
- `observability/` Prometheus config and Grafana dashboards
- `tests/` integration/api/load tests (k6)
- `docs/` architecture, sequence, and deployment diagrams

## Security and Quality

- OAuth2 JWT authentication and role-based access control
- Checkstyle, SpotBugs, JaCoCo for backend
- ESLint and Prettier for frontend
- SAST, DAST, and dependency scans in CI

