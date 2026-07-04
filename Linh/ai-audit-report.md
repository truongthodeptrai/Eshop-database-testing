# AI Audit Report

Date: 2026-07-04

## User Request

The user asked for help preparing database testing work for the EShop project.
The required tools were:

- DbUnit
- Database Rider
- Tonic.ai Fabricate

Summary of the user's request:

```text
Help prepare database testing evidence for the current EShop project.
Use DbUnit, Database Rider, and Tonic.ai Fabricate.
Show that the tools are installed and that their hello-world tests pass.
Capture metrics such as setup time, run time, and flake rate.
Make the final report clear enough for a first-year student to understand.
```

The requested milestones emphasized in this audit were:

- M1: Install each tool and pass its official or equivalent hello-world test.
- M5: Capture setup time, run time, and flake rate.

The user also requested that the final report be written in a way that a
first-year student could understand.

## AI Work Summary

The AI assisted with part of the database testing setup and documentation.

The main testing scenario selected was:

```text
Admin imports products from CSV.
```

This scenario was chosen because it tests database behavior without using the
common login, add product, and checkout flow.

The work used a separate Java Maven testing folder called:

```text
database-testing/
```

This folder is outside the main EShop application. It was used as a testing
harness because DbUnit and Database Rider are Java-based tools, while the EShop
application itself is a Node.js, Express, React, and SQLite project.

The AI helped prepare:

- DbUnit hello-world test.
- Database Rider hello-world test.
- Explanations and corrections around the Java EShop scenario test for CSV product import.
- Tonic Fabricate CSV test data reference.
- Helper scripts for timing and metrics.
- A student-friendly database testing report.
- An explanation of what the helper JavaScript files do.

## Files Created or Modified

The following files were involved in the AI-assisted work. This list does not
mean every file was fully authored by the AI.

### Database testing harness

```text
database-testing/pom.xml
```

Purpose:

```text
Defines the Maven project and installs DbUnit, Database Rider, SQLite JDBC,
JUnit, and related test dependencies.
```

```text
database-testing/src/test/java/com/eshop/dbtest/DbUnitHelloWorldTest.java
```

Purpose:

```text
Small DbUnit hello-world test used to prove DbUnit is installed and can
load/check a database dataset.
```

```text
database-testing/src/test/java/com/eshop/dbtest/DatabaseRiderHelloWorldTest.java
```

Purpose:

```text
Small Database Rider hello-world test used to prove Database Rider is installed
and can load/check a dataset.
```

### Test data

```text
database-testing/src/test/resources/dbunit/hello-users.xml
database-testing/src/test/resources/rider/products.yml
database-testing/src/test/resources/rider/expected-products.yml
database-testing/tonic-ai/ai-variant-products.csv
```

Purpose:

```text
Provide small datasets for the hello-world tests and the AI-generated CSV import
variant.
```

### Helper scripts

```text
database-testing/scripts/run-eshop-csv-import-scenario.mjs
database-testing/scripts/run-tonic-fabricate-ai-scenario.mjs
```

Purpose:

```text
Collect timing and stability metrics for the normal scenario and the
AI-generated data scenario.
```

These scripts are helper scripts only. The main bug-finding proof is the Java
Maven test:

```text
EshopCsvImportScenarioTest
```

### Documentation

```text
database-testing/README.md
docs/database-testing-report.md
docs/ai-audit-report.md
```

Purpose:

```text
Explain how to run the tests, what the results mean, and what was changed after
human review.
```

### Screenshots and artifacts

```text
docs/screenshots/
database-testing/artifacts/
```

Purpose:

```text
Store visual evidence and metrics output from test runs.
```

## Issues Encountered

One important issue was found during human review.

At first, the report described 3 bugs, but the Maven command appeared to pass:

```bash
mvn test
```

The human reviewer noticed that this was confusing and pointed out that the
bugs should be found by DbUnit and Database Rider style tests, not only by the
helper JavaScript files or written explanation.

After that review, the AI changed the Java EShop scenario test so that it
asserts the correct expected behavior:

- Invalid CSV rows should cause the whole import to rollback.
- Negative prices should not be saved.
- Normal users should not access admin import APIs.
- Tonic Fabricate data should be tested through the same Java scenario.

After the correction, the simple command:

```bash
mvn test
```

still passes because it only runs the hello-world tool installation tests.

The real EShop scenario command:

```bash
mvn test -q -Deshop.e2e=true \
  -Deshop.baseUrl=http://localhost:3000 \
  -Deshop.backendContainer=eshop-sut-backend-1 \
  -Dtest=EshopCsvImportScenarioTest
```

now fails while the EShop bugs still exist. This is the correct result for a
bug-finding test.

Another issue was that some early Tonic-related files were not needed anymore.
The human reviewer asked for unused files to be removed, so the report was
updated to focus on the final CSV data and the final screenshot evidence.

## Human Review

Human review was important in this work.

The human reviewer:

- Created or completed the Tonic Fabricate account/setup work.
- Checked the Maven test output manually.
- Found that the first version of the report was misleading because the test
  command passed while the report claimed bugs were found.
- Asked for the report to be rewritten for a first-year student audience.
- Asked for unused files to be removed.
- Asked for the helper JavaScript files to be explained clearly.
- Asked for the final documentation to show that the real bug evidence comes
  from the Java Maven EShop scenario test.

The final documentation should therefore be read as AI-assisted work with human
review and correction, not as fully independent AI completion.
