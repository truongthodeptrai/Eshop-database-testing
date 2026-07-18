# Báo cáo kiểm thử cơ sở dữ liệu cho EShop

Ngày: 2026-07-19

## 1. Giới thiệu

Kiểm thử cơ sở dữ liệu rất quan trọng vì nhiều lỗi ứng dụng không chỉ xuất hiện
trên giao diện người dùng. Lỗi có thể xảy ra khi dữ liệu được lưu, cập nhật, xóa
hoặc kiểm tra trong cơ sở dữ liệu.

Trong seminar này, hệ thống cần kiểm thử (System Under Test - SUT) là dự án
EShop tại https://github.com/ttbhanh/eshop-sut

EShop là một SUT phù hợp vì đây là ứng dụng mua sắm thông thường. Hệ thống có
người dùng, sản phẩm, danh mục, mã giảm giá, giỏ hàng, đơn hàng và chức năng quản
trị. Tất cả chức năng này đều phụ thuộc vào hành vi chính xác của cơ sở dữ liệu.

Ba công cụ được nghiên cứu trong báo cáo gồm:

- DbUnit
- Database Rider
- Tonic.ai

Mục tiêu chính là tìm hiểu cách các công cụ này hỗ trợ chuẩn bị dữ liệu kiểm
thử, kiểm tra kết quả trong cơ sở dữ liệu và tạo dữ liệu test an toàn hơn.

## 2. Tổng quan công cụ

| Công cụ | Mục đích chính | Được dùng cho |
| --- | --- | --- |
| DbUnit | Thiết lập cơ sở dữ liệu bằng file dataset và kiểm tra dữ liệu | Unit test và integration test cho cơ sở dữ liệu |
| Database Rider | Giúp viết test theo phong cách DbUnit dễ hơn | Bảo trì database test dễ hơn |
| Tonic.ai | Tạo dữ liệu test an toàn và che giấu dữ liệu nhạy cảm | Quản lý dữ liệu kiểm thử |

### DbUnit

DbUnit là thư viện kiểm thử dành cho Java. Công cụ có thể nạp một dataset vào cơ
sở dữ liệu trước khi test chạy. Sau khi test thực thi, chúng ta có thể kiểm tra
cơ sở dữ liệu có chứa dữ liệu mong đợi hay không.

Trong demo, DbUnit sử dụng dataset XML.

### Database Rider

Database Rider được xây dựng trên DbUnit. Công cụ cung cấp cách viết database
test gọn hơn. Database Rider có thể sử dụng dataset YAML, thường dễ đọc hơn XML.

Trong demo, Database Rider sử dụng dataset YAML.

### Tonic.ai

Tonic.ai khác với DbUnit và Database Rider. Đây không phải là công cụ assertion
chính. Tonic.ai được dùng để tạo hoặc che giấu dữ liệu phục vụ kiểm thử.

Đối với EShop, chức năng này hữu ích vì bảng `users` có thể chứa dữ liệu nhạy
cảm như tên, email, mật khẩu, số điện thoại và địa chỉ.

## 3. Kiểm thử cơ sở dữ liệu quan hệ và NoSQL

Kiểm thử cơ sở dữ liệu có cùng mục tiêu tổng quát trên cả hệ quan hệ và NoSQL:
chuẩn bị trạng thái được kiểm soát, thực thi thao tác, xác minh dữ liệu đã lưu và
dọn dẹp môi trường test. Tuy nhiên, rủi ro và assertion khác nhau vì hai mô hình
tổ chức và phân phối dữ liệu theo những cách khác nhau.

### 3.1 Kiểm thử cơ sở dữ liệu quan hệ

Kiểm thử cơ sở dữ liệu quan hệ thường kiểm tra:

- Định nghĩa bảng và cột.
- Khóa chính, khóa ngoại, ràng buộc duy nhất, `NOT NULL` và `CHECK`.
- Hành vi insert, update, delete và query.
- Phép join và quan hệ giữa các bảng.
- Giao dịch ACID, commit và rollback.
- Index và SQL execution plan.
- Database migration và khả năng tương thích ngược.
- Stored procedure, trigger và view nếu ứng dụng sử dụng chúng.

