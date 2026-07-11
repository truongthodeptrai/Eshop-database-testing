# EShop Database Testing User Guide

This guide explains how to install and use three database-testing tools with the EShop System Under Test (SUT): DbUnit, Database Rider, and Tonic.ai. The local Java demos use Maven, SQLite, JUnit 5, and controlled datasets. Tonic.ai is used to generate privacy-safe test data from exported CSV files.

Unless stated otherwise, run commands from the repository root, meaning the directory that contains this `guide/` folder. The repository can be cloned into any local directory; this guide does not depend on a specific username or absolute path.

## 1. Introduction

EShop is a Node.js and Express e-commerce application that stores its data in SQLite. The database file is:

```text
backend/database.sqlite
```

The three tools have different roles:

```text
DbUnit:        XML dataset  -> SQLite database -> JDBC assertion
Database Rider: YAML dataset -> SQLite database -> JDBC assertion
Tonic.ai:      CSV source   -> masked/generated CSV output
```

DbUnit and Database Rider load controlled data and verify database state. Tonic.ai generates safe replacement data from CSV exports; it does not replace the Java database assertions.

The demo does not replace browser or API testing. It verifies the database used by EShop; it does not directly test React screens or Node.js route code.

Default EShop accounts:

| Account | Email | Password |
| --- | --- | --- |
| Admin | `admin@eshop.com` | `Admin123!` |
| Test user | `test@eshop.com` | `Test1234!` |

## 2. Install

### 2.1 Prerequisites

Install or verify the following tools:

```bash
node -v
npm -v
java -version
mvn -version
sqlite3 --version
```

The demo targets Java 17 or later and requires Maven. Node.js, npm, Java, Maven, and SQLite must be available in the terminal's `PATH`.

#### macOS

```bash
brew install openjdk@17
brew install maven
```

If Homebrew prints a command for adding OpenJDK to `PATH`, run that command, open a new Terminal window, and verify `java -version` again.

#### Linux

On Debian or Ubuntu:

```bash
sudo apt update
sudo apt install -y nodejs npm openjdk-17-jdk maven sqlite3
```

On another Linux distribution, install the same five tools with that distribution's package manager. Then open a new terminal and run the verification commands above.

#### Windows PowerShell

The easiest option is to install the tools with `winget` in PowerShell:

```powershell
winget install --id OpenJS.NodeJS.LTS -e
winget install --id EclipseAdoptium.Temurin.17.JDK -e
winget install --id Apache.Maven -e
winget install --id SQLite.SQLite -e
```

Close and reopen PowerShell after installation, then verify:

```powershell
node --version
npm --version
java -version
mvn --version
sqlite3 --version
```

If `winget` is unavailable or a package cannot be found, install the tools from their official download pages and add their installation directories to the Windows `PATH`. Maven also requires a Java JDK and its `bin` directory in `PATH`; after changing `PATH`, open a new PowerShell window before running the verification commands.

Windows users can use PowerShell for all command blocks in this guide. The `cd`, `node`, `npm`, `mvn`, and `sqlite3` commands are the same. When a command is described as running from the repository root, first open PowerShell in the cloned repository directory.

### 2.2 Install EShop dependencies

From the repository root:

```bash
cd backend
npm install
```

### 2.3 Install Database Rider dependencies

The Java demo is already configured in `database-rider-demo/pom.xml`. Maven downloads Database Rider, JUnit, SQLite JDBC, and logging dependencies on the first test run.

```bash
cd database-rider-demo
mvn test
```

Successful output ends with a result similar to:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 2.4 Install DbUnit dependencies

The DbUnit demo is configured in `dbunit-demo/pom.xml`. Maven downloads DbUnit, JUnit, and SQLite JDBC automatically:

```bash
cd dbunit-demo
mvn test
```

### 2.5 Prepare Tonic.ai

Tonic.ai is a web application, so no local package installation is required for this demo. Prepare CSV source files from the EShop database and upload them to a Tonic workspace. Use one file group per table because files in the same file group must have the same columns:

| File group | Source file |
| --- | --- |
| `users` | `users.csv` |
| `products` | `products.csv` |
| `coupons` | `coupons.csv` |
| `orders` | `orders.csv` |

For a beginner demo, start with `users.csv`. Configure generators for sensitive fields such as name, email, password, address, and phone. Keep identifiers and relationship fields such as `id`, `role`, and `user_id` unchanged when relationships must remain valid.

