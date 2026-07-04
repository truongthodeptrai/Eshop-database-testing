import { execFileSync } from "node:child_process";
import { mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { join } from "node:path";

const baseUrl = process.env.ESHOP_BASE_URL || "http://localhost:3000";
const container = process.env.ESHOP_BACKEND_CONTAINER || "eshop-sut-backend-1";
const csvPath =
  process.env.TONIC_FABRICATE_CSV || new URL("../tonic-ai/ai-variant-products.csv", import.meta.url).pathname;
const artifactsDir = new URL("../artifacts/", import.meta.url).pathname;
mkdirSync(artifactsDir, { recursive: true });

const runId = `TONIC-FABRICATE-${Date.now()}`;
const started = performance.now();

function parseCsv(text) {
  const [headerLine, ...lines] = text.trim().split(/\r?\n/);
  const headers = headerLine.split(",");
  return lines.map((line) => {
    const values = line.split(",");
    return Object.fromEntries(headers.map((header, index) => [header, values[index] ?? ""]));
  });
}

function asProduct(row) {
  return {
    name: row.name,
    price: Number(row.price),
    description: row.description,
    imageUrl: row.imageUrl,
    category_id: Number(row.category_id),
  };
}

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
  const rows = parseCsv(readFileSync(csvPath, "utf8"));
  if (rows.length !== 4) {
    throw new Error(`Expected 4 Fabricate data rows, found ${rows.length}`);
  }

  const adminToken = await login("admin@eshop.com", "Admin123!");
  const userEmail = `tonic-ai-${Date.now()}@example.test`;
  await request("POST", "/api/register", null, {
    name: "Tonic AI User",
    email: userEmail,
    password: "User123!",
  });
  const userToken = await login(userEmail, "User123!");
  const setupTimeSeconds = Number(((performance.now() - started) / 1000).toFixed(3));
  const scenarioStarted = performance.now();

  const invalidAdminImport = await request("POST", "/api/admin/import-products", adminToken, {
    products: rows.slice(0, 3).map(asProduct),
  });

  const nonAdminImport = await request("POST", "/api/admin/import-products", userToken, {
    products: [asProduct(rows[3])],
  });

  const dbPath = join(artifactsDir, `${runId}.sqlite`);
  execFileSync("docker", ["cp", `${container}:/app/database.sqlite`, dbPath]);
  const dbRows = execFileSync(
    "sqlite3",
    [
      "-json",
      dbPath,
      "SELECT id, name, price FROM products WHERE name LIKE 'TONIC-FABRICATE-%' ORDER BY id;",
    ],
    { encoding: "utf8" },
  );

  const products = JSON.parse(dbRows || "[]");
  const metrics = {
    runId,
    sourceCsv: csvPath,
    baseUrl,
    setupTimeSeconds,
    runTimeSeconds: Number(((performance.now() - scenarioStarted) / 1000).toFixed(3)),
    invalidAdminImport,
    nonAdminImport,
    databaseRows: products,
    observedFailures: [
      invalidAdminImport.json.inserted > 0
        ? "FR-16 rollback failure: Fabricate import inserted rows even though the batch contained an invalid missing-name row."
        : null,
      products.some((product) => Number(product.price) < 0)
        ? "FR-16 validation failure: Fabricate generated negative-price product was persisted."
        : null,
      nonAdminImport.status === 200 && nonAdminImport.json.inserted === 1
        ? "FR-12 authorization failure: a normal user imported the Fabricate non-admin product through /api/admin/import-products."
        : null,
    ].filter(Boolean),
  };

  writeFileSync(
    join(artifactsDir, `${runId}-ai-metrics.json`),
    JSON.stringify(metrics, null, 2),
  );
  console.log(JSON.stringify(metrics, null, 2));
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