Test trên cơ sở dữ liệu quan hệ thường có thể dựa vào database engine để từ chối
dữ liệu không hợp lệ. Ví dụ, foreign key có thể ngăn `orders.user_id` tham chiếu
đến một user không tồn tại.

### 3.2 Kiểm thử cơ sở dữ liệu NoSQL

NoSQL là một nhóm rộng. Document database, key-value store, wide-column database
và graph database có nhu cầu kiểm thử khác nhau. Với document database như
MongoDB, hoạt động kiểm thử thường bao gồm:

- Kiểm tra cấu trúc document, field bắt buộc, kiểu dữ liệu, object lồng nhau và mảng.
- Kiểm tra schema validation nếu cơ sở dữ liệu hỗ trợ.
- Kiểm tra document nhúng và reference giữa các collection.
- Kiểm tra tính toàn vẹn ở tầng ứng dụng khi không có foreign key.
- Kiểm tra document query và aggregation pipeline.
- Kiểm tra phạm vi cập nhật atomic và hành vi transaction.
- Kiểm tra strong consistency hoặc eventual consistency sau khi ghi.
- Kiểm tra partition key, shard key, hot partition và phân phối dữ liệu.
- Kiểm tra secondary index, unique index và TTL index.
- Kiểm tra nhiều phiên bản document cùng tồn tại trong quá trình thay đổi schema.
- Kiểm tra retry, idempotency, replication và failover.

Một assertion chạy ngay sau thao tác ghi có thể đúng với strong consistency
nhưng không ổn định với eventual consistency. Test nên sử dụng bounded retry và
timeout rõ ràng thay vì `sleep` cố định.

### 3.3 Khác biệt chính

| Vấn đề kiểm thử | Cơ sở dữ liệu quan hệ | Cơ sở dữ liệu NoSQL |
| --- | --- | --- |
| Cấu trúc dữ liệu | Bảng, hàng và cột | Collection, document, key hoặc graph |
| Schema | Thường được khai báo và thực thi rõ ràng | Linh hoạt, tùy chọn hoặc do ứng dụng thực thi |
| Quan hệ | Foreign key và join | Dữ liệu nhúng hoặc reference do ứng dụng quản lý |
| Transaction | Giao dịch ACID phổ biến | Phụ thuộc database và phạm vi thao tác |
| Consistency | Thường là strong consistency trong một database | Strong, eventual hoặc có thể cấu hình |
| Phân phối | Thường được test như một dịch vụ database | Partition, sharding và replication là rủi ro chính |
| Thay đổi schema | Migration theo thứ tự | Nhiều phiên bản document có thể cùng tồn tại |
| Assertion điển hình | Row, constraint, join, transaction | Document shape, consistency, partition, aggregation |

### 3.4 Khả năng áp dụng của ba công cụ

| Công cụ | Cơ sở dữ liệu quan hệ | Cơ sở dữ liệu NoSQL |
| --- | --- | --- |
| DbUnit | Phù hợp trực tiếp thông qua JDBC và dataset dạng bảng | Không hỗ trợ native cho document hoặc key-value |
| Database Rider | Phù hợp vì sử dụng DbUnit và JDBC | Không hỗ trợ NoSQL native |
| Tonic Structural | Hỗ trợ nhiều connector quan hệ | Hỗ trợ một số connector như MongoDB và Amazon DynamoDB |

Vì vậy, DbUnit và Database Rider nên được mô tả là công cụ kiểm thử cơ sở dữ
liệu quan hệ. Dự án NoSQL thường cần native driver của database, test container
hoặc môi trường test riêng, cùng assertion hiểu document, consistency và
partition. Tonic Structural có thể chuẩn bị dữ liệu an toàn cho một số hệ NoSQL,
nhưng vẫn cần framework kiểm thử riêng để xác minh ứng dụng hoạt động đúng.

## 4. Cài đặt và thiết lập

