# Activity Worksheet: Database Testing với DbUnit và Database Rider

## Thông tin hoạt động

- Thời gian: 20 phút
- Hình thức: Làm theo nhóm
- Chủ đề: Database testing bằng DbUnit và Database Rider
- Project thực hành: EShop với SQLite
- Kết quả cần nộp: Câu trả lời và ảnh chụp màn hình trong Mục 7

## 1. Mục tiêu

Sau hoạt động này, nhóm có thể:

1. Nhận biết test project và dataset đã được chuẩn bị sẵn.
2. Chạy một test DbUnit bằng XML dataset.
3. Chạy một test Database Rider bằng YAML dataset.
4. Phân biệt cách DbUnit và Database Rider nạp dữ liệu và assertion database.

## 2. Những gì đã được chuẩn bị sẵn

Trong package/repository được người trình bày chia sẻ, các file sau đã có sẵn:

```text
backend/
dbunit-demo/
database-rider-demo/
```

DbUnit đã có sẵn:

```text
dbunit-demo/pom.xml
dbunit-demo/src/test/java/com/eshop/dbunit/EshopDbUnitTest.java
dbunit-demo/src/test/resources/datasets/initial-dataset.xml
```

Database Rider đã có sẵn:

```text
database-rider-demo/pom.xml
database-rider-demo/src/test/java/com/eshop/databaserider/EshopDatabaseRiderTest.java
database-rider-demo/src/test/resources/datasets/eshop-users.yml
```

> Không tạo file mới, không viết Java test, không sửa pom.xml, không sửa dataset và không cần chạy frontend trong hoạt động này.

## 3. Chuẩn bị máy và tải project (2 phút)

Tải bản project đã chuẩn bị từ Moodle hoặc link repository do người trình bày cung cấp. Sau khi giải nén, mở Terminal tại thư mục gốc của project:

```bash
cd /duong-dan-toi/SeminarTesting
```

Thay /duong-dan-toi/SeminarTesting bằng đường dẫn thật trên máy của nhóm.

Kiểm tra các file đã có sẵn:

```bash
test -f dbunit-demo/pom.xml && echo "DbUnit files: OK"
test -f database-rider-demo/pom.xml && echo "Database Rider files: OK"
test -f dbunit-demo/src/test/resources/datasets/initial-dataset.xml && echo "DbUnit dataset: OK"
test -f database-rider-demo/src/test/resources/datasets/eshop-users.yml && echo "Rider dataset: OK"
```

Kiểm tra công cụ:

```bash
java -version
mvn -version
node -v
npm -v
```

Nếu backend chưa có dependency, cài một lần:

```bash
cd backend
npm install
cd ..
```

## 4. Reset database trước khi test (2 phút)

Từ thư mục gốc project:

```bash
cd backend
node database.js
cd ..
```

Kết quả mong đợi:

```text
Database initialized and seeded (Phase 2).
Connected to database
```

Lệnh này đưa SQLite database về trạng thái seed ban đầu. Không cần chạy node server.js và không cần mở frontend để chạy hai database test.

## 5. Hoạt động A: chạy DbUnit (6 phút)

### 5.1 Quan sát file đã chuẩn bị

Mở hai file sau bằng VS Code hoặc trình soạn thảo:

```text
dbunit-demo/src/test/resources/datasets/initial-dataset.xml
```

Hãy chú ý:

- initial-dataset.xml là dữ liệu setup cho DbUnit.
- Dataset được dùng để đưa database về trạng thái xác định trước khi test.

### 5.2 Chạy test

Từ thư mục gốc project:

```bash
cd dbunit-demo
mvn -Dtest=EshopDbUnitTest test
cd ..
```

Kết quả mong đợi:

```text
Tests run: 1, Failures: 0, Errors: 0
BUILD SUCCESS
```

Chụp ảnh màn hình kết quả Maven có BUILD SUCCESS.

## 6. Hoạt động B: chạy Database Rider (6 phút)

### 6.1 Quan sát file đã chuẩn bị

Mở hai file sau:

```text
database-rider-demo/src/test/resources/datasets/eshop-users.yml
```

Hãy so sánh với file XML của DbUnit:

- DbUnit dùng XML.
- Database Rider dùng YAML.
- Cả hai đều dùng dataset để chuẩn bị database trước khi test.

### 6.2 Chạy test

Từ thư mục gốc project:

```bash
cd database-rider-demo
mvn -Dtest=EshopDatabaseRiderTest test
cd ..
```

Kết quả mong đợi:

```text
Tests run: 1, Failures: 0, Errors: 0
BUILD SUCCESS
```

Chụp ảnh màn hình kết quả Maven có BUILD SUCCESS.

## 7. Câu hỏi nộp lại (2 phút)

Ghi câu trả lời vào file text hoặc biểu mẫu do người trình bày cung cấp:

1. DbUnit dùng định dạng dataset nào?
2. Database Rider dùng định dạng dataset nào trong hoạt động này?
3. Hai tool dùng dataset để làm gì trước khi test?
4. BUILD SUCCESS cho biết điều gì?
5. Nêu một điểm khác nhau giữa XML dataset và YAML dataset.
6. Vì sao cần reset database trước khi chạy test?

Bằng chứng cần nộp:

- Một screenshot DbUnit test pass.
- Một screenshot Database Rider test pass.
- Một screenshot XML hoặc YAML dataset.
- Câu trả lời cho 6 câu hỏi trên.

## 8. Xử lý lỗi nhanh

| Lỗi | Cách xử lý |
| --- | --- |
| mvn: command not found | Cài Maven hoặc kiểm tra lại mvn -version. |
| Unable to locate a Java Runtime | Cài Java 17 hoặc mới hơn rồi kiểm tra java -version. |
| Cannot find module sqlite3 | Vào backend, chạy npm install, sau đó chạy lại node database.js. |
| database is locked | Đóng các tiến trình đang dùng SQLite, chạy lại node database.js, rồi chạy Maven test. |
| Test fail vì database khác trạng thái | Từ thư mục gốc chạy lại cd backend, node database.js, cd .., rồi chạy test. |
| Không tìm thấy dataset XML hoặc YAML | Nhóm đang dùng sai package; tải lại bản project đã chuẩn bị. |
| Maven tải dependency quá lâu | Kiểm tra Internet và chờ Maven hoàn tất lần tải đầu tiên. |

> Nếu vẫn không chạy được sau các bước trên, chụp toàn bộ lỗi Terminal và nộp kèm worksheet. Không tự sửa code trong hoạt động 20 phút.

