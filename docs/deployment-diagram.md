# Deployment

Google Cloud is the deployment target. Vercel is a good host for the React UI alone, but it does not run these Spring Boot services, and Vercel Postgres is no longer a first-party product.

```mermaid
graph TD
  GH[GitHub Actions] --> AR[Artifact Registry]
  AR --> GKE[GKE]
  GKE --> LB[Load balancer]
  GKE --> Services[Microservices]
  Services --> SQL[Cloud SQL PostgreSQL]
  Services --> Prom[Prometheus]
  Prom --> Graf[Grafana]
```
