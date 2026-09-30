import { useState } from "react";
import api, { errorMessage } from "../services/api";
import { useAuth } from "../hooks/useAuth";

const demos = [
  ["platform-admin", "Platform admin"],
  ["acme-admin", "Acme admin"],
  ["acme-adjuster", "Acme adjuster"],
  ["acme-customer", "Acme customer"],
  ["bigco-admin", "Big Co admin"],
];

export default function Login() {
  const { login } = useAuth();
  const [username, setUsername] = useState("acme-admin");
  const [password, setPassword] = useState("password");
  const [error, setError] = useState("");

  async function submit(event) {
    event.preventDefault();
    setError("");
    try {
      const { data } = await api.post("/auth/token", { username, password });
      login(data);
    } catch (err) {
      setError(errorMessage(err));
    }
  }

  return (
    <div className="login">
      <form className="login-card card stack" onSubmit={submit}>
        <div>
          <div className="brand">Claim Center</div>
          <p className="muted">Multi-tenant claims workspace. Password for every demo account is password.</p>
        </div>
        <label>
          Username
          <input value={username} onChange={(event) => setUsername(event.target.value)} />
        </label>
        <label>
          Password
          <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} />
        </label>
        {error && <div className="error">{error}</div>}
        <button type="submit">Sign in</button>
        <div className="demo">
          {demos.map(([user, label]) => (
            <button key={user} type="button" className="ghost" onClick={() => setUsername(user)}>
              {label}
            </button>
          ))}
        </div>
      </form>
    </div>
  );
}
