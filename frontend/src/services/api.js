import axios from "axios";

const api = axios.create({ baseURL: "/api" });
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("access_token");
  const tenantId = localStorage.getItem("tenant_id");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  if (tenantId) config.headers["X-Tenant-Id"] = tenantId;
  return config;
});

export default api;
