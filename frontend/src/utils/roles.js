export const ROLE_LABELS = {
  PLATFORM_ADMIN: "Platform admin",
  TENANT_ADMIN: "Tenant admin",
  ADJUSTER: "Adjuster",
  CUSTOMER: "Customer",
};

export function can(role, allowed) {
  return allowed.includes(role);
}