## 3. First Test

### 3.1 Reset the EShop database

Reset the database before the first run so that the schema and seed data are known:

```bash
cd backend
node database.js
```

Expected messages include:

```text
Database initialized and seeded (Phase 2).
Connected to database
```

Run the command from the `backend` directory as shown. If you are already inside `backend`, use `sqlite3 database.sqlite`, not `sqlite3 backend/database.sqlite`.

### 3.2 Run the DbUnit test

```bash
cd dbunit-demo
mvn test
```

The DbUnit test class is:

```text
dbunit-demo/src/test/java/com/eshop/dbunit/EshopDbUnitTest.java
```

It reads `src/test/resources/datasets/initial-dataset.xml`, applies a `CLEAN_INSERT` operation, and checks that the `users` table contains two rows. DbUnit is the lower-level XML-based database-testing demonstration.

### 3.3 Run the Database Rider test

```bash
cd database-rider-demo
mvn test
```

The test class is:

```text
database-rider-demo/src/test/java/com/eshop/databaserider/EshopDatabaseRiderTest.java
```

The dataset is:

```text
database-rider-demo/src/test/resources/datasets/eshop-users.yml
```

The test opens the SQLite database through JDBC, applies the YAML dataset with Database Rider, and asserts that the `users` table contains two rows.

### 3.4 Verify the result with SQLite

From the repository root:

```bash
cd .
sqlite3 backend/database.sqlite "SELECT id, name, email, role FROM users;"
```

The result should contain the Admin User and Test User rows from the YAML dataset.

### 3.5 Run EShop separately

To demonstrate that EShop uses the same database, start the API in one terminal:

```bash
cd backend
node server.js
```

Start the web frontend in another terminal if the frontend is available in the checkout:

```bash
cd frontend-web
npm install
npm run dev
```

Open the URL printed by Vite, usually `http://localhost:5173`, and log in with the test account. Keep the API terminal running while using the web application.

### 3.6 Run the Tonic.ai data-generation workflow

In Tonic.ai, choose a Files source with Local Filesystem, upload the CSV files, and create separate file groups named `users`, `products`, `coupons`, and `orders`. Resolve any schema-change warning before generation. Then:

1. Open the `users` file group.
2. Assign generators to sensitive columns: Name, Email, Constant or scramble for password, Address, and Phone.
3. Keep `id`, `role`, and foreign-key columns unchanged when the scenario depends on them.
4. Choose `Generate Data` or `Run Generation`.
5. Wait for the job to finish.
6. Download the generated CSV from the completed job or file group download action.

Do not upload an empty `orders.csv`; create an order first or omit that table from the beginner demo. Review the generated CSV before importing it into SQLite. Generated passwords and emails may prevent the original web login from working.

## 4. Advanced Usage

### 4.1 Change a DbUnit or Database Rider dataset

Edit `database-rider-demo/src/test/resources/datasets/eshop-users.yml` to create a different repeatable state. For DbUnit, edit `dbunit-demo/src/test/resources/datasets/initial-dataset.xml`. Then run the relevant Maven test:

```bash
cd database-rider-demo
mvn test
```

Keep foreign-key values consistent. For example, a product's `category_id` must refer to an existing category.

### 4.2 Use Tonic.ai for privacy-safe data

Tonic.ai should be used on a copy or export of the local database. It can replace personally identifiable values while preserving useful formats. A practical mapping is:

| Column | Generator | Keep or replace |
| --- | --- | --- |
| `users.name` | Name | Replace |
| `users.email` | Email | Replace |
| `users.password` | Constant or scramble | Replace |
| `users.shipping_address` | Address | Replace |
| `users.phone` | Phone | Replace |
| `users.id` | Passthrough | Keep for relationships |
| `users.role` | Passthrough | Keep for authorization scenarios |

After generation, inspect the CSV schema and values. Import generated data only into a backup or disposable database. Restore one known test account if the web demo needs login.

### 4.3 Add a database assertion

The current test checks the number of users. A second assertion can verify a specific role:

```java
try (Statement statement = connection.createStatement();
     ResultSet resultSet = statement.executeQuery(
             "SELECT role FROM users WHERE email = 'admin@eshop.com'")) {
    assertEquals("admin", resultSet.getString("role"));
}
```

For an order scenario, first create an order through the EShop API or web application, then verify it with JDBC:

```sql
SELECT user_id, total_amount, status
FROM orders;
```

