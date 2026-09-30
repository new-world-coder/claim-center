import { Link, Navigate, Route, Routes, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
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
  const navigate = useNavigate();
  if (!session) return <Navigate to="/login" replace />;
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
        <button
          className="ghost"
          onClick={() => {
            logout();
            navigate("/login");
          }}
        >
          Sign out
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
