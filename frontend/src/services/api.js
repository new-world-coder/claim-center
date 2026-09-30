import axios from "axios";

const api = axios.create({ baseURL: "/api" });

api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem("access_token");
  const tenantId = sessionStorage.getItem("tenant_id");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  if (tenantId) {
    config.headers["X-Tenant-Id"] = tenantId;
  }
  return config;
});

export function errorMessage(error) {
  return error?.response?.data?.error || error.message || "Request failed";
}

export default api;