Expected values should be based on the scenario being demonstrated. Do not claim a checkout result unless the checkout was actually executed.

### 4.4 Protect the original database

Database Rider changes the SQLite file used by the test. Make a backup before experimenting:

```bash
cd .
cp backend/database.sqlite backend/database.before-rider.sqlite
```

On Windows PowerShell, use the equivalent command:

```powershell
Copy-Item backend/database.sqlite backend/database.before-rider.sqlite
```

To return to the normal seeded state, run:

```bash
cd backend
node database.js
```

### 4.5 Understand the test boundary

DbUnit and Database Rider are Java database-testing tools. EShop is a Node.js application, but both can access the same SQLite file. Tonic.ai works from exported files or a configured source connection. The integration point for the local Java demos is the database file, not the programming language:

```text
Node.js EShop API -> backend/database.sqlite <- DbUnit / Database Rider
                                      \
                                       -> CSV export -> Tonic.ai -> generated CSV
```

This makes the demo suitable for database setup and persistence checks. Full end-to-end behavior still requires API or browser actions.

## 5. Troubleshooting

### `mvn: command not found`

On macOS with Homebrew, install Maven and open a new Terminal:

```bash
brew install maven
mvn -version
```

On Windows PowerShell, install Maven with `winget`, close and reopen PowerShell, then verify:

```powershell
winget install --id Apache.Maven -e
mvn --version
```

On Linux, install Maven with the package manager for your distribution, for example `sudo apt install maven` on Debian or Ubuntu.

### DbUnit or Database Rider test fails after a data import

Reset the local database and run only one Java demo at a time:

```bash
cd backend
node database.js
cd ../dbunit-demo
mvn test
cd ../database-rider-demo
mvn test
```

Check that the dataset column names match the SQLite schema and that foreign-key values refer to existing rows.

### `Unable to locate a Java Runtime`

On macOS with Homebrew, install Java 17 and follow Homebrew's `PATH` instruction:

```bash
brew install openjdk@17
java -version
```

On Windows PowerShell:

```powershell
winget install --id EclipseAdoptium.Temurin.17.JDK -e
java -version
```

If Java is installed but the command is still not found, add the JDK `bin` directory to the Windows `PATH`, open a new PowerShell window, and run `java -version` again.

### Maven cannot parse `pom.xml`

The `pom.xml` must contain only one XML document. Open `database-rider-demo/pom.xml` and remove any Markdown, terminal output, or text after `</project>`.

### `database is locked`

Stop the EShop API and any SQLite shell that is using the file. Then reset the database and run the test again:

```bash
cd backend
node database.js
cd ../database-rider-demo
mvn test
```

### `unable to open database file`

Check the current directory and use the correct relative path:

```bash
pwd
```

From the repository root, use `sqlite3 backend/database.sqlite`. From inside `backend`, use `sqlite3 database.sqlite`.

### `No suitable driver` or missing Maven dependency

Run Maven again with an internet connection so it can download dependencies:

```bash
cd database-rider-demo
mvn test
```

### The web login no longer works after generated-data import

Generated data may mask the original email or password. Restore one demo user after the import:

```bash
cd .
sqlite3 backend/database.sqlite "UPDATE users SET email='test@eshop.com', password='Test1234!', role='user' WHERE id=2;"
```

Use only test data in this local demo database.

### Tonic.ai reports multiple schemas detected

Do not upload `users.csv`, `products.csv`, `coupons.csv`, and `orders.csv` into one file group. Create one file group for each table. Files inside one group must have matching columns.

### Tonic.ai says that a file group must contain at least one file

Give the group a name and upload a non-empty CSV file. If `orders.csv` is empty, create an order in EShop first or leave that table out of the first Tonic.ai demonstration.

## 6. References

- Database Rider documentation: <https://database-rider.github.io/database-rider/>
- Database Rider source repository: <https://github.com/database-rider/database-rider>
- DbUnit documentation: <https://www.dbunit.org/>
- DbUnit step-by-step project guide: `DBUNIT_STEP_BY_STEP_GUIDE.md`
- Tonic.ai project guide: `TONIC_AI_TESTING_GUIDE.md`
- Maven documentation: <https://maven.apache.org/guides/index.html>
- SQLite command-line shell: <https://sqlite.org/cli.html>
- EShop project files: `setup_guide.md`, `backend/database.js`, and `database-rider-demo/`
