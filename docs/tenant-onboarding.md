# Tenant Onboarding Flow

1. Create tenant record in `tenants`.
2. Determine tenant tier (shared DB or dedicated DB).
3. Provision schema/database.
4. Seed tenant settings and feature flags.
5. Create tenant admin user and role mappings.
6. Emit onboarding event for audit and notifications.
