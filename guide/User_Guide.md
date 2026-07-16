# EShop Database Testing User Guide

This guide shows how to use three tools with the EShop System Under Test:

- DbUnit for XML-based database tests.
- Database Rider for YAML-based database tests.
- Tonic.ai for privacy-safe generated test data.

The guide is written for a new user starting from a fresh clone. Unless stated otherwise, run commands from the repository root, meaning the folder that contains this `User_Guide.md` file.

## 1. Introduction

EShop is a Node.js e-commerce application that stores data in SQLite:

```text
backend/database.sqlite
```

The three tools have different jobs:

| Tool | Main purpose | Input | Output |
| --- | --- | --- | --- |
| DbUnit | Load controlled test data and verify database state | XML dataset | JUnit result |
| Database Rider | Make DbUnit-style tests easier to write and maintain | YAML dataset | JUnit result |
| Tonic.ai | Generate masked or synthetic test data | CSV export | Generated CSV |

DbUnit and Database Rider do not test the React UI directly. They test the database used by EShop. Tonic.ai does not assert correctness; it creates safer test data that can be reviewed or imported into a test database.

Default EShop accounts after running the seed script:

| Account | Email | Password |
| --- | --- | --- |
| Admin | `admin@eshop.com` | `Admin123!` |
| Test user | `test@eshop.com` | `Test1234!` |

## 2. Install

### 2.1 Install required tools

Check whether these commands work:

```bash
node -v
npm -v
java -version
mvn -version
sqlite3 --version
git --version
```

You need Node.js, npm, Java 17 or later, Maven, SQLite, and Git.

On macOS with Homebrew:

```bash
brew install node openjdk@17 maven sqlite git
```

If Homebrew prints an instruction for adding Java to `PATH`, run that command, open a new Terminal window, and check `java -version` again.

On Debian or Ubuntu:

```bash
sudo apt update
sudo apt install -y nodejs npm openjdk-17-jdk maven sqlite3 git
```

On Windows PowerShell:

```powershell
winget install --id OpenJS.NodeJS.LTS -e
winget install --id EclipseAdoptium.Temurin.17.JDK -e
winget install --id Apache.Maven -e
winget install --id SQLite.SQLite -e
winget install --id Git.Git -e
```

Close and reopen PowerShell after installation.

### 2.2 Clone the project

If the project is not on your machine yet:

```bash
git clone https://github.com/ttbhanh/eshop-sut.git SeminarTesting
cd SeminarTesting
```

If you already have the project folder, open a terminal in that folder.

### 2.3 Install EShop backend dependencies

```bash
cd backend
npm install
cd ..
```

### 2.4 Create the SQLite database

Run the database seed script before running any Java database test:

```bash
cd backend
node database.js
cd ..
```

Expected output:

```text
Database initialized and seeded (Phase 2).
Connected to database
```

Verify that the database exists:

```bash
sqlite3 backend/database.sqlite "SELECT id, name, email, role FROM users;"
```

You should see the admin user and the test user.

## 3. First Test

This section creates two small Java test projects inside the EShop repository. If the folders already exist, compare your files with the snippets below.

### 3.1 Create the DbUnit demo folder

From the repository root:

```bash
mkdir -p dbunit-demo/src/test/java/com/eshop/dbunit
mkdir -p dbunit-demo/src/test/resources/datasets
touch dbunit-demo/pom.xml
touch dbunit-demo/src/test/resources/datasets/initial-dataset.xml
touch dbunit-demo/src/test/java/com/eshop/dbunit/EshopDbUnitTest.java
```

Open `dbunit-demo/pom.xml` and paste:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.eshop</groupId>
    <artifactId>dbunit-demo</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.dbunit</groupId>
            <artifactId>dbunit</artifactId>
            <version>2.7.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.xerial</groupId>
            <artifactId>sqlite-jdbc</artifactId>
            <version>3.45.3.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
            </plugin>
        </plugins>
    </build>
</project>
```

Open `dbunit-demo/src/test/resources/datasets/initial-dataset.xml` and paste:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<dataset>
    <categories id="1" name="Dien thoai"/>
    <categories id="2" name="Laptop"/>
    <categories id="3" name="Phu kien"/>

    <users id="1" name="Admin User" email="admin@eshop.com" password="Admin123!" role="admin"
           login_attempts="0" locked_until="[null]" reset_token="[null]"
           shipping_address="[null]" phone="[null]"/>
    <users id="2" name="Test User" email="test@eshop.com" password="Test1234!" role="user"
           login_attempts="0" locked_until="[null]" reset_token="[null]"
           shipping_address="[null]" phone="[null]"/>

    <products id="1" name="iPhone 15 Pro Max" price="30000000"
              description="Dien thoai cao cap cua Apple"
              imageUrl="https://placehold.co/300x300/png?text=iPhone+15" category_id="1"/>
    <products id="2" name="MacBook Pro M3" price="45000000"
              description="Laptop chuyen nghiep manh me"
              imageUrl="https://placehold.co/300x300/png?text=Macbook+Pro" category_id="2"/>
</dataset>
```

