# AI Audit Report

## 1. Task Information

| Field | Content |
| --- | --- |
| Date | 2026-07-03 |
| Workspace | `/Users/jiduckiess/Documents/SeminarTesting` |
| SUT | EShop SUT |
| Topic | Database Testing |
| Tool | DbUnit |
| Purpose | Create a step-by-step Markdown guide for installing and using DbUnit with the EShop SUT |
| Output artifact | `DBUNIT_STEP_BY_STEP_GUIDE.md` |

## 2. User Request

User request recorded before prompt-by-prompt audit logging:

```text
hướng dẫn cài và dùng dbunit từng bước như bạn chỉ cho tôi
```

## 3. AI Assistance Summary

AI was used to create a step-by-step Markdown guide for installing and using DbUnit with the EShop SUT.

The guide explains:

- How to check Java and Maven installation.
- How to install Java and Maven using Homebrew.
- How to reset the EShop SQLite database.
- How to create a small Maven project for DbUnit.
- How to configure `pom.xml` with JUnit, DbUnit, and SQLite JDBC dependencies.
- How to create a DbUnit XML dataset.
- How to write a first DbUnit test.
- How to run the test with `mvn test`.
- What screenshots, command lines, and evidence should be captured for Stage S3.

## 4. Files Created or Modified

| File | Action | Purpose |
| --- | --- | --- |
| `DBUNIT_STEP_BY_STEP_GUIDE.md` | Created | Main step-by-step guide for installing and using DbUnit with EShop SUT |
| `dbunit-demo/pom.xml` | Created/Fixed | Maven configuration for DbUnit demo project |
| `dbunit-demo/src/test/java/com/eshop/dbunit/EshopDbUnitTest.java` | Created | Example DbUnit test class |
| `dbunit-demo/src/test/resources/datasets/initial-dataset.xml` | Created | DbUnit XML dataset used by the test |
| `AI_Audit_Report_DBUnit_Guide.md` | Created | Audit record for this AI-assisted task |

## 5. Commands Mentioned or Used

Commands included in the guide:

```bash
java -version
mvn -version
brew install openjdk@17
brew install maven
cd /Users/jiduckiess/Documents/SeminarTesting/backend
node database.js
cd /Users/jiduckiess/Documents/SeminarTesting/dbunit-demo
mvn test
```

Commands executed during verification:

```bash
mvn test
```

## 6. Verification Result

The DbUnit demo test was executed successfully.

Observed result:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 7. Issues Encountered

| Issue | Cause | Resolution |
| --- | --- | --- |
| `Non-parseable POM ... in epilog non whitespace content is not allowed` | Extra Markdown content was accidentally copied into `pom.xml` after `</project>` | Removed the extra content so `pom.xml` only contains valid Maven XML |
| Missing dataset file | `EshopDbUnitTest.java` expected `src/test/resources/datasets/initial-dataset.xml` | Created `initial-dataset.xml` |
| Maven could not write to `~/.m2/repository` in sandbox | Sandbox permission restriction | Re-ran Maven with approved elevated permission |

## 8. Human Review Notes

- The Markdown guide is intended for coursework/seminar documentation and hands-on practice.
- The guide focuses on a minimal DbUnit proof of concept, not a full production testing framework.
- The EShop backend database is SQLite: `backend/database.sqlite`.
- Running DbUnit with `CLEAN_INSERT` can reset tables involved in the dataset, so it should be used carefully during demos.

## 9. AI Disclosure Statement

AI was used to draft the DbUnit setup guide, create supporting demo files, diagnose Maven/DbUnit errors, and summarize verification evidence. The human user reviewed the workflow interactively and requested this audit record.

---

## 10. Audit Entry - Tonic.ai Testing Guide

| Field | Content |
| --- | --- |
| Date | 2026-07-04 |
| Workspace | `/Users/jiduckiess/Documents/SeminarTesting` |
| SUT | EShop SUT |
| Topic | Database Testing and Test Data Management |
| Tool | Tonic.ai Structural |
| Purpose | Create a step-by-step guide for testing EShop data with Tonic.ai |
| Output artifact | `TONIC_AI_TESTING_GUIDE.md` |

### 10.1 User Prompt

```text
tiếp theo hãy cho tôi hướng dẫn test bằng tonic.ai (ghi vào AI audit report)
```

### 10.2 AI Assistance Summary

