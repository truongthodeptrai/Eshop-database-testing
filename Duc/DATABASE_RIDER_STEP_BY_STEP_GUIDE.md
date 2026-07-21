# Huong dan cai va dung Database Rider voi EShop SUT

Muc tieu: dung Database Rider de test cung scenario voi file DbUnit:

1. Reset database SQLite cua EShop.
2. Nap dataset ban dau gom `categories`, `users`, va `products`.
3. Kiem tra bang `users` co dung 2 dong.
4. Chay scenario EShop tren web: dang nhap, them san pham vao gio hang, checkout.
5. Dung Database Rider de kiem tra bang `orders` sau checkout.

Database cua EShop nam tai:

```bash
/Users/jiduckiess/Documents/SeminarTesting/backend/database.sqlite
```

Database Rider duoc xay tren DbUnit. Diem khac la Database Rider giup code gon hon va dataset YAML de doc hon XML.

## 1. Cai Java va Maven

Kiem tra may da co Java/Maven chua:

```bash
java -version
mvn -version
```

Neu may bao loi `Unable to locate a Java Runtime` hoac `command not found: mvn`, can cai:

```bash
brew install openjdk@17
brew install maven
```

Sau khi cai xong, kiem tra lai:

```bash
java -version
mvn -version
```

Neu Java da cai nhung terminal van khong nhan, them Java vao PATH theo huong dan Homebrew hien ra sau khi cai `openjdk@17`.

## 2. Reset database EShop

Chay lenh nay de tao lai database va seed data mau:

```bash
cd /Users/jiduckiess/Documents/SeminarTesting/backend
node database.js
```

Lenh thanh cong se hien:

```text
Connected to database
Database initialized and seeded (Phase 2).
```

## 3. Tao project Database Rider demo

Tao thu muc rieng cho Database Rider:

```bash
cd /Users/jiduckiess/Documents/SeminarTesting
mkdir database-rider-demo
cd database-rider-demo
mkdir -p src/test/java/com/eshop/databaserider
mkdir -p src/test/resources/datasets
```

## 4. Tao file Maven `pom.xml`

Tao file:

```bash
touch pom.xml
```

Noi dung can co:

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

## 5. Tao dataset ban dau

Tao file:

```bash
touch src/test/resources/datasets/initial-dataset.yml
```

Noi dung:

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
  - id: 2
    name: "MacBook Pro M3"
    price: 45000000
    description: "Laptop chuyen nghiep manh me"
    imageUrl: "https://placehold.co/300x300/png?text=Macbook+Pro"
    category_id: 2
```

Dataset nay giong dataset XML trong file DbUnit. No dai dien cho trang thai database truoc khi test.

## 6. Viet test seed database

Tao file:

```bash
touch src/test/java/com/eshop/databaserider/EshopDatabaseRiderSeedTest.java
```

Noi dung:

```java
package com.eshop.databaserider;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.configuration.DBUnitConfig;
import com.github.database.rider.core.configuration.DataSetConfig;
import com.github.database.rider.core.dataset.DataSetExecutorImpl;
import org.dbunit.database.DatabaseConfig;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EshopDatabaseRiderSeedTest {

    private static final String DB_PATH =
            "/Users/jiduckiess/Documents/SeminarTesting/backend/database.sqlite";

    @Test
    void shouldLoadInitialDatasetAndCheckUsers() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH)) {
            ConnectionHolder holder = () -> connection;

            DBUnitConfig dbUnitConfig = new DBUnitConfig()
                    .columnSensing(true)
                    .cacheConnection(false)
                    .addDBUnitProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

            DataSetExecutorImpl executor =
                    DataSetExecutorImpl.instance("eshop-rider-seed", holder, dbUnitConfig);

            DataSetConfig dataSet = new DataSetConfig("datasets/initial-dataset.yml")
                    .strategy(SeedStrategy.CLEAN_INSERT)
                    .disableConstraints(true);

            executor.createDataSet(dataSet);
        }

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS total FROM users")) {

            assertEquals(2, resultSet.getInt("total"));
        }
    }
}
```

Test nay lam 3 viec giong test DbUnit:

1. Doc dataset YAML.
2. Reset database bang `CLEAN_INSERT`.
3. Kiem tra bang `users` co dung 2 dong.

`CLEAN_INSERT` nghia la xoa du lieu cu trong cac bang cua dataset, sau do insert du lieu moi vao.

## 7. Chay test seed

Trong thu muc `database-rider-demo`, chay:

```bash
mvn -Dtest=EshopDatabaseRiderSeedTest test
```

Neu thanh cong, Maven se hien ket qua gan nhu:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 8. Chay scenario EShop roi kiem tra database

Scenario nay giong file DbUnit: dang nhap, them san pham vao gio hang, checkout, sau do kiem tra bang `orders`.

1. Reset database:

```bash
cd /Users/jiduckiess/Documents/SeminarTesting/backend
node database.js
```

2. Chay backend:

```bash
node server.js
```

3. Mo terminal khac, chay frontend:

```bash
cd /Users/jiduckiess/Documents/SeminarTesting/frontend-web
npm run dev
```

4. Tren web, thuc hien scenario:

- Dang nhap bang `test@eshop.com` / `Test1234!`
- Them san pham `iPhone 15 Pro Max` vao gio hang
- Checkout

Ky vong sau checkout:

- Bang `orders` co them 1 don hang.
- `user_id` la `2`, vi user `test@eshop.com` co id la `2`.
- `total_amount` la `30000000`, vi san pham `iPhone 15 Pro Max` co gia `30000000`.
- `status` mac dinh la `pending`.

## 9. Tao expected dataset cho checkout

Tao file:

```bash
touch src/test/resources/datasets/expected-checkout.yml
```

Noi dung:

```yaml
orders:
  - user_id: 2
    total_amount: 30000000
    status: "pending"