Open `dbunit-demo/src/test/java/com/eshop/dbunit/EshopDbUnitTest.java` and paste:

```java
package com.eshop.dbunit;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EshopDbUnitTest {

    private static final String DB_PATH =
            Paths.get("..", "backend", "database.sqlite")
                    .toAbsolutePath()
                    .normalize()
                    .toString();

    @Test
    void shouldLoadInitialDatasetAndCheckUsers() throws Exception {
        IDataSet dataSet = new FlatXmlDataSetBuilder()
                .setColumnSensing(true)
                .build(new File("src/test/resources/datasets/initial-dataset.xml"));

        IDatabaseTester databaseTester = new JdbcDatabaseTester(
                "org.sqlite.JDBC",
                "jdbc:sqlite:" + DB_PATH
        );

        databaseTester.setDataSet(dataSet);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.onSetup();

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS total FROM users")) {

            assertEquals(2, resultSet.getInt("total"));
        }
    }
}
```

Run the DbUnit test:

```bash
cd dbunit-demo
mvn test
cd ..
```

Expected result:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 3.2 Create the Database Rider demo folder

From the repository root:

```bash
mkdir -p database-rider-demo/src/test/java/com/eshop/databaserider
mkdir -p database-rider-demo/src/test/resources/datasets
touch database-rider-demo/pom.xml
touch database-rider-demo/src/test/resources/datasets/eshop-users.yml
touch database-rider-demo/src/test/java/com/eshop/databaserider/EshopDatabaseRiderTest.java
```

Open `database-rider-demo/pom.xml` and paste:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.eshop</groupId>
    <artifactId>database-rider-demo</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.github.database-rider</groupId>
            <artifactId>rider-core</artifactId>
            <version>1.44.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.xerial</groupId>
            <artifactId>sqlite-jdbc</artifactId>
            <version>3.45.3.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.13</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
            </plugin>
        </plugins>
    </build>
</project>
```

Open `database-rider-demo/src/test/resources/datasets/eshop-users.yml` and paste:

```yaml
categories:
  - id: 1
    name: "Dien thoai"
  - id: 2
    name: "Laptop"
  - id: 3
    name: "Phu kien"

users:
  - id: 1
    name: "Admin User"
    email: "admin@eshop.com"
    password: "Admin123!"
    role: "admin"
    login_attempts: 0
    locked_until: null
    reset_token: null
    shipping_address: null
    phone: null
  - id: 2
    name: "Test User"
    email: "test@eshop.com"
    password: "Test1234!"
    role: "user"
    login_attempts: 0
    locked_until: null
    reset_token: null
    shipping_address: null
    phone: null

products:
  - id: 1
    name: "iPhone 15 Pro Max"
    price: 30000000
    description: "Dien thoai cao cap cua Apple"
    imageUrl: "https://placehold.co/300x300/png?text=iPhone+15"
    category_id: 1
```

Open `database-rider-demo/src/test/java/com/eshop/databaserider/EshopDatabaseRiderTest.java` and paste:

```java
package com.eshop.databaserider;

import com.github.database.rider.core.configuration.DataSetConfig;
import com.github.database.rider.core.configuration.DBUnitConfig;
import com.github.database.rider.core.dsl.RiderDSL;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EshopDatabaseRiderTest {

    private static final String DB_PATH =
            Paths.get("..", "backend", "database.sqlite")
                    .toAbsolutePath()
                    .normalize()
                    .toString();

    @Test
    void shouldSeedUsersWithDatabaseRiderYamlDataset() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH)) {
            RiderDSL.withConnection(connection)
                    .withDataSetConfig(new DataSetConfig("datasets/eshop-users.yml")
                            .cleanBefore(true)
                            .disableConstraints(true))
                    .withDBUnitConfig(new DBUnitConfig()
                            .addDBUnitProperty("caseSensitiveTableNames", false))
                    .createDataSet();
        }

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS total FROM users")) {

            assertEquals(2, resultSet.getInt("total"));
        }
    }
}
```

Run the Database Rider test:

```bash
cd database-rider-demo
mvn test
cd ..
```

Expected result:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 3.3 Check the database after the tests

From the repository root:

```bash
sqlite3 backend/database.sqlite "SELECT id, name, email, role FROM users;"
```

Expected rows:

```text
1|Admin User|admin@eshop.com|admin
2|Test User|test@eshop.com|user
```

## 4. Advanced Usage

### 4.1 Add a stronger assertion

The first tests only check that two users exist. To verify a specific role, add this block inside a test method after the dataset is loaded:

```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
     Statement statement = connection.createStatement();
     ResultSet resultSet = statement.executeQuery(
             "SELECT role FROM users WHERE email = 'admin@eshop.com'")) {

    assertEquals("admin", resultSet.getString("role"));
}
```

This assertion proves that the admin account is not only inserted, but also has the expected authorization role.

### 4.2 Test an order scenario

For an order scenario, first run EShop and create an order through the web UI or API. Start the backend:

```bash
cd backend
node server.js
```

In another terminal, start the web frontend:

```bash
cd frontend-web
npm install
npm run dev
```

Open the Vite URL, usually `http://localhost:5173`, log in as `test@eshop.com`, add a product to cart, and complete checkout. Then check the database:

