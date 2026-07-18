# Báo cáo kiểm thử cơ sở dữ liệu cho EShop

Ngày: 2026-07-19

## 1. Giới thiệu

Kiểm thử cơ sở dữ liệu rất quan trọng vì nhiều lỗi ứng dụng không chỉ xuất hiện
trên giao diện. Lỗi có thể xảy ra khi dữ liệu được lưu, cập nhật, xóa hoặc kiểm
tra trong cơ sở dữ liệu.

Hệ thống được kiểm thử trong seminar là dự án EShop tại
https://github.com/ttbhanh/eshop-sut

EShop phù hợp để minh họa vì đây là một ứng dụng thương mại điện tử điển hình,
bao gồm người dùng, sản phẩm, danh mục, mã giảm giá, giỏ hàng, đơn hàng và chức
năng quản trị. Tất cả chức năng này đều phụ thuộc vào tính chính xác của dữ liệu.

Ba công cụ được nghiên cứu trong báo cáo:

- DbUnit
- Database Rider
- Tonic.ai

Mục tiêu chính là tìm hiểu cách các công cụ này giúp chuẩn bị dữ liệu kiểm thử,
kiểm tra kết quả trong cơ sở dữ liệu và tạo dữ liệu kiểm thử an toàn hơn.

## 2. Tổng quan công cụ

| Công cụ | Mục đích chính | Trường hợp sử dụng |
| --- | --- | --- |
| DbUnit | Thiết lập trạng thái cơ sở dữ liệu bằng dataset và kiểm tra dữ liệu | Kiểm thử đơn vị và kiểm thử tích hợp cơ sở dữ liệu |
| Database Rider | Giúp viết và duy trì kiểm thử kiểu DbUnit dễ hơn | Kiểm thử cơ sở dữ liệu bằng annotation và YAML |
| Tonic.ai | Tạo hoặc che dữ liệu nhạy cảm | Quản lý dữ liệu kiểm thử an toàn |

### DbUnit

DbUnit là thư viện kiểm thử dành cho Java. Công cụ có thể nạp dataset vào cơ sở
dữ liệu trước khi test chạy. Sau khi thực hiện hành động, test có thể kiểm tra cơ
sở dữ liệu có chứa dữ liệu mong đợi hay không.

Trong demo, DbUnit sử dụng dataset XML.

### Database Rider

Database Rider được xây dựng trên DbUnit. Công cụ cung cấp annotation và cách
cấu hình gọn hơn để viết database test. Dataset YAML thường dễ đọc và chỉnh sửa
hơn XML.

Trong demo, Database Rider sử dụng dataset YAML.

### Tonic.ai

Tonic.ai khác DbUnit và Database Rider. Đây không phải công cụ assertion chính.
Tonic được dùng để tạo hoặc che dữ liệu phục vụ kiểm thử.

Đối với EShop, điều này hữu ích vì bảng `users` có thể chứa tên, email, mật khẩu,
số điện thoại và địa chỉ giao hàng. Đây đều là dữ liệu không nên đưa trực tiếp
vào môi trường demo hoặc test nếu lấy từ người dùng thật.

## 3. Kiểm thử cơ sở dữ liệu quan hệ và NoSQL

Kiểm thử cơ sở dữ liệu quan hệ và NoSQL có cùng mục tiêu tổng quát: chuẩn bị một
trạng thái có kiểm soát, thực hiện hành động, kiểm tra dữ liệu đã lưu và dọn dẹp
môi trường test. Tuy nhiên, rủi ro và nội dung assertion khác nhau do hai mô hình
tổ chức và phân phối dữ liệu theo cách khác nhau.

### 3.1 Kiểm thử cơ sở dữ liệu quan hệ

Kiểm thử cơ sở dữ liệu quan hệ thường bao gồm:

- Kiểm tra định nghĩa bảng, cột và kiểu dữ liệu.
- Kiểm tra khóa chính, khóa ngoại, `UNIQUE`, `NOT NULL` và `CHECK`.
- Kiểm tra thao tác thêm, sửa, xóa và truy vấn.
- Kiểm tra phép nối và quan hệ giữa các bảng.
- Kiểm tra transaction, commit và rollback theo ACID.
- Kiểm tra index và kế hoạch thực thi SQL.
- Kiểm tra migration và khả năng tương thích ngược.
- Kiểm tra stored procedure, trigger và view nếu hệ thống sử dụng.

