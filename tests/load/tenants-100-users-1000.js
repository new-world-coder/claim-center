import http from "k6/http";
import { check, sleep } from "k6";

export const options = { vus: 1000, duration: "2m" };

export default function () {
  const tenantId = `tenant-${Math.floor(Math.random() * 100)}`;
  const res = http.post("http://localhost:8080/api/claims", JSON.stringify({ name: "high-volume-claim" }), { headers: { "Content-Type": "application/json", "X-Tenant-Id": tenantId } });
  check(res, { "status is 200": (r) => r.status === 200 || r.status === 201 });
  sleep(0.1);
}