```bash
sqlite3 backend/database.sqlite "SELECT id, user_id, total_amount, status FROM orders;"
```

Only write an automated order assertion after you know the exact expected order data.

### 4.3 Export CSV files for Tonic.ai

Create a folder for CSV source files:

```bash
mkdir -p tonic-export/source
```

Export tables from SQLite:

```bash
sqlite3 -header -csv backend/database.sqlite "SELECT * FROM users;" > tonic-export/source/users.csv
sqlite3 -header -csv backend/database.sqlite "SELECT * FROM products;" > tonic-export/source/products.csv
sqlite3 -header -csv backend/database.sqlite "SELECT * FROM coupons;" > tonic-export/source/coupons.csv
sqlite3 -header -csv backend/database.sqlite "SELECT * FROM orders;" > tonic-export/source/orders.csv
```

If `orders.csv` only contains a header row, create an order first or skip that file in the Tonic.ai demo.

### 4.4 Configure Tonic.ai

In Tonic.ai:

1. Create a workspace.
2. Choose a Files source.
3. Choose Local Filesystem.
4. Upload CSV files from `tonic-export/source`.
5. Create one file group per table.

Use this mapping:

| CSV file | File group name |
| --- | --- |
| `users.csv` | `users` |
| `products.csv` | `products` |
| `coupons.csv` | `coupons` |
| `orders.csv` | `orders` |

Do not upload all CSV files into one file group. Tonic.ai expects files inside one group to have the same columns.

For the `users` file group, configure generators:

| Column | Suggested generator | Reason |
| --- | --- | --- |
| `name` | Name | Replace real names |
| `email` | Email | Replace real emails |
| `password` | Constant or Character Scramble | Avoid keeping real passwords |
| `shipping_address` | Address | Replace real addresses |
| `phone` | Phone | Replace real phone numbers |
| `id` | Passthrough | Preserve relationships |
| `role` | Passthrough | Preserve admin/user behavior |

Run generation, wait for the job to finish, and download the generated CSV output. Review the generated CSV before importing it into SQLite.

### 4.5 Import generated CSV carefully

For seminar purposes, showing the generated CSV is usually enough. If you import generated users into SQLite, import into a backup database first:

```bash
cp backend/database.sqlite backend/database.tonic-demo.sqlite
```

Then import only after checking that the CSV columns match the `users` table. If generated email or password values replace the default login account, `test@eshop.com` may no longer work until you restore that user.

## 5. Troubleshooting

### `mvn: command not found`

Maven is not installed or is not in `PATH`.

On macOS:

```bash
brew install maven
mvn -version
```

On Debian or Ubuntu:

```bash
sudo apt install -y maven
mvn -version
```

On Windows PowerShell:

```powershell
winget install --id Apache.Maven -e
mvn --version
```

Close and reopen the terminal after installation.

### `Unable to locate a Java Runtime`

Java is missing or not in `PATH`.

On macOS:

```bash
brew install openjdk@17
java -version
```

On Windows PowerShell:

```powershell
winget install --id EclipseAdoptium.Temurin.17.JDK -e
java -version
```

If the command still fails, add the JDK `bin` folder to `PATH` and reopen the terminal.

### Maven cannot parse `pom.xml`

The `pom.xml` must contain only XML. Remove Markdown fences, terminal output, or extra text after `</project>`.

### `no such table: users`

The SQLite database has not been initialized. Run:

```bash
cd backend
node database.js
cd ..
```

### `unable to open database file`

Check your current directory:

```bash
pwd
```

From the repository root, the database path is `backend/database.sqlite`. From inside `backend`, the path is `database.sqlite`.

### `database is locked`

Stop any running EShop backend server or SQLite shell that is using the database file, then run the test again.

### Tonic.ai reports multiple schemas detected

Create one file group per CSV table. Do not combine `users.csv`, `products.csv`, `coupons.csv`, and `orders.csv` in the same file group.

### Tonic.ai says a file group must contain at least one file

Upload a non-empty CSV file. If `orders.csv` has no order rows, skip the `orders` group or create an order in EShop before exporting.

### Web login no longer works after generated data

Generated data may replace email or password fields. Restore a demo account:

```bash
sqlite3 backend/database.sqlite "UPDATE users SET email='test@eshop.com', password='Test1234!', role='user' WHERE id=2;"
```

## 6. References

- DbUnit documentation: <https://www.dbunit.org/>
- Database Rider documentation: <https://database-rider.github.io/database-rider/>
- Database Rider source repository: <https://github.com/database-rider/database-rider>
- Maven documentation: <https://maven.apache.org/guides/index.html>
- SQLite command-line shell: <https://sqlite.org/cli.html>