Trong mô hình quan hệ, database engine thường có thể trực tiếp từ chối dữ liệu
không hợp lệ. Ví dụ, khóa ngoại có thể ngăn `orders.user_id` tham chiếu đến một
người dùng không tồn tại.

### 3.2 Kiểm thử cơ sở dữ liệu NoSQL

NoSQL là một nhóm rộng gồm document database, key-value store, wide-column và
graph database. Với document database như MongoDB, công việc kiểm thử thường
bao gồm:

- Kiểm tra cấu trúc document, field bắt buộc, kiểu dữ liệu, object lồng nhau và mảng.
- Kiểm tra schema validation nếu database hỗ trợ.
- Kiểm tra embedded document và reference giữa các collection.
- Kiểm tra tính toàn vẹn ở tầng ứng dụng khi không có foreign key.
- Kiểm tra document query và aggregation pipeline.
- Kiểm tra phạm vi atomic update và transaction.
- Kiểm tra strong consistency hoặc eventual consistency sau khi ghi.
- Kiểm tra partition key, shard key, hot partition và phân phối dữ liệu.
- Kiểm tra secondary index, unique index và TTL index.
- Kiểm tra nhiều phiên bản document cùng tồn tại khi schema thay đổi.
- Kiểm tra retry, idempotency, replication và failover.

Trong hệ thống eventual consistency, assertion chạy ngay sau thao tác ghi có thể
không ổn định. Test nên dùng cơ chế retry có giới hạn và timeout rõ ràng thay vì
`sleep` cố định.

### 3.3 Khác biệt chính

| Nội dung kiểm thử | Cơ sở dữ liệu quan hệ | Cơ sở dữ liệu NoSQL |
| --- | --- | --- |
| Cấu trúc dữ liệu | Bảng, dòng và cột | Collection, document, key hoặc graph |
| Schema | Thường khai báo và được database bắt buộc | Linh hoạt, tùy chọn hoặc do ứng dụng kiểm soát |
| Quan hệ | Foreign key và join | Embedded data hoặc reference do ứng dụng quản lý |
| Transaction | ACID transaction phổ biến | Phụ thuộc database và phạm vi thao tác |
| Tính nhất quán | Thường strong consistency trong một database | Strong, eventual hoặc có thể cấu hình |
| Phân phối | Thường kiểm thử như một dịch vụ database | Partition, shard và replication là rủi ro chính |
| Thay đổi schema | Migration theo thứ tự | Nhiều phiên bản document có thể cùng tồn tại |
| Assertion điển hình | Dòng, constraint, join và transaction | Document, consistency, partition và aggregation |

### 3.4 Khả năng áp dụng của ba công cụ

| Công cụ | Cơ sở dữ liệu quan hệ | Cơ sở dữ liệu NoSQL |
| --- | --- | --- |
| DbUnit | Phù hợp trực tiếp thông qua JDBC và table dataset | Không hỗ trợ native cho document hoặc key-value |
| Database Rider | Phù hợp vì sử dụng DbUnit và JDBC | Không hỗ trợ NoSQL native |
| Tonic Structural | Hỗ trợ nhiều relational connector | Hỗ trợ có chọn lọc, gồm MongoDB và Amazon DynamoDB |

DbUnit và Database Rider nên được xem là công cụ kiểm thử cơ sở dữ liệu quan hệ.
Một dự án NoSQL thường cần native driver của database, test container hoặc test
instance riêng, cùng assertion hiểu document, consistency và partition.

Tonic Structural có thể chuẩn bị dữ liệu an toàn cho một số NoSQL connector,
nhưng vẫn cần framework test khác để chứng minh ứng dụng hoạt động đúng.

## 4. Cài đặt và thiết lập

Thiết lập cơ bản dùng cho seminar:

1. Cài Node.js cho backend và frontend EShop.
2. Cài Java và Maven cho demo DbUnit và Database Rider.
3. Cài công cụ SQLite để xem và truy vấn database EShop.
4. Clone hoặc mở dự án EShop.
5. Chạy `npm install` trong các phần Node.js của dự án.
6. Chạy `node database.js` để tạo và seed SQLite database.
7. Tạo Maven project dùng cho DbUnit.
8. Tạo Maven project dùng cho Database Rider.
9. Chuẩn bị workspace hoặc quy trình tạo dữ liệu trên Tonic.ai.

Database backend của EShop:

```text
eshop-sut/backend/database.sqlite
```

File khởi tạo và seed database:

```text
eshop-sut/backend/database.js
```

Các bảng chính bao gồm:

- `users`
- `categories`
- `products`
- `orders`
- `order_items`
- `coupons`

## 5. Thực hành và các bước demo

### 5.1 Demo DbUnit

Nhóm tạo một Maven test project nhỏ cho DbUnit.

Các file chính:

```text
dbunit-demo/pom.xml
dbunit-demo/src/test/resources/datasets/initial-dataset.xml
dbunit-demo/src/test/java/com/eshop/dbunit/EshopDbUnitTest.java
```

Dataset XML chứa dữ liệu mẫu cho các bảng như `users`, `categories` và
`products`.

Test thực hiện các bước:

1. Đọc dataset XML.
2. Đưa database về trạng thái đã biết bằng `CLEAN_INSERT`.
3. Truy vấn database bằng JDBC.
4. Assert dữ liệu mong đợi tồn tại.

Lệnh chạy:

```bash
mvn test
```

Kết quả quan sát:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Kết quả này cho thấy demo DbUnit đã chạy thành công.

### 5.2 Demo Database Rider

Nhóm tạo một Maven project khác cho Database Rider.

Các file chính:

```text
database-rider-demo/pom.xml
database-rider-demo/src/test/resources/datasets/eshop-users.yml
database-rider-demo/src/test/java/com/eshop/databaserider/EshopDatabaseRiderTest.java
```

Dataset YAML chứa dữ liệu mẫu cho `users`, `categories` và `products`.

Test thực hiện các bước:

1. Đọc dataset YAML.
2. Nạp dataset vào SQLite.
3. Truy vấn bảng `users`.
4. Assert bảng có đúng số dòng mong đợi.

Lệnh chạy:

```bash
mvn test
```

Kết quả quan sát:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Kết quả này cho thấy demo Database Rider đã chạy thành công.

### 5.3 Demo Tonic.ai

Quy trình Tonic.ai Structural:

1. Reset SQLite database của EShop.
2. Export `users`, `products`, `coupons` và `orders` sang CSV.
3. Upload từng CSV vào Tonic.ai.
4. Tạo file group riêng cho mỗi schema.
5. Cấu hình generator cho các cột nhạy cảm.
6. Chạy data generation.
7. Tải các file CSV đã tạo về máy.
8. So sánh dữ liệu gốc và dữ liệu generated.

Các cột nhạy cảm trong `users`:

| Cột | Lý do nhạy cảm |
| --- | --- |
| `name` | Tên người dùng |
| `email` | Thông tin định danh cá nhân |
| `password` | Bí mật đăng nhập |
| `shipping_address` | Địa chỉ cá nhân |
| `phone` | Số điện thoại cá nhân |

Các cột như `id`, `role` và foreign key nên được giữ ổn định để không phá vỡ
quan hệ giữa các bảng.

Trong seminar, Tonic.ai được dùng cho dữ liệu `users`. Kiểm tra chính là so sánh
CSV ban đầu với CSV generated và xác nhận các giá trị nhạy cảm đã thay đổi.

## 6. Kịch bản kiểm thử trên EShop

### Kịch bản A: Seed và kiểm tra người dùng

Kịch bản cho DbUnit và Database Rider:

```text
Nạp dữ liệu test đã biết vào SQLite database và kiểm tra bảng users.
```

Kết quả mong đợi:

1. Database được reset về trạng thái có kiểm soát.
2. Dataset tạo một admin và một người dùng test thông thường.
3. Truy vấn xác nhận bảng `users` có dữ liệu mong đợi.

DbUnit sử dụng XML dataset:

```text
src/test/resources/datasets/initial-dataset.xml
```

