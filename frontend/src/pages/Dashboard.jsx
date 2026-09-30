import { useEffect, useState } from "react";
import api from "../services/api";

export default function Dashboard() {
  const [counts, setCounts] = useState({ policies: 0, claims: 0, customers: 0 });
  const [claims, setClaims] = useState([]);

  useEffect(() => {
    Promise.allSettled([api.get("/policies"), api.get("/claims"), api.get("/customers")]).then((results) => {
      const policies = results[0].status === "fulfilled" ? results[0].value.data : [];
      const claimRows = results[1].status === "fulfilled" ? results[1].value.data : [];
      const customers = results[2].status === "fulfilled" ? results[2].value.data : [];
      setCounts({ policies: policies.length, claims: claimRows.length, customers: customers.length });
      setClaims(claimRows.slice(0, 5));
    });
  }, []);

  return (
    <div className="stack">
      <h1>Operations dashboard</h1>
      <div className="grid">
        <div className="card"><h2>{counts.policies}</h2><div>Policies</div></div>
        <div className="card"><h2>{counts.claims}</h2><div>Claims</div></div>
        <div className="card"><h2>{counts.customers}</h2><div>Customers</div></div>
      </div>
      <div className="panel">
        <h2>Recent claims</h2>
        <table>
          <thead><tr><th>Number</th><th>Status</th><th>Amount</th></tr></thead>
          <tbody>
            {claims.map((claim) => (
              <tr key={claim.id}><td>{claim.claimNumber}</td><td>{claim.status}</td><td>{claim.amount}</td></tr>
            ))}
            {claims.length === 0 && <tr><td colSpan="3">No claims yet.</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );
}
