# EShop Database Testing Harness

This module adapts Java database-testing tools to the current Node.js + SQLite
EShop app.

## M1: installation and hello world

```bash
mvn test
```

This runs:

- `DbUnitHelloWorldTest`: DbUnit flat XML dataset loaded into HSQLDB.
- `DatabaseRiderHelloWorldTest`: Database Rider YAML dataset loaded and asserted.
- `EshopCsvImportScenarioTest` is skipped unless `-Deshop.e2e=true` is set.

## M2/M4: EShop scenario

The scenario is admin CSV product import, not checkout. It exercises FR-16
atomic CSV import validation and FR-12 admin-only authorization.

```bash
mvn test -Deshop.e2e=true \
  -Deshop.baseUrl=http://localhost:3000 \
  -Deshop.backendContainer=eshop-sut-backend-1 \
  -Dtest=EshopCsvImportScenarioTest
```

This command is expected to fail while the current EShop bugs still exist. The
failing assertions are the evidence that the database tools found the bugs.

For a metrics artifact:

```bash
node scripts/run-eshop-csv-import-scenario.mjs
```

The Tonic/Fabricate AI variant lives in `tonic-ai/`. The checked-in
`ai-variant-products.csv` was generated from the Fabricate agent UI after
signing in to `https://fabricate.tonic.ai/`.

Run the Fabricate AI variant:

```bash
node scripts/run-tonic-fabricate-ai-scenario.mjs
```

## Scripts

- `scripts/run-eshop-csv-import-scenario.mjs`: metrics helper only. It creates
  fixed test data, sends it to the running EShop API, copies the SQLite database
  from Docker, and writes a metrics JSON file.
- `scripts/run-tonic-fabricate-ai-scenario.mjs`: does the same thing, but reads
  its test rows from the Fabricate-generated CSV file. It is also a metrics
  helper. The main bug-finding assertions are in the Java Maven test.