Database Rider sử dụng YAML dataset:

```text
src/test/resources/datasets/eshop-users.yml
```

### Kịch bản B: Checkout và kiểm tra đơn hàng

Kịch bản EShop:

```text
Đăng nhập, thêm sản phẩm vào giỏ hàng, checkout, sau đó kiểm tra bảng orders.
```

Kết quả mong đợi sau checkout:

1. Bảng `orders` có đơn hàng mới.
2. `user_id` thuộc về người dùng đang đăng nhập.
3. `total_amount` khớp tổng tiền giỏ hàng.
4. `status` mặc định là `pending`.

Có thể kiểm tra bằng SQL:

```sql
SELECT COUNT(*) FROM orders;
SELECT user_id, total_amount, status FROM orders;
```

### Kịch bản C: Che dữ liệu người dùng nhạy cảm

Kịch bản cho Tonic.ai:

```text
Export users sang CSV, upload lên Tonic.ai, che các cột nhạy cảm và tải CSV generated.
```

Kết quả mong đợi:

1. `name`, `email`, `password`, `shipping_address` và `phone` được thay đổi.
2. Dữ liệu generated vẫn có định dạng phù hợp để test.
3. Cột quan hệ như `id` được giữ ổn định.
4. Dữ liệu có thể dùng an toàn hơn cho demo hoặc staging.

## 7. So sánh ba công cụ

| Tiêu chí | DbUnit | Database Rider | Tonic.ai |
| --- | --- | --- | --- |
| Loại công cụ | Thư viện kiểm thử | Lớp tiện ích trên DbUnit | Nền tảng dữ liệu kiểm thử |
| Input phổ biến | XML dataset | YAML dataset | CSV hoặc database source |
| Output chính | Kết quả test | Kết quả test | Dữ liệu generated |
| Độ dễ sử dụng | Trung bình | Dễ hơn DbUnit | Dễ sau khi quen giao diện |
| Assert kết quả database | Có | Có | Không tự thực hiện |
| Che dữ liệu nhạy cảm | Không | Không | Có |
| Vai trò trong EShop | Reset và kiểm tra database | Database test dễ bảo trì hơn | Tạo dữ liệu test an toàn |

DbUnit và Database Rider kiểm tra database có đúng hay không. Tonic.ai tạo dữ
liệu test an toàn hơn. Ba công cụ bổ trợ nhưng không thay thế nhau.

## 8. Phân tích khả năng tái sử dụng

Demo đầu tiên là proof of concept, chưa phải testing framework có thể tái sử
dụng trực tiếp. Code hiện tại có các phần hard-code:

- JDBC URL, username và password của HSQLDB nằm trong class test.
- Lệnh `CREATE TABLE` được viết trực tiếp trong Java.
- Tên bảng được cố định là `USER_ACCOUNT` và `PRODUCT`.
- Đường dẫn dataset cố định trong setup hoặc annotation.
- SQL query và expected row count bằng `2` được ghi trực tiếp.
- Package Java và metadata Maven còn gắn với EShop.

### 8.1 Ước lượng khả năng tái sử dụng hiện tại

Các tỷ lệ sau là ước lượng kỹ thuật dựa trên cấu trúc code, không phải kết quả
benchmark đã đo lường.

| Thành phần | Khả năng dùng lại trong dự án Java relational khác |
| --- | --- |
| Maven dependencies | 80-90% |
| Annotation DbUnit và Database Rider | 80-90% |
| Connection và test lifecycle | 50-70% |
| Cách seed dataset và assertion | 60-80% |
| Schema, tên bảng, SQL và dataset | Gần 0% |
| Business assertion riêng của dự án | Gần 0% |

Tổng thể, khoảng 30-40% code hiện tại có thể copy trực tiếp sang một dự án
Java/JDBC khác. Phần dùng lại được chủ yếu là cấu hình framework và vòng đời
test. Schema, dataset, query và business assertion phải thay đổi vì chúng mô tả
hệ thống mới.

