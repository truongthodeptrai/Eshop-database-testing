# Database Testing Report for EShop

Date: 2026-07-17

## 1. Introduction

Database testing is important because many application bugs are not only in the
user interface. They happen when data is saved, updated, deleted, or checked in
the database.

For this seminar, our System Under Test (SUT) is the EShop project in https://github.com/ttbhanh/eshop-sut

EShop is a good SUT because it is a normal shopping application. It has users,
products, categories, coupons, carts, orders, and admin features. These features
all depend on correct database behavior.

The three tools studied in this report are:

- DbUnit
- Database Rider
- Tonic.ai

The main goal is to understand how these tools help us prepare test data, check
database results, and create safer test data.

## 2. Tool Overview

| Tool | Main purpose | Used for |
| --- | --- | --- |
| DbUnit | Setup database by dataset file and check data | Database unit and integration test |
| Database Rider | Make DbUnit-style tests easier to write | Easier database test maintenance |
| Tonic.ai | Create safe test data and mask sensitive data | Test data management |

### DbUnit

DbUnit is a Java testing library. It can load a dataset into a database before a
test runs. After the test runs, we can check if the database contains the
expected data.

In our demo, DbUnit used an XML dataset.

### Database Rider

Database Rider is built on top of DbUnit. It gives a cleaner way to write
database tests. It can use YAML datasets, which are easier to read than XML.

In our demo, Database Rider used a YAML dataset.

### Tonic.ai

Tonic.ai is different from DbUnit and Database Rider. It is not mainly an
assertion tool. It is used to create generated or masked data for testing.

For EShop, this is useful because the `users` table can contain sensitive data
such as name, email, password, phone, and address.

## 3. Installation and Setup

The basic setup used for the seminar was:

1. Install Node.js for the EShop backend and frontend.
2. Install Java and Maven for DbUnit and Database Rider demos.
3. Install SQLite tools for checking the EShop database.
4. Clone or open the EShop project.
5. Run `npm install` in the Node.js parts of the project.
6. Run `node database.js` to create and seed the SQLite database.
7. Create a DbUnit demo Maven project.
8. Create a Database Rider demo Maven project.
9. Prepare a Tonic.ai workspace or Tonic.ai generated-data workflow.

The EShop backend database is:

```text
eshop-sut/backend/database.sqlite
```

The EShop backend uses SQLite. The main database seed file is:

```text
eshop-sut/backend/database.js
```

The database includes tables such as:

- `users`
- `categories`
- `products`
- `orders`
- `order_items`
- `coupons`

## 4. Hands-on Practice and Demo Steps

### 4.1 DbUnit Demo

For DbUnit, we created a small Maven test project.

Main files:

```text
dbunit-demo/pom.xml
dbunit-demo/src/test/resources/datasets/initial-dataset.xml
dbunit-demo/src/test/java/com/eshop/dbunit/EshopDbUnitTest.java
```

The XML dataset contained sample data for tables such as `users`,
`categories`, and `products`.

The test used DbUnit to:

1. Read the XML dataset.
2. Reset the database with `CLEAN_INSERT`.
3. Query the database with JDBC.
4. Assert that the expected data existed.

Command:

```bash
mvn test
```

Observed result:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

This means the DbUnit demo passed.

### 4.2 Database Rider Demo

For Database Rider, we created another small Maven test project.

Main files:

```text
database-rider-demo/pom.xml
database-rider-demo/src/test/resources/datasets/eshop-users.yml
database-rider-demo/src/test/java/com/eshop/databaserider/EshopDatabaseRiderTest.java
```

The YAML dataset contained sample `users`, `categories`, and `products`.

The test used Database Rider to:

1. Read the YAML dataset.
2. Load the dataset into SQLite.
3. Query the `users` table.
4. Assert that the table had the expected number of rows.

Command:

```bash
mvn test
```

Observed result:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

This means the Database Rider demo passed.

### 4.3 Tonic.ai Demo

The planned Tonic.ai Structural workflow was:

1. Reset the EShop SQLite database.
2. Export tables such as `users`, `products`, `coupons`, and `orders` to CSV.
3. Upload CSV files to Tonic.ai.
4. Create file groups.
5. Configure generators for sensitive columns.
6. Run data generation.
7. Download generated CSV files.
8. Compare the original data with the generated data.

Important sensitive columns in the `users` table:

| Column | Why it is sensitive |
| --- | --- |
| `name` | Real user name |
| `email` | Personal identifier |
| `password` | Login secret |
| `shipping_address` | Personal address |
| `phone` | Personal phone number |

Columns such as `id`, `role`, and foreign keys should usually be kept stable,
because the application needs them to preserve relationships between tables.

In the seminar demo, Tonic.ai was used for the `users` data. The main check was
to compare the original CSV with the generated CSV and confirm that sensitive
values were changed.

## 5. EShop Scenario Tested

The seminar demo focused on simple database testing scenarios that match the
three tools.

### Scenario A: Seed and Check Users

For DbUnit and Database Rider, the scenario was:

```text
Load known test data into the EShop SQLite database and check the users table.
```

This scenario is useful because login, checkout, and profile features all need
valid users. If the `users` table is wrong, many EShop features cannot work.

Expected result:

1. The database is reset to a known state.
2. The dataset creates two users: one admin user and one normal test user.
3. The test query confirms that the `users` table has the expected data.

DbUnit did this with an XML dataset:

```text
src/test/resources/datasets/initial-dataset.xml
```

Database Rider did the same idea with a YAML dataset:

```text
src/test/resources/datasets/eshop-users.yml
```

### Scenario B: Checkout and Order Check

The EShop application demo scenario was:

```text
Login as a test user, add a product to cart, checkout, then check the orders table.
```

Expected result after checkout:

1. The `orders` table has a new order.
2. The `user_id` belongs to the logged-in test user.
3. The `total_amount` matches the cart total.
4. The default order `status` is `pending`.

The database can be checked with SQL such as:

```sql
SELECT COUNT(*) FROM orders;
SELECT user_id, total_amount, status FROM orders;
```

### Scenario C: Mask Sensitive User Data

For Tonic.ai, the scenario was:

```text
Export users from SQLite to CSV, upload the CSV to Tonic.ai, mask sensitive columns, and download generated CSV.
```

Expected result:

1. `name`, `email`, `password`, `shipping_address`, and `phone` are changed.
2. The generated values still look like realistic test data.
3. Important relationship columns such as `id` are kept stable.
4. The generated data can be used as safer test data for a demo or staging
   environment.

## 6. Comparison

| Criteria | DbUnit | Database Rider | Tonic.ai |
| --- | --- | --- | --- |
| Tool type | Testing library | Testing library wrapper | Test data platform |
| Common input | XML dataset | YAML dataset | CSV or database source |
| Main output | Test result | Test result | Generated data |
| Ease of use | Medium | Easier than DbUnit | Easy after learning the UI |
| Can assert database results | Yes | Yes | No, not by itself |
| Can mask sensitive data | No | No | Yes |
| Best use in EShop | Reset and check database state | Cleaner database tests | Safer generated test data |

DbUnit and Database Rider are used to check if the database is correct.
Tonic.ai is used to create safer test data. Because of this, the tools support
each other, but they do not replace each other.

## 7. Strengths and Limitations

### DbUnit

Strengths:

- It gives strong control over database state.
- It is good for learning the basic idea of database testing.
- It can reset data before tests.

Limitations:

- XML datasets can be long.
- The setup code is more low-level.
- Students must understand Java, JDBC, and database tables.

### Database Rider

Strengths:

- YAML datasets are easier to read.
- It reduces some setup code compared with plain DbUnit.
- It is good for maintainable database tests.

Limitations:

- It still requires Java and Maven.
- It still requires knowledge of DbUnit concepts.
- It is not a replacement for understanding the database schema.

### Tonic.ai

Strengths:

- It can mask sensitive data.
- It can generate test data that looks realistic.
- It is useful when real production data should not be used directly.

Limitations:

- It is not a test assertion tool.
- It needs correct generator configuration.
- If IDs or foreign keys are changed incorrectly, relationships can break.
- If emails or passwords are masked, default demo login accounts may stop
  working.

