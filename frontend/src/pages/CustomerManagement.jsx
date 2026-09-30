import { useEffect, useState } from "react";
import api, { errorMessage } from "../services/api";

export default function CustomerManagement() {
  const [rows, setRows] = useState([]);
  const [form, setForm] = useState({ fullName: "", email: "", phone: "" });
  const [error, setError] = useState("");

  async function load() {
    const response = await api.get("/customers");
    setRows(response.data);
  }

  useEffect(() => { load().catch((err) => setError(errorMessage(err))); }, []);

  async function create(event) {
    event.preventDefault();
    try {
      await api.post("/customers", form);
      setForm({ fullName: "", email: "", phone: "" });
      await load();
    } catch (err) {
      setError(errorMessage(err));
    }
  }

  return (
    <div className="stack">
      <h1>Customers</h1>
      <form className="panel" onSubmit={create}>
        <input placeholder="Full name" value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} />
        <input placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input placeholder="Phone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
        <button type="submit">Add customer</button>
        {error && <div className="error">{error}</div>}
      </form>
      <div className="panel">
        <table>
          <thead><tr><th>Name</th><th>Email</th><th>Phone</th></tr></thead>
          <tbody>{rows.map((row) => <tr key={row.id}><td>{row.fullName}</td><td>{row.email}</td><td>{row.phone}</td></tr>)}</tbody>
        </table>
      </div>
    </div>
  );
}
