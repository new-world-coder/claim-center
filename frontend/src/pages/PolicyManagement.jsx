import { useEffect, useState } from "react";
import api, { errorMessage } from "../services/api";
import { useAuth } from "../hooks/useAuth";
import { can } from "../utils/roles";

export default function PolicyManagement() {
  const { session } = useAuth();
  const [rows, setRows] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [form, setForm] = useState({ policyNumber: "", customerId: "", status: "ACTIVE", coverageAmount: "10000" });
  const [error, setError] = useState("");

  async function load() {
    const policies = await api.get("/policies");
    setRows(policies.data);
    if (can(session.role, ["TENANT_ADMIN", "ADJUSTER"])) {
      const customerResponse = await api.get("/customers");
      setCustomers(customerResponse.data);
      if (!form.customerId && customerResponse.data[0]) {
        setForm((current) => ({ ...current, customerId: customerResponse.data[0].id }));
      }
    }
  }

  useEffect(() => { load().catch((err) => setError(errorMessage(err))); }, []);

  async function create(event) {
    event.preventDefault();
    setError("");
    try {
      await api.post("/policies", { ...form, coverageAmount: Number(form.coverageAmount) });
      setForm({ ...form, policyNumber: "" });
      await load();
    } catch (err) {
      setError(errorMessage(err));
    }
  }

  return (
    <div className="stack">
      <h1>Policies</h1>
      {can(session.role, ["TENANT_ADMIN", "ADJUSTER"]) && (
        <form className="panel" onSubmit={create}>
          <input placeholder="Policy number" value={form.policyNumber} onChange={(e) => setForm({ ...form, policyNumber: e.target.value })} />
          <select value={form.customerId} onChange={(e) => setForm({ ...form, customerId: e.target.value })}>
            {customers.map((customer) => <option key={customer.id} value={customer.id}>{customer.fullName}</option>)}
          </select>
          <input value={form.coverageAmount} onChange={(e) => setForm({ ...form, coverageAmount: e.target.value })} />
          <button type="submit">Create policy</button>
          {error && <div className="error">{error}</div>}
        </form>
      )}
      <div className="panel">
        <table>
          <thead><tr><th>Number</th><th>Status</th><th>Coverage</th></tr></thead>
          <tbody>
            {rows.map((row) => <tr key={row.id}><td>{row.policyNumber}</td><td>{row.status}</td><td>{row.coverageAmount}</td></tr>)}
          </tbody>
        </table>
      </div>
    </div>
  );
}
