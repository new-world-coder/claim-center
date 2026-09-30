import { createContext, useMemo, useState } from "react";

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => {
    const token = sessionStorage.getItem("access_token");
    if (!token) return null;
    return {
      token,
      username: sessionStorage.getItem("username"),
      role: sessionStorage.getItem("role"),
      tenantId: sessionStorage.getItem("tenant_id"),
      tier: sessionStorage.getItem("tier"),
    };
  });

  const value = useMemo(
    () => ({
      session,
      login(payload) {
        sessionStorage.setItem("access_token", payload.access_token);
        sessionStorage.setItem("username", payload.username);
        sessionStorage.setItem("role", payload.role);
        sessionStorage.setItem("tenant_id", payload.tenant_id);
        sessionStorage.setItem("tier", payload.tier);
        setSession({
          token: payload.access_token,
          username: payload.username,
          role: payload.role,
          tenantId: payload.tenant_id,
          tier: payload.tier,
        });
      },
      logout() {
        sessionStorage.clear();
        setSession(null);
      },
    }),
    [session],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
