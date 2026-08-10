import React from "react";
import { Link, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import PolicyManagement from "./pages/PolicyManagement";
import ClaimSubmission from "./pages/ClaimSubmission";
import ClaimReview from "./pages/ClaimReview";
import CustomerManagement from "./pages/CustomerManagement";
import TenantAdministration from "./pages/TenantAdministration";

export default function App() {
  return (
    <AuthProvider>
      <nav>
        <Link to="/">Dashboard</Link> | <Link to="/login">Login</Link> | <Link to="/policies">Policies</Link>
      </nav>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<Dashboard />} />
        <Route path="/policies" element={<PolicyManagement />} />
        <Route path="/claims/submit" element={<ClaimSubmission />} />
        <Route path="/claims/review" element={<ClaimReview />} />
        <Route path="/customers" element={<CustomerManagement />} />
        <Route path="/tenant-admin" element={<TenantAdministration />} />
      </Routes>
    </AuthProvider>
  );
}
