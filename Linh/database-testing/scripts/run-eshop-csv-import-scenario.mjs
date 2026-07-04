import { execFileSync } from "node:child_process";
import { mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { join } from "node:path";

const baseUrl = process.env.ESHOP_BASE_URL || "http://localhost:3000";
const container = process.env.ESHOP_BACKEND_CONTAINER || "eshop-sut-backend-1";
const artifactsDir = new URL("../artifacts/", import.meta.url).pathname;
mkdirSync(artifactsDir, { recursive: true });

const runId = `DBT-METRICS-${Date.now()}`;
const started = performance.now();

async function request(method, path, token, body) {
  const response = await fetch(`${baseUrl}${path}`, {
    method,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await response.text();
  let json = null;
  try {
    json = JSON.parse(text);
  } catch {
    json = { raw: text };
  }
  return { status: response.status, json, text };
}

async function login(email, password) {
  const response = await request("POST", "/api/login", null, { email, password });
  if (response.status !== 200 || !response.json.token) {
    throw new Error(`Login failed for ${email}: ${response.status} ${response.text}`);
  }
  return response.json.token;
}

async function main() {
  const adminToken = await login("admin@eshop.com", "Admin123!");
  const userEmail = `dbt-metrics-${Date.now()}@example.test`;
  await request("POST", "/api/register", null, {
    name: "DBT Metrics User",
    email: userEmail,
    password: "User123!",
  });
  const userToken = await login(userEmail, "User123!");
  const setupTimeSeconds = Number(((performance.now() - started) / 1000).toFixed(3));
  const scenarioStarted = performance.now();

  const invalidAdminImport = await request("POST", "/api/admin/import-products", adminToken, {
    products: [
      {
        name: `${runId}-valid`,
        price: 123456,
        description: "valid row before invalid rows",
        imageUrl: "",
        category_id: 1,
      },
      {
        name: "",
        price: 777,
        description: "missing name should rollback entire import",
        imageUrl: "",
        category_id: 1,
      },
      {
        name: `${runId}-negative`,
        price: -9000,
        description: "negative price should be rejected",
        imageUrl: "",
        category_id: 1,
      },
    ],
  });

  const nonAdminImport = await request("POST", "/api/admin/import-products", userToken, {
    products: [
      {
        name: `${runId}-non-admin`,
        price: 234567,
        description: "non-admin should not be allowed to import",
        imageUrl: "",
        category_id: 1,
      },
    ],
  });

  const dbPath = join(artifactsDir, `${runId}.sqlite`);
  execFileSync("docker", ["cp", `${container}:/app/database.sqlite`, dbPath]);
  const dbRows = execFileSync(
    "sqlite3",
    [
      "-json",
      dbPath,
      `SELECT id, name, price FROM products WHERE name LIKE '${runId}%' ORDER BY name;`,
    ],
    { encoding: "utf8" },
  );

  const products = JSON.parse(dbRows || "[]");
  const metrics = {
    runId,
    baseUrl,
    setupTimeSeconds,
    runTimeSeconds: null,
    invalidAdminImport,
    nonAdminImport,
    databaseRows: products,
    observedFailures: [
      invalidAdminImport.json.inserted === 2
        ? "FR-16 rollback failure: valid rows were inserted even though the import contained an invalid row."
        : null,
      products.some((product) => product.price < 0)
        ? "FR-16 validation failure: a negative price was persisted."
        : null,
      nonAdminImport.status === 200
        ? "FR-12 authorization failure: a normal user imported products through /api/admin/import-products."
        : null,
    ].filter(Boolean),
  };
  metrics.runTimeSeconds = Number(((performance.now() - scenarioStarted) / 1000).toFixed(3));

  writeFileSync(
    join(artifactsDir, `${runId}-metrics.json`),
    JSON.stringify(metrics, null, 2),
  );
  console.log(JSON.stringify(metrics, null, 2));
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