Thiết lập cơ bản được sử dụng cho seminar gồm:

1. Cài Node.js cho backend và frontend của EShop.
2. Cài Java và Maven cho demo DbUnit và Database Rider.
3. Cài công cụ SQLite để kiểm tra cơ sở dữ liệu EShop.
4. Clone hoặc mở dự án EShop.
5. Chạy `npm install` trong các phần Node.js của dự án.
6. Chạy `node database.js` để tạo và seed cơ sở dữ liệu SQLite.
7. Tạo project Maven demo DbUnit.
8. Tạo project Maven demo Database Rider.
9. Chuẩn bị workspace Tonic.ai hoặc quy trình sinh dữ liệu Tonic.ai.

Cơ sở dữ liệu backend của EShop nằm tại:

```text
eshop-sut/backend/database.sqlite
```

Backend EShop sử dụng SQLite. File seed chính là:

```text
eshop-sut/backend/database.js
```

Cơ sở dữ liệu gồm các bảng như:

- `users`
- `categories`
- `products`
- `orders`
- `order_items`
- `coupons`

## 5. Thực hành và các bước demo

### 5.1 Demo DbUnit

Đối với DbUnit, nhóm tạo một project Maven nhỏ.

Các file chính:

```text
dbunit-demo/pom.xml
dbunit-demo/src/test/resources/datasets/initial-dataset.xml
dbunit-demo/src/test/java/com/eshop/dbunit/EshopDbUnitTest.java
```

Dataset XML chứa dữ liệu mẫu cho các bảng như `users`, `categories` và
`products`.

Test sử dụng DbUnit để:

1. Đọc dataset XML.
2. Khởi tạo lại cơ sở dữ liệu bằng `CLEAN_INSERT`.
3. Query cơ sở dữ liệu bằng JDBC.
4. Assert dữ liệu mong đợi tồn tại.

Lệnh chạy:

```bash
mvn test
```

Kết quả ghi nhận:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Kết quả cho thấy demo DbUnit đã chạy thành công.

### 5.2 Demo Database Rider

Đối với Database Rider, nhóm tạo một project Maven khác.

Các file chính:

```text
database-rider-demo/pom.xml
database-rider-demo/src/test/resources/datasets/eshop-users.yml
database-rider-demo/src/test/java/com/eshop/databaserider/EshopDatabaseRiderTest.java
```

Dataset YAML chứa dữ liệu mẫu cho `users`, `categories` và `products`.

Test sử dụng Database Rider để:

1. Đọc dataset YAML.
2. Nạp dataset vào SQLite.
3. Query bảng `users`.
4. Assert bảng có đúng số lượng row mong đợi.

Lệnh chạy:

```bash
mvn test
```

Kết quả ghi nhận:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Kết quả cho thấy demo Database Rider đã chạy thành công.

### 5.3 Demo Tonic.ai

Quy trình Tonic.ai Structural được thực hiện như sau:

1. Khởi tạo lại cơ sở dữ liệu SQLite của EShop.
2. Export các bảng `users`, `products`, `coupons` và `orders` sang CSV.
3. Upload các file CSV lên Tonic.ai.
4. Tạo file group.
5. Cấu hình generator cho các cột nhạy cảm.
6. Chạy data generation.
7. Tải các file CSV đã generate.
8. So sánh dữ liệu gốc và dữ liệu đã generate.

Các cột nhạy cảm quan trọng trong bảng `users`:

| Cột | Lý do nhạy cảm |
| --- | --- |
| `name` | Tên người dùng thật |
| `email` | Thông tin định danh cá nhân |
| `password` | Bí mật đăng nhập |
| `shipping_address` | Địa chỉ cá nhân |
| `phone` | Số điện thoại cá nhân |

Các cột như `id`, `role` và foreign key thường nên được giữ ổn định vì ứng dụng
cần chúng để bảo toàn quan hệ giữa các bảng.

Trong demo seminar, Tonic.ai được dùng cho dữ liệu `users`. Kiểm tra chính là so
sánh CSV gốc với CSV được generate và xác nhận các giá trị nhạy cảm đã thay đổi.

