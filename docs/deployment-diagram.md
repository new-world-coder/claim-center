# Deployment

```mermaid
graph TD
A[GitHub Actions] --> B[Artifact Registry]
B --> C[GKE]
C --> D[Ingress Load Balancer]
C --> E[Microservices]
E --> F[Cloud SQL PostgreSQL]
```