## 8. Problems Encountered

During the work, these problems were recorded:

| Problem | Simple explanation |
| --- | --- |
| Missing `sqlite3` module | The Node.js dependency was not installed yet |
| Maven `pom.xml` parse error | Extra invalid text was accidentally copied into `pom.xml` |
| DbUnit order test failed | The expected order did not exist yet |
| Tonic.ai multiple schemas issue | Different CSV schemas were uploaded into one file group |
| `orders.csv` was empty | No orders existed after reset seed data |
| Masked email/password affected login | Generated credentials may not match the demo login flow |
| Database Rider import/package issue | Some Java import names had to match the Database Rider version |

## 9. Lessons Learned

Database testing needs stable data. If the database changes randomly, tests
become hard to trust.

DbUnit and Database Rider are useful when we want to prepare a known database
state before testing. They are also useful when we want to check the database
after an action.

Tonic.ai is useful when we need test data but should not use real sensitive
data. It helps protect privacy by replacing values such as names, emails,
addresses, and phone numbers.

Foreign key relationships are important. For example, if `orders.user_id`
points to `users.id`, we should be careful when generating or changing IDs.

Database tests can check things that UI screenshots alone cannot prove. For
example, after checkout, the UI may show success, but the database test can
confirm that a row was really added to the `orders` table.

## 10. Conclusion

DbUnit is useful for understanding the foundation of database testing. It shows
how to load a dataset and check database results.

Database Rider is easier to maintain because YAML datasets are cleaner and the
testing workflow is shorter.

Tonic.ai is useful for safe test data. It helps generate or mask data so testing
does not need to expose sensitive information.

For EShop, the three tools work best together:

- DbUnit and Database Rider help verify database behavior.
- Tonic.ai helps prepare safer test data.
- The EShop checkout scenario shows why checking the database after a user
  action is useful.

## 11. Evidence and Appendix

Supporting documents:

- User Guide
- DbUnit Step-by-Step Guide
- Database Rider Step-by-Step Guide
- Tonic.ai Testing Guide
- AI Audit Report

Seminar screenshot evidence:

### DbUnit Evidence

![DbUnit evidence 1](Duc/evidence/DbUnit/DbUnit1.png)

![DbUnit evidence 2](Duc/evidence/DbUnit/DbUnit2.png)

![DbUnit evidence 3](Duc/evidence/DbUnit/DbUnit3.png)

![DbUnit evidence 4](Duc/evidence/DbUnit/DbUnit4.png)

### Database Rider Evidence

![Database Rider evidence 1](Duc/evidence/DatabaseRider/DatabaseRider1.png)

![Database Rider evidence 2](Duc/evidence/DatabaseRider/DatabaseRider2.png)

### Tonic.ai Evidence

![Tonic.ai evidence 1](<Duc/evidence/Tonic AI/Tonic1.png>)

![Tonic.ai evidence 2](<Duc/evidence/Tonic AI/Tonic2.png>)

![Tonic.ai evidence 3](<Duc/evidence/Tonic AI/Tonic3.png>)

![Tonic.ai evidence 4](<Duc/evidence/Tonic AI/Tonic4.png>)

![Tonic.ai evidence 5](<Duc/evidence/Tonic AI/Tonic5.png>)

![Tonic.ai evidence 6](<Duc/evidence/Tonic AI/Tonic6.png>)

![Tonic.ai evidence 7](<Duc/evidence/Tonic AI/Tonic7.png>)

![Tonic.ai evidence 8](<Duc/evidence/Tonic AI/Tonic8.png>)

![Tonic.ai evidence 9](<Duc/evidence/Tonic AI/Tonic9.png>)

Commands used for evidence:

```bash
cd dbunit-demo
mvn test
```

```bash
cd database-rider-demo
mvn test
```

```bash
cd eshop-sut/backend
node database.js
```

## 12. References

- DbUnit: https://www.dbunit.org/
- Database Rider: https://database-rider.github.io/database-rider/
- Tonic.ai: https://www.tonic.ai/