## 6. Kịch bản kiểm thử EShop

Demo seminar tập trung vào các kịch bản database testing đơn giản, phù hợp với
ba công cụ.

### Kịch bản A: Seed và kiểm tra user

Đối với DbUnit và Database Rider, kịch bản là:

Nạp dữ liệu test biết trước vào cơ sở dữ liệu SQLite của EShop và kiểm tra bảng users.

Kịch bản này hữu ích vì đăng nhập, checkout và profile đều cần user hợp lệ. Nếu
bảng `users` sai, nhiều chức năng EShop không thể hoạt động.

Kết quả mong đợi:

1. Cơ sở dữ liệu được đưa về trạng thái biết trước.
2. Dataset tạo hai user: một admin và một user test thông thường.
3. Query xác nhận bảng `users` có dữ liệu mong đợi.

DbUnit thực hiện bằng dataset XML:

```text
src/test/resources/datasets/initial-dataset.xml
```

Database Rider thực hiện cùng ý tưởng bằng dataset YAML:

```text
src/test/resources/datasets/eshop-users.yml
```

### Kịch bản B: Checkout và kiểm tra đơn hàng

Kịch bản demo ứng dụng EShop:

Đăng nhập bằng user test, thêm sản phẩm vào giỏ hàng, checkout và kiểm tra bảng orders.

Kết quả mong đợi sau checkout:

1. Bảng `orders` có đơn hàng mới.
2. `user_id` thuộc về user đang đăng nhập.
3. `total_amount` khớp với tổng tiền giỏ hàng.
4. `status` mặc định của đơn hàng là `pending`.

Có thể kiểm tra cơ sở dữ liệu bằng SQL:

```sql
SELECT COUNT(*) FROM orders;
SELECT user_id, total_amount, status FROM orders;
```

### Kịch bản C: Che giấu dữ liệu user nhạy cảm

Đối với Tonic.ai, kịch bản là:

Export users từ SQLite sang CSV, upload CSV lên Tonic.ai, che giấu cột nhạy cảm và tải CSV đã generate.

Kết quả mong đợi:

1. `name`, `email`, `password`, `shipping_address` và `phone` được thay đổi.
2. Giá trị được generate vẫn giống dữ liệu test thực tế.
3. Các cột quan hệ quan trọng như `id` được giữ ổn định.
4. Dữ liệu mới có thể dùng an toàn hơn cho demo hoặc staging.

## 7. So sánh

| Tiêu chí | DbUnit | Database Rider | Tonic.ai |
| --- | --- | --- | --- |
| Loại công cụ | Thư viện kiểm thử | Wrapper của thư viện kiểm thử | Nền tảng dữ liệu test |
| Input phổ biến | Dataset XML | Dataset YAML | CSV hoặc nguồn database |
| Output chính | Kết quả test | Kết quả test | Dữ liệu được generate |
| Mức độ dễ dùng | Trung bình | Dễ hơn DbUnit | Dễ sau khi làm quen UI |
| Có thể assert kết quả database | Có | Có | Không, nếu chỉ dùng riêng Tonic |
| Có thể che giấu dữ liệu nhạy cảm | Không | Không | Có |
| Cách dùng tốt nhất trong EShop | Reset và kiểm tra trạng thái database | Database test gọn hơn | Dữ liệu test an toàn hơn |

DbUnit và Database Rider được dùng để kiểm tra cơ sở dữ liệu có đúng hay không.
Tonic.ai được dùng để tạo dữ liệu an toàn hơn. Vì vậy, các công cụ bổ trợ lẫn
nhau nhưng không thay thế nhau.

## 8. Phân tích tính di động và khả năng tái sử dụng

Demo đầu tiên là proof of concept, chưa phải framework kiểm thử có khả năng tái
sử dụng cao. Việc review hai class DbUnit và Database Rider cho thấy các phần
hard-code sau:

