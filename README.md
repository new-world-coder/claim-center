# Claim Center

Insurance operations platform for many carriers. Each carrier is a tenant. Small carriers share a database. Large carriers get a dedicated database. Tenant identity comes from the signed JWT.

## Run locally

```bash
make start
```

Open http://localhost:3000

Demo password for every account is `password`.

| Username | Role | Tenant |
| --- | --- | --- |
| platform-admin | Platform admin | platform |
| acme-admin | Tenant admin | acme, shared database |
| acme-adjuster | Adjuster | acme |
| acme-customer | Customer | acme |
| bigco-admin | Tenant admin | bigco, dedicated database |

Prometheus is on http://localhost:9090 and Grafana on http://localhost:3001 (`admin` / `admin`).

## Where to deploy

Use Google Cloud: GKE or Cloud Run for the services, Cloud SQL for PostgreSQL, and Artifact Registry for images. Terraform for that layout is in `infra/terraform/gcp`.

Vercel is a good place for a React frontend, and you can attach Neon, Supabase, or Aurora Postgres from the Vercel Marketplace. It is not a fit for this backend. Vercel no longer offers its own Postgres product.

The Google Cloud CLI on this machine needs a fresh login before a cloud deploy:

```bash
gcloud auth login
gcloud config set project YOUR_PROJECT
terraform -chdir=infra/terraform/gcp init
terraform -chdir=infra/terraform/gcp apply
```
