import { Link, Route, Routes } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import api from "../services/api";
import { can, ROLE_LABELS } from "../utils/roles";
import DashboardPage from "../pages/Dashboard";
import PolicyPage from "../pages/PolicyManagement";
import ClaimSubmitPage from "../pages/ClaimSubmission";
import ClaimReviewPage from "../pages/ClaimReview";
import CustomerPage from "../pages/CustomerManagement";
import TenantPage from "../pages/TenantAdministration";

const links = [
  ["/", "Dashboard", ["PLATFORM_ADMIN", "TENANT_ADMIN", "ADJUSTER", "CUSTOMER"]],
  ["/policies", "Policies", ["PLATFORM_ADMIN", "TENANT_ADMIN", "ADJUSTER", "CUSTOMER"]],
  ["/claims/submit", "Submit claim", ["TENANT_ADMIN", "ADJUSTER", "CUSTOMER"]],
  ["/claims/review", "Review claims", ["TENANT_ADMIN", "ADJUSTER"]],
  ["/customers", "Customers", ["TENANT_ADMIN", "ADJUSTER"]],
  ["/tenant-admin", "Tenants", ["PLATFORM_ADMIN"]],
];

export default function Shell() {
  const { session, logout } = useAuth();
  if (!session) return null;

  function signOut() {
    const token = sessionStorage.getItem("access_token");
    const tenantId = sessionStorage.getItem("tenant_id");
    logout();
    api.post("/auth/logout", null, {
      timeout: 4000,
      headers: {
        Authorization: token ? `Bearer ${token}` : undefined,
        "X-Tenant-Id": tenantId || undefined,
      },
    }).catch(() => {
      // The local session is already cleared.
    });
  }
  return (
    <div className="app-shell">
      <aside className="nav">
        <div>
          <div className="brand">Claim Center</div>
          <div>{session.username}</div>
          <div>{ROLE_LABELS[session.role] || session.role}</div>
          <div>
            {session.tenantId} · {session.tier}
          </div>
        </div>
        <nav>
          {links
            .filter(([, , roles]) => can(session.role, roles))
            .map(([path, label]) => (
              <Link key={path} to={path}>
                {label}
              </Link>
            ))}
        </nav>
        <button className="ghost" type="button" onClick={signOut}>
          Log out
        </button>
      </aside>
      <main className="main">
        <Routes>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/policies" element={<PolicyPage />} />
          <Route path="/claims/submit" element={<ClaimSubmitPage />} />
          <Route path="/claims/review" element={<ClaimReviewPage />} />
          <Route path="/customers" element={<CustomerPage />} />
          <Route path="/tenant-admin" element={<TenantPage />} />
        </Routes>
      </main>
    </div>
  );
}
