import { useEffect, useState } from "react";
import api, { errorMessage } from "../services/api";

function bandClass(band) {
  if (band === "HIGH") return "band-high";
  if (band === "MEDIUM") return "band-medium";
  return "band-low";
}

export default function ClaimReview() {
  const [rows, setRows] = useState([]);
  const [error, setError] = useState("");

  async function load() {
    const response = await api.get("/claims");
    setRows(response.data);
  }

  useEffect(() => { load().catch((err) => setError(errorMessage(err))); }, []);

  async function decide(id, decision) {
    setError("");
    try {
      await api.post(`/claims/${id}/decision`, { decision });
      await load();
    } catch (err) {
      setError(errorMessage(err));
    }
  }

  return (
    <div className="stack">
      <h1>Claim review</h1>
      {error && <div className="error">{error}</div>}
      <div className="panel">
        <table>
          <thead><tr><th>Number</th><th>Status</th><th>Amount</th><th>Score</th><th>Signals</th><th></th></tr></thead>
          <tbody>
            {rows.map((row) => (
              <tr key={row.id}>
                <td>{row.claimNumber}</td>
                <td>{row.status}</td>
                <td>{row.amount}</td>
                <td><span className={bandClass(row.fraudBand)}>{row.fraudScore} {row.fraudBand}</span></td>
                <td>{(row.fraudReasons || []).join("; ") || "None"}</td>
                <td>
                  <button type="button" onClick={() => decide(row.id, "APPROVED")}>Approve</button>
                  <button type="button" className="ghost" onClick={() => decide(row.id, "REJECTED")}>Reject</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