Đối với dự án NoSQL, gần như không thể dùng trực tiếp implementation DbUnit hoặc
Database Rider vì hai công cụ phụ thuộc JDBC và mô hình bảng. Phần có thể giữ
lại là quy trình tổng quát: arrange data, thực hiện thao tác, kiểm tra trạng thái
đã lưu và cleanup.

### 8.2 Refactor để tăng khả năng tái sử dụng

Testing harness nên tách hạ tầng khỏi test case theo domain:

```text
database-testing/
|-- src/test/java/support/
|   |-- DatabaseConnectionFactory.java
|   `-- DatabaseTestSupport.java
|-- src/test/java/tests/
|   |-- DbUnitDatasetTest.java
|   `-- DatabaseRiderDatasetTest.java
`-- src/test/resources/
    |-- database-test.properties
    |-- schema.sql
    `-- datasets/
        |-- seed.yml
        `-- expected.yml
```

Thông tin kết nối nên lấy từ configuration hoặc biến môi trường:

```properties
db.driver=${DB_DRIVER}
db.url=${DB_URL}
db.user=${DB_USER}
db.password=${DB_PASSWORD}
```

Khi chuyển sang một dự án relational khác, nhóm chỉ cần thay driver, URL,
`schema.sql`, dataset và business assertion. Sau refactor, ước lượng 70-80% test
harness có thể dùng lại trong dự án Java relational khác.

Business assertion vẫn phải riêng cho từng dự án vì đây chính là phần xác định
hành vi đúng của hệ thống. Nếu cố biến mọi assertion thành generic, test sẽ mất
ý nghĩa.

## 9. Ưu điểm và hạn chế

### DbUnit

Ưu điểm:

- Kiểm soát trạng thái database tốt.
- Thể hiện rõ nền tảng của database testing.
- Có thể reset dữ liệu trước mỗi test.

Hạn chế:

- XML dataset có thể dài.
- Setup code tương đối thấp cấp.
- Cần hiểu Java, JDBC và database schema.
- Không hỗ trợ NoSQL native.

### Database Rider

Ưu điểm:

- YAML dễ đọc hơn XML.
- Giảm setup code so với DbUnit thuần.
- Phù hợp với database test cần bảo trì lâu dài.

Hạn chế:

- Vẫn cần Java và Maven.
- Vẫn phụ thuộc các khái niệm của DbUnit.
- Không thay thế việc hiểu database schema.
- Không hỗ trợ NoSQL native.

### Tonic.ai

Ưu điểm:

- Có thể che dữ liệu nhạy cảm.
- Tạo dữ liệu test có định dạng thực tế.
- Hỗ trợ relational và một số NoSQL connector.
- Giảm nhu cầu sử dụng trực tiếp production data.

Hạn chế:

- Không phải công cụ assertion.
- Cần cấu hình generator chính xác.
- Thay đổi sai ID hoặc foreign key có thể phá quan hệ.
- Che email hoặc password có thể làm tài khoản demo không đăng nhập được.
- Một số connector và tính năng phụ thuộc gói license.

## 10. Các vấn đề đã gặp

| Vấn đề | Nguyên nhân hoặc ý nghĩa |
| --- | --- |
| Thiếu module `sqlite3` | Node.js dependency chưa được cài |
| Maven không parse được `pom.xml` | Có nội dung không hợp lệ phía sau thẻ đóng XML |
| DbUnit order test thất bại | Dữ liệu order mong đợi chưa tồn tại |
| Tonic báo multiple schemas | Các CSV khác cấu trúc được đưa vào cùng file group |
| `orders.csv` rỗng | Seed data chưa có đơn hàng |
| Email/password bị mask | Credential generated không còn khớp luồng đăng nhập demo |
| Database Rider import/package lỗi | Tên import phải phù hợp phiên bản Database Rider |

## 11. Bài học rút ra

Database testing cần dữ liệu ổn định. Nếu trạng thái database thay đổi không kiểm
soát, test sẽ khó lặp lại và khó tin cậy.

DbUnit và Database Rider hữu ích khi cần đưa database về trạng thái biết trước
và kiểm tra trạng thái sau hành động. Tonic.ai hữu ích khi cần dữ liệu test nhưng
không nên dùng thông tin nhạy cảm thật.

Quan hệ khóa ngoại cần được bảo toàn. Ví dụ, nếu `orders.user_id` trỏ đến
`users.id`, việc random ID không kiểm soát sẽ phá dữ liệu.

Database test có thể chứng minh những điều UI screenshot không thể hiện. Giao
diện có thể báo checkout thành công, nhưng database assertion mới xác nhận một
order được tạo với đúng user, tổng tiền và trạng thái.

Relational và NoSQL dùng cùng vòng đời kiểm thử nhưng có failure model khác
nhau. NoSQL test cần chú ý thêm consistency, partition, replication và schema
evolution.

Code test không thể generic hoàn toàn. Nên tái sử dụng test infrastructure và
workflow, còn schema, dataset và business assertion phải được thiết kế theo
từng project.

## 12. Kết luận

DbUnit giúp hiểu nền tảng database testing thông qua dataset, reset database và
assertion.

Database Rider giúp cùng workflow đó dễ đọc và dễ bảo trì hơn bằng annotation
và YAML.

Tonic.ai giúp tạo dữ liệu test an toàn, giảm nguy cơ lộ thông tin nhạy cảm.

Đối với EShop:

- DbUnit và Database Rider kiểm tra hành vi của relational database.
- Tonic.ai chuẩn bị dữ liệu an toàn hơn.
- Kịch bản checkout cho thấy vì sao cần kiểm tra database sau thao tác người dùng.

DbUnit và Database Rider là công cụ relational, trong khi Tonic Structural hỗ
trợ relational và một số NoSQL connector. Giải pháp có khả năng chuyển đổi giữa
các dự án nên tái sử dụng testing harness và workflow, đồng thời giữ schema,
dataset và business assertion riêng cho từng hệ thống.

## 13. Bằng chứng và phụ lục

Các tài liệu hỗ trợ:

- User Guide
- DbUnit Step-by-Step Guide
- Database Rider Step-by-Step Guide
- Tonic.ai Testing Guide
- AI Audit Report
- Activity Worksheet
- Pitch Script

### Bằng chứng DbUnit

![Bằng chứng DbUnit 1](Duc/evidence/DbUnit/DbUnit1.png)

![Bằng chứng DbUnit 2](Duc/evidence/DbUnit/DbUnit2.png)

![Bằng chứng DbUnit 3](Duc/evidence/DbUnit/DbUnit3.png)

![Bằng chứng DbUnit 4](Duc/evidence/DbUnit/DbUnit4.png)

### Bằng chứng Database Rider

![Bằng chứng Database Rider 1](Duc/evidence/DatabaseRider/DatabaseRider1.png)

![Bằng chứng Database Rider 2](Duc/evidence/DatabaseRider/DatabaseRider2.png)

### Bằng chứng Tonic.ai

![Bằng chứng Tonic.ai 1](<Duc/evidence/Tonic AI/Tonic1.png>)

![Bằng chứng Tonic.ai 2](<Duc/evidence/Tonic AI/Tonic2.png>)

![Bằng chứng Tonic.ai 3](<Duc/evidence/Tonic AI/Tonic3.png>)

![Bằng chứng Tonic.ai 4](<Duc/evidence/Tonic AI/Tonic4.png>)

![Bằng chứng Tonic.ai 5](<Duc/evidence/Tonic AI/Tonic5.png>)

![Bằng chứng Tonic.ai 6](<Duc/evidence/Tonic AI/Tonic6.png>)

![Bằng chứng Tonic.ai 7](<Duc/evidence/Tonic AI/Tonic7.png>)

![Bằng chứng Tonic.ai 8](<Duc/evidence/Tonic AI/Tonic8.png>)

![Bằng chứng Tonic.ai 9](<Duc/evidence/Tonic AI/Tonic9.png>)

Các lệnh dùng để tạo bằng chứng:

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

## 14. Tài liệu tham khảo

- DbUnit: https://www.dbunit.org/
- Database Rider: https://database-rider.github.io/database-rider/
- Tonic.ai: https://www.tonic.ai/
- Tonic Structural data connectors: https://docs.tonic.ai/app/setting-up-your-database/database-connectors
- Tonic Structural MongoDB support: https://docs.tonic.ai/app/setting-up-your-database/mongodb