- HSQLDB JDBC URL, username và password được viết trực tiếp trong test class.
- Lệnh `CREATE TABLE` được nhúng trong mã Java.
- Tên bảng bị cố định là `USER_ACCOUNT` và `PRODUCT`.
- Đường dẫn dataset bị cố định trong annotation hoặc test setup.
- SQL query và số row mong đợi là `2` bị cố định.
- Tên package Java và metadata Maven vẫn tham chiếu đến EShop.

### 8.1 Ước lượng mức tái sử dụng của demo hiện tại

Các giá trị sau là ước lượng kỹ thuật dựa trên cấu trúc code, không phải kết quả
benchmark đã đo.

| Thành phần | Khả năng tái sử dụng trong project Java quan hệ khác |
| --- | --- |
| Maven dependency | 80-90% |
| Annotation DbUnit và Database Rider | 80-90% |
| Mẫu connection và test lifecycle | 50-70% |
| Mẫu seed dataset và assertion | 60-80% |
| Schema, tên bảng, SQL và dataset | Gần 0% |
| Business assertion đặc thù project | Gần 0% |

Tổng thể, khoảng 30-40% code hiện tại có thể copy trực tiếp sang project
Java/JDBC khác. Phần tái sử dụng được là cấu hình framework và test lifecycle.
Database schema, dataset, query và business assertion phải thay đổi vì chúng mô
tả hệ thống mới.

Đối với project NoSQL, gần như không thể tái sử dụng trực tiếp phần triển khai
DbUnit hoặc Database Rider vì hai công cụ phụ thuộc JDBC và mô hình bảng. Phần
có thể tái sử dụng là quy trình cấp cao: chuẩn bị dữ liệu, thực thi thao tác, xác
minh trạng thái đã lưu và dọn dẹp.

### 8.2 Refactor để tăng khả năng tái sử dụng

Test harness cho cơ sở dữ liệu quan hệ nên tách hạ tầng khỏi test case đặc thù
nghiệp vụ:

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

Thông tin kết nối nên lấy từ configuration hoặc biến môi trường thay vì mã Java:

```properties
db.driver=${DB_DRIVER}
db.url=${DB_URL}
db.user=${DB_USER}
db.password=${DB_PASSWORD}
```

Khi chuyển sang project quan hệ khác, nhóm chỉ cần thay database configuration,
`schema.sql`, dataset và business assertion. Sau refactor, ước lượng 70-80% test
harness có thể tái sử dụng trong một project Java quan hệ khác. Domain assertion
vẫn phải đặc thù vì nếu biến chúng thành generic thì test sẽ mất ý nghĩa.

## 9. Ưu điểm và hạn chế

### DbUnit

Ưu điểm:

- Kiểm soát mạnh trạng thái cơ sở dữ liệu.
- Phù hợp để học nền tảng database testing.
- Có thể reset dữ liệu trước mỗi test.

Hạn chế:

- Dataset XML có thể dài.
- Mã thiết lập ở mức thấp hơn.
- Người dùng cần hiểu Java, JDBC và bảng trong database.
- Không hỗ trợ native cho NoSQL document hoặc key-value.

### Database Rider

Ưu điểm:

- Dataset YAML dễ đọc hơn.
- Giảm một phần mã thiết lập so với DbUnit thuần.
- Phù hợp cho database test cần bảo trì lâu dài.

Hạn chế:

- Vẫn yêu cầu Java và Maven.
- Vẫn cần hiểu khái niệm DbUnit.
- Không thay thế việc hiểu database schema.
- Không hỗ trợ NoSQL native vì vẫn dùng DbUnit và JDBC.

### Tonic.ai

Ưu điểm:

- Có thể che giấu dữ liệu nhạy cảm.
- Có thể tạo dữ liệu test trông thực tế.
- Hữu ích khi không nên dùng trực tiếp dữ liệu production.
- Hỗ trợ cơ sở dữ liệu quan hệ và một số connector NoSQL.

Hạn chế:

