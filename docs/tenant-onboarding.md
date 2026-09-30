# Tenant onboarding

1. Platform admin calls `POST /api/tenants`.
2. Tenant service stores the tenant and default settings.
3. Small tenants stay on the shared database.
4. Large tenants get `CREATE DATABASE tenant_<slug>` and the shared schema.
5. Auth service creates the tenant admin user with the same JWT issuer.
