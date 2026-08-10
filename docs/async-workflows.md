# Asynchronous Claim Workflow

Claim submission emits events consumed by downstream services:

- `claim.submitted` -> Policy validation
- `claim.validated` -> Fraud analysis
- `claim.reviewed` -> Notification service
- `claim.completed` -> Audit service