- Không phải công cụ assertion.
- Cần cấu hình generator chính xác.
- Nếu ID hoặc foreign key thay đổi sai, quan hệ có thể bị phá vỡ.
- Nếu email hoặc password bị che giấu, tài khoản demo mặc định có thể không đăng nhập được.
- Mức hỗ trợ và tính năng NoSQL phụ thuộc connector và license.

## 10. Các vấn đề đã gặp

| Vấn đề | Giải thích ngắn |
| --- | --- |
| Thiếu module `sqlite3` | Dependency Node.js chưa được cài đặt |
| Lỗi parse Maven `pom.xml` | Nội dung không hợp lệ bị copy thêm vào cuối `pom.xml` |
| DbUnit order test thất bại | Order mong đợi chưa tồn tại |
| Tonic.ai báo nhiều schema | Các CSV có schema khác nhau bị upload vào cùng một file group |
| `orders.csv` rỗng | Chưa có order sau khi reset seed data |
| Email/password bị mask ảnh hưởng đăng nhập | Credential được generate không còn khớp với luồng demo |
| Lỗi import/package Database Rider | Tên import Java phải khớp với phiên bản Database Rider |

## 11. Bài học rút ra

Database testing cần dữ liệu ổn định. Nếu cơ sở dữ liệu thay đổi ngẫu nhiên,
test sẽ khó đáng tin cậy.

DbUnit và Database Rider hữu ích khi cần chuẩn bị trạng thái database biết trước
trước khi test. Hai công cụ cũng hữu ích khi cần kiểm tra database sau một hành
động.

Tonic.ai hữu ích khi cần dữ liệu test nhưng không nên dùng dữ liệu nhạy cảm thật.
Công cụ bảo vệ quyền riêng tư bằng cách thay thế tên, email, địa chỉ và số điện
thoại.

Quan hệ foreign key rất quan trọng. Ví dụ, nếu `orders.user_id` tham chiếu đến
`users.id`, cần cẩn thận khi generate hoặc thay đổi ID.

Database test có thể xác minh điều mà screenshot UI không chứng minh được. Ví
dụ, sau checkout, UI có thể báo thành công nhưng database test mới xác nhận row
thật sự đã được thêm vào bảng `orders`.

Relational và NoSQL có cùng test lifecycle nhưng failure model khác nhau. NoSQL
đòi hỏi kiểm tra thêm consistency, partition, replication, document version và
quy tắc toàn vẹn ở tầng ứng dụng.

## 12. Kết luận

DbUnit hữu ích để hiểu nền tảng database testing. Công cụ cho thấy cách nạp
dataset và kiểm tra kết quả trong cơ sở dữ liệu.

Database Rider dễ bảo trì hơn vì dataset YAML gọn và workflow test ngắn hơn.

Tonic.ai hữu ích để tạo dữ liệu test an toàn. Công cụ giúp generate hoặc mask dữ
liệu để quá trình kiểm thử không làm lộ thông tin nhạy cảm.

Đối với EShop, ba công cụ phối hợp tốt theo các vai trò khác nhau:

- DbUnit và Database Rider giúp xác minh hành vi cơ sở dữ liệu.
- Tonic.ai giúp chuẩn bị dữ liệu test an toàn hơn.
- Kịch bản checkout EShop cho thấy lý do cần kiểm tra database sau hành động của user.

Seminar cũng làm rõ một ranh giới quan trọng: DbUnit và Database Rider là công
cụ cho cơ sở dữ liệu quan hệ, trong khi Tonic Structural hỗ trợ cơ sở dữ liệu
quan hệ và một số connector NoSQL. Giải pháp có tính di động nên tái sử dụng test
harness và workflow, đồng thời giữ schema, dataset và business assertion riêng
cho từng project.

## 13. Bằng chứng và phụ lục

Tài liệu hỗ trợ:

- User Guide
- Hướng dẫn DbUnit từng bước
- Hướng dẫn Database Rider từng bước
- Hướng dẫn kiểm thử Tonic.ai
- AI Audit Report

Bằng chứng screenshot seminar:

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

Các lệnh được sử dụng để tạo bằng chứng:

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