```

Dataset nay chi mo ta nhung cot quan trong can kiem tra. Cot `id` thuong la gia tri database tu sinh, nen khong nen viet co dinh vao expected dataset.

## 10. Viet test kiem tra checkout bang Database Rider

Tao file:

```bash
touch src/test/java/com/eshop/databaserider/EshopCheckoutDatabaseRiderTest.java
```

Noi dung:

```java
package com.eshop.databaserider;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.CompareOperation;
import com.github.database.rider.core.configuration.DBUnitConfig;
import com.github.database.rider.core.configuration.DataSetConfig;
import com.github.database.rider.core.dataset.DataSetExecutorImpl;
import org.dbunit.database.DatabaseConfig;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

class EshopCheckoutDatabaseRiderTest {

    private static final String DB_PATH =
            "/Users/jiduckiess/Documents/SeminarTesting/backend/database.sqlite";

    @Test
    void shouldCreateOrderAfterCheckoutScenario() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH)) {
            ConnectionHolder holder = () -> connection;

            DBUnitConfig dbUnitConfig = new DBUnitConfig()
                    .columnSensing(true)
                    .cacheConnection(false)
                    .addDBUnitProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

            DataSetExecutorImpl executor =
                    DataSetExecutorImpl.instance("eshop-rider-checkout", holder, dbUnitConfig);

            DataSetConfig expected = new DataSetConfig("datasets/expected-checkout.yml");

            executor.compareCurrentDataSetWith(
                    expected,
                    new String[] {"id"},
                    null,
                    new String[] {"user_id"},
                    CompareOperation.EQUALS);
        }
    }
}
```

Test nay khong reset database. Ly do: tester da thao tac checkout tren web truoc do. Test chi ket noi vao database that va so sanh bang `orders` voi expected dataset.

Chay test checkout:

```bash
mvn -Dtest=EshopCheckoutDatabaseRiderTest test
```

Neu tester checkout san pham khac, can doi `total_amount` trong `expected-checkout.yml` theo tong tien that tren gio hang.

## 11. Bang chung can chup/ghi vao team log

Can luu lai:

- Screenshot `java -version` va `mvn -version`.
- Screenshot file `initial-dataset.yml`.
- Screenshot file `expected-checkout.yml`.
- Screenshot ket qua `mvn -Dtest=EshopDatabaseRiderSeedTest test` thanh cong.
- Screenshot ket qua `mvn -Dtest=EshopCheckoutDatabaseRiderTest test` thanh cong.
- Command da chay:
  - `node database.js`
  - `node server.js`
  - `npm run dev`
- Screenshot UI EShop khi checkout.
- Loi gap phai va cach sua, neu co.

## 12. Noi dung demo ngan gon khi thuyet trinh

Co the noi:

> Nhom em dung Database Rider de quan ly trang thai database truoc va sau test. Truoc test, Database Rider nap dataset YAML vao SQLite database cua EShop bang CLEAN_INSERT. Sau khi chay scenario checkout tren web, Database Rider so sanh bang orders trong database that voi expected dataset. Scenario nay giong DbUnit, nhung Database Rider dung YAML va API gon hon.
