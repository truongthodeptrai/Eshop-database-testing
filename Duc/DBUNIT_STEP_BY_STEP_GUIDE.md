# Huong dan cai va dung DbUnit voi EShop SUT

Muc tieu: dung DbUnit de reset du lieu test va kiem tra database SQLite cua EShop sau khi chay test.

Database cua EShop nam tai:

```bash
/Users/jiduckiess/Documents/SeminarTesting/backend/database.sqlite
```

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

## 3. Tao project DbUnit demo

Tao thu muc rieng cho DbUnit:

```bash
cd /Users/jiduckiess/Documents/SeminarTesting
mkdir dbunit-demo
cd dbunit-demo
mkdir -p src/test/java/com/eshop/dbunit
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

## 5. Tao dataset DbUnit

Tao file:

```bash
touch src/test/resources/datasets/initial-dataset.xml
```

Noi dung:

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

Dataset nay dai dien cho trang thai database truoc khi test.

## 6. Viet test DbUnit dau tien

Tao file:

```bash
touch src/test/java/com/eshop/dbunit/EshopDbUnitTest.java
```

Noi dung:

```java
package com.eshop.dbunit;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EshopDbUnitTest {

    private static final String DB_PATH =
            "/Users/jiduckiess/Documents/SeminarTesting/backend/database.sqlite";

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

Test nay lam 3 viec:

1. Doc dataset XML.
2. Reset database bang `CLEAN_INSERT`.
3. Kiem tra bang `users` co dung 2 dong.

## 7. Chay test

Trong thu muc `dbunit-demo`, chay:

```bash
mvn test
```

Neu thanh cong, Maven se hien ket qua gan nhu:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 8. Chay scenario EShop roi kiem tra database

Sau khi da co test DbUnit co ban, lam tiep demo cho seminar:

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
- Them san pham vao gio hang
- Checkout

5. Viet them DbUnit/JDBC assertion de kiem tra:

```sql
SELECT COUNT(*) FROM orders;
SELECT user_id, total_amount, status FROM orders;
```

Ky vong sau checkout:

- Bang `orders` co them don hang.
- `user_id` la user dang nhap.
- `total_amount` khop tong tien.
- `status` mac dinh la `pending`.

## 9. Bang chung can chup/ghi vao team log

Can luu lai:

- Screenshot `java -version` va `mvn -version`.
- Screenshot file dataset XML.
- Screenshot ket qua `mvn test` thanh cong.
- Command da chay:
  - `node database.js`
  - `node server.js`
  - `npm run dev`
  - `mvn test`
- Screenshot UI EShop khi checkout.
- Screenshot hoac log ket qua query bang `orders`.
- Loi gap phai va cach sua, neu co.

## 10. Noi dung demo ngan gon khi thuyet trinh

Co the noi:

> Nhom em dung DbUnit de quan ly trang thai database truoc va sau test. Truoc moi test, DbUnit nap dataset XML vao SQLite database cua EShop bang CLEAN_INSERT. Sau khi chay scenario checkout, nhom em kiem tra bang orders de xac nhan don hang duoc tao dung voi user, tong tien va trang thai mong doi.

