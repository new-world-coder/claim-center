import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  scenarios: {
    claims: {
      executor: "constant-vus",
      vus: 50,
      duration: "1m",
    },
  },
};

const tenants = [
  { username: "acme-admin", tenant: "acme" },
  { username: "bigco-admin", tenant: "bigco" },
];

export function setup() {
  return tenants.map((tenant) => {
    const login = http.post(
      "http://localhost:8080/api/auth/token",
      JSON.stringify({ username: tenant.username, password: "password" }),
      { headers: { "Content-Type": "application/json" } },
    );
    return { ...tenant, token: login.json("access_token") };
  });
}

export default function (data) {
  const tenant = data[Math.floor(Math.random() * data.length)];
  const policies = http.get("http://localhost:8080/api/policies", {
    headers: { Authorization: `Bearer ${tenant.token}`, "X-Tenant-Id": tenant.tenant },
  });
  check(policies, { "policies listed": (response) => response.status === 200 });
  const body = policies.json();
  if (Array.isArray(body) && body.length > 0) {
    const claim = http.post(
      "http://localhost:8080/api/claims",
      JSON.stringify({
        policyId: body[0].id,
        amount: 900 + Math.floor(Math.random() * 100),
        description: "Load test claim",
      }),
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${tenant.token}`,
          "X-Tenant-Id": tenant.tenant,
        },
      },
    );
    check(claim, { "claim accepted": (response) => response.status === 200 });
  }
  sleep(0.2);
}
