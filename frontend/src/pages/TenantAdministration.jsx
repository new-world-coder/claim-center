import { useEffect, useState } from "react";
import api, { errorMessage } from "../services/api";

export default function TenantAdministration() {
  const [rows, setRows] = useState([]);
  const [form, setForm] = useState({ name: "", tier: "SMALL", adminUsername: "", adminPassword: "password" });
  const [error, setError] = useState("");

  async function load() {
    const response = await api.get("/tenants");
    setRows(response.data);
  }

  useEffect(() => { load().catch((err) => setError(errorMessage(err))); }, []);

  async function create(event) {
    event.preventDefault();
    setError("");
    try {
      await api.post("/tenants", form);
      setForm({ name: "", tier: "SMALL", adminUsername: "", adminPassword: "password" });
      await load();
    } catch (err) {
      setError(errorMessage(err));
    }
  }

  return (
    <div className="stack">
      <h1>Tenant administration</h1>
      <p className="muted">Small tenants share the primary database. Large tenants get a dedicated database.</p>
      <form className="panel" onSubmit={create}>
        <input placeholder="Company name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <select value={form.tier} onChange={(e) => setForm({ ...form, tier: e.target.value })}>
          <option value="SMALL">Small / shared database</option>
          <option value="LARGE">Large / dedicated database</option>
        </select>
        <input placeholder="Admin username" value={form.adminUsername} onChange={(e) => setForm({ ...form, adminUsername: e.target.value })} />
        <input placeholder="Admin password" value={form.adminPassword} onChange={(e) => setForm({ ...form, adminPassword: e.target.value })} />
        <button type="submit">Onboard tenant</button>
        {error && <div className="error">{error}</div>}
      </form>
      <div className="panel">
        <table>
          <thead><tr><th>Tenant</th><th>Tier</th><th>Database</th><th>Status</th></tr></thead>
          <tbody>
            {rows.map((row) => (
              <tr key={row.tenantId}><td>{row.name}</td><td>{row.tier}</td><td>{row.dbName || "shared"}</td><td>{row.status}</td></tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
