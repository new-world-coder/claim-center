import { useEffect, useState } from "react";
import api, { errorMessage } from "../services/api";

export default function ClaimSubmission() {
  const [policies, setPolicies] = useState([]);
  const [form, setForm] = useState({ policyId: "", amount: "1500", description: "Water damage in kitchen" });
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    api.get("/policies").then((response) => {
      setPolicies(response.data);
      if (response.data[0]) setForm((current) => ({ ...current, policyId: response.data[0].id }));
    }).catch((err) => setError(errorMessage(err)));
  }, []);

  async function submit(event) {
    event.preventDefault();
    setError("");
    try {
      const response = await api.post("/claims", { ...form, amount: Number(form.amount) });
      setResult(response.data);
    } catch (err) {
      setError(errorMessage(err));
    }
  }

  return (
    <form className="panel stack" onSubmit={submit}>
      <h1>Submit a claim</h1>
      <select value={form.policyId} onChange={(e) => setForm({ ...form, policyId: e.target.value })}>
        {policies.map((policy) => <option key={policy.id} value={policy.id}>{policy.policyNumber}</option>)}
      </select>
      <input value={form.amount} onChange={(e) => setForm({ ...form, amount: e.target.value })} />
      <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
      <button type="submit">Submit</button>
      {error && <div className="error">{error}</div>}
      {result && (
        <div>
          Created {result.claimNumber} with status {result.status}. Fraud score {result.fraudScore} ({result.fraudBand}).
          {(result.fraudReasons || []).length > 0 && <div>{result.fraudReasons.join("; ")}</div>}
        </div>
      )}
    </form>
  );
}
