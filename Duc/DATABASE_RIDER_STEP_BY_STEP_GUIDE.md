# Huong dan cai va dung Database Rider voi EShop SUT

Muc tieu: dung Database Rider de nap du lieu test bang YAML va kiem tra SQLite database cua EShop.

Database cua EShop:

```bash
/Users/jiduckiess/Documents/SeminarTesting/backend/database.sqlite
```

Tai lieu chinh thuc da tham khao:

- Database Rider GitHub: https://github.com/database-rider/database-rider
- Database Rider documentation: https://database-rider.github.io/database-rider/
- Database Rider JUnit 5 section: https://github.com/database-rider/database-rider#7-junit-5

## 1. Database Rider la gi?

Database Rider la tool giup viet database test de hon tren Java/JUnit. No duoc xay tren DBUnit, nhung thay vi phai viet nhieu setup code nhu DbUnit thuan, Database Rider cho phep dung dataset YAML/XML/JSON/CSV va API/annotation de seed database.

Voi EShop:

```text
EShop Node.js app       -> backend/database.sqlite
Database Rider Java test -> backend/database.sqlite
```

Database Rider khong test UI React va khong test code Node.js truc tiep. No test **database SQLite ma EShop dang dung**.

## 2. Kiem tra Java va Maven

```bash
java -version
mvn -version
```

Neu chua co:

```bash
brew install openjdk@17
brew install maven
```

## 3. Reset database EShop

```bash
cd /Users/jiduckiess/Documents/SeminarTesting/backend
node database.js
```

Ket qua mong doi:

```text
Database initialized and seeded (Phase 2).
Connected to database
```

## 4. Tao project demo Database Rider

```bash
cd /Users/jiduckiess/Documents/SeminarTesting
mkdir -p database-rider-demo/src/test/java/com/eshop/databaserider
mkdir -p database-rider-demo/src/test/resources/datasets
cd database-rider-demo
```

## 5. Tao `pom.xml`

Tao file:

```bash
touch pom.xml
```

Noi dung:

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

## 6. Tao dataset YAML

Tao file:

```bash
touch src/test/resources/datasets/eshop-users.yml
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
```

Dataset nay dai dien cho trang thai database truoc test.

## 7. Viet test Database Rider dau tien

Tao file:

```bash
touch src/test/java/com/eshop/databaserider/EshopDatabaseRiderTest.java
```

Noi dung:

```java
package com.eshop.databaserider;

import com.github.database.rider.core.configuration.DataSetConfig;
import com.github.database.rider.core.configuration.DBUnitConfig;
import com.github.database.rider.core.dsl.RiderDSL;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EshopDatabaseRiderTest {

    private static final String DB_PATH =
            "/Users/jiduckiess/Documents/SeminarTesting/backend/database.sqlite";

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

Test nay lam 3 viec:

1. Database Rider doc dataset YAML.
2. Database Rider nap dataset vao SQLite database cua EShop.
3. JDBC kiem tra bang `users` co dung 2 dong.

## 8. Chay test

```bash
cd /Users/jiduckiess/Documents/SeminarTesting/database-rider-demo
mvn test
```

Ket qua mong doi:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 9. So sanh Database Rider voi DbUnit

| Noi dung | DbUnit | Database Rider |
| --- | --- | --- |
| Dataset | Thuong dung XML | Thuong dung YAML de doc hon |
| Setup | Nhieu code hon | Gon hon, co RiderDSL/annotation |
| Nen demo | Tot de noi ve DBUnit goc | Tot de demo database test gon hon |
| Phu hop voi EShop | Co | Co |

## 10. Cach demo voi EShop

Demo don gian:

1. Chay `node database.js` de reset database EShop.
2. Chay `mvn test` trong `database-rider-demo`.
3. Mo SQLite hoac dung query de chung minh bang `users` duoc nap dung.

Kiem tra bang terminal:

```bash
cd /Users/jiduckiess/Documents/SeminarTesting
sqlite3 backend/database.sqlite "SELECT id, name, email, role FROM users;"
```

## 11. Bang chung can chup

- Screenshot `mvn test` thanh cong.
- Screenshot file `eshop-users.yml`.
- Screenshot test Java `EshopDatabaseRiderTest.java`.
- Screenshot query `SELECT id, name, email, role FROM users;`.
- Neu co loi, chup loi va ghi cach sua.

## 12. Cau noi ngan gon khi thuyet trinh

Co the noi:

> Nhom em dung Database Rider de seed va kiem tra database test bang dataset YAML. Database Rider duoc xay tren DBUnit nhung cach viet gon hon. Trong demo, Database Rider doc file `eshop-users.yml`, nap du lieu vao SQLite database cua EShop, sau do test kiem tra bang `users` co dung du lieu mong doi.
