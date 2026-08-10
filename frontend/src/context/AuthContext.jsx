import React, { createContext, useMemo, useState } from "react";

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [role, setRole] = useState(localStorage.getItem("role") || "USER");
  const value = useMemo(() => ({ role, setRole }), [role]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