AI created a Markdown guide that explains how to use Tonic.ai Structural with the EShop SUT. Because the EShop project uses SQLite, the guide recommends a CSV-based workflow:

- Export EShop SQLite tables to CSV.
- Upload CSV files into Tonic.ai using Local files/File connector.
- Configure generators for sensitive columns.
- Run data generation.
- Download generated CSV output.
- Compare original and generated data for seminar evidence.

### 10.3 Files Created or Modified

| File | Action | Purpose |
| --- | --- | --- |
| `TONIC_AI_TESTING_GUIDE.md` | Created | Step-by-step Tonic.ai testing guide for EShop |
| `AI_Audit_Report.md` | Modified | Added this audit entry |

### 10.4 Sources Consulted

Official Tonic.ai documentation was checked before writing the guide:

- `https://docs.tonic.ai/app`
- `https://docs.tonic.ai/app/quick-start-guide`
- `https://docs.tonic.ai/app/setting-up-your-database/file-connector`
- `https://docs.tonic.ai/app/generation/generators`
- `https://docs.tonic.ai/app/workflows/data-generation-run-job`

### 10.5 Verification Status

| Item | Status |
| --- | --- |
| Tonic.ai guide file created | Completed |
| Official documentation checked | Completed |
| Tonic.ai account/free trial tested | Not executed |
| CSV upload to Tonic.ai tested | Not executed |
| Data generation job executed | Not executed |

### 10.6 AI Disclosure Statement

AI was used to research official Tonic.ai documentation, adapt the workflow to the EShop SQLite database, create the step-by-step Markdown guide, and record this audit entry.

---

## 11. Audit Entry - Database Rider Step-by-Step Guide

| Field | Content |
| --- | --- |
| Date | 2026-07-04 |
| Workspace | `/Users/jiduckiess/Documents/SeminarTesting` |
| SUT | EShop SUT |
| Topic | Database Testing |
| Tool | Database Rider |
| Purpose | Create a step-by-step guide and runnable demo for using Database Rider with the EShop SQLite database |
| Output artifact | `DATABASE_RIDER_STEP_BY_STEP_GUIDE.md` |

### 11.1 User Prompt

```text
hướng dẫn cài và dùng Database Rider từng bước (ghi audit)
```

### 11.2 AI Assistance Summary

AI created a Markdown guide and a runnable Maven demo project for Database Rider. The guide explains:

- How Database Rider relates to DBUnit.
- How Database Rider can test the EShop SQLite database even though EShop is a Node.js project.
- How to create a Java/Maven test project.
- How to define a YAML dataset for EShop tables.
- How to run a Database Rider test with `mvn test`.
- What screenshots and command outputs should be captured for seminar evidence.

### 11.3 Files Created or Modified

| File | Action | Purpose |
| --- | --- | --- |
| `DATABASE_RIDER_STEP_BY_STEP_GUIDE.md` | Created | Step-by-step Database Rider guide |
| `database-rider-demo/pom.xml` | Created | Maven configuration for Database Rider demo |
| `database-rider-demo/src/test/resources/datasets/eshop-users.yml` | Created | YAML dataset for EShop database test |
| `database-rider-demo/src/test/java/com/eshop/databaserider/EshopDatabaseRiderTest.java` | Created/Fixed | Runnable Database Rider/JUnit test |
| `AI_Audit_Report.md` | Modified | Added this audit entry |

### 11.4 Sources Consulted

Official Database Rider documentation was checked before writing the guide:

- `https://github.com/database-rider/database-rider`
- `https://database-rider.github.io/database-rider/`

### 11.5 Verification Result

Command executed:

```bash
cd /Users/jiduckiess/Documents/SeminarTesting/database-rider-demo
mvn test
```

Observed result:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 11.6 Issues Encountered

| Issue | Cause | Resolution |
| --- | --- | --- |
| Maven could not write to `~/.m2/repository` in sandbox | Sandbox permission restriction | Re-ran Maven with approved elevated permission |
| Compile error: `RiderDSL` and `DataSetConfig` not found | Initial import package names did not match Database Rider 1.44.0 | Updated imports to `com.github.database.rider.core.dsl.RiderDSL` and `com.github.database.rider.core.configuration.DataSetConfig` |

### 11.7 AI Disclosure Statement

AI was used to check Database Rider documentation, create the step-by-step guide, scaffold the runnable Maven demo, fix compile errors, run verification, and record this audit entry.
