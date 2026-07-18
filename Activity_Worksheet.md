# Activity Worksheet: EShop Database Testing

## Thông tin hoạt động

- Thời gian: 20 phút
- Hình thức: Làm theo nhóm
- Chủ đề: DbUnit, Database Rider và Tonic.ai với EShop
- Kết quả cần nộp: Ảnh chụp màn hình và câu trả lời trong Mục 7

## 1. Mục tiêu

Sau hoạt động này, nhóm có thể:

1. Đưa EShop SQLite database về trạng thái seed ban đầu.
2. Chạy một database test bằng DbUnit và một test bằng Database Rider.
3. Nhận biết DbUnit dùng XML dataset, còn Database Rider dùng YAML dataset.
4. Nhận biết dữ liệu nhạy cảm và cách Tonic.ai tạo dữ liệu test an toàn hơn.

## 2. Cần xem trước khi vào seminar

Trước buổi seminar, mỗi thành viên cần skim các tài liệu sau:

- [User_Guide.md](User_Guide.md)
- [SCREencast_DEMO_SCRIPT.md](SCREencast_DEMO_SCRIPT.md)

Cần có sẵn trong máy:

- Java 17 hoặc mới hơn và Maven.
- Node.js và npm.
- Source code EShop đã clone.

> Không cần tự viết Java test, tạo `pom.xml`, hoặc import dữ liệu Tonic.ai vào database trong hoạt động 20 phút này.

## 3. Chuẩn bị (2 phút)

Mở Terminal tại thư mục gốc của project `SeminarTesting`, sau đó reset database:

```bash
cd backend
node database.js
cd ..
```

Kết quả mong đợi có dòng gần như sau:

```text
Database initialized and seeded (Phase 2).
Connected to database
```

Nếu lệnh báo lỗi `Cannot find module 'sqlite3'`, chạy:

```bash
cd backend
npm install
node database.js
cd ..
```

## 4. Hoạt động A: DbUnit với XML dataset (6 phút)

1. Mở file dataset DbUnit:

   ```text
   dbunit-demo/src/test/resources/datasets/initial-dataset.xml
   ```

2. Trả lời: dataset này có các bảng nào? Hãy ghi ít nhất một giá trị `email` trong bảng `users`.

3. Chạy DbUnit test:

   ```bash
   cd dbunit-demo
   mvn test
   cd ..
   ```

4. Kết quả mong đợi: Maven hiện `BUILD SUCCESS` và test không có Failure/Error.

5. Chụp một ảnh màn hình terminal hiện kết quả `mvn test`.

## 5. Hoạt động B: Database Rider với YAML dataset (6 phút)

1. Mở file dataset Database Rider:

   ```text
   database-rider-demo/src/test/resources/datasets/eshop-users.yml
   ```

2. So sánh nhanh với file XML ở Hoạt động A. Hãy ghi một điểm khác nhau về cú pháp/định dạng.

3. Chạy Database Rider test:

   ```bash
   cd database-rider-demo
   mvn test
   cd ..
   ```

4. Kết quả mong đợi: Maven hiện `BUILD SUCCESS` và test không có Failure/Error.

5. Chụp một ảnh màn hình file YAML hoặc terminal hiện kết quả test pass.

## 6. Hoạt động C: Nhận diện dữ liệu nhạy cảm trong Tonic.ai (4 phút)

Xem CSV `users.csv` đã export sẵn, hoặc giao diện Tonic.ai do nhóm trình bày mở.

1. Đánh dấu các cột nhạy cảm trong bảng `users`:

   - `id`
   - `name`
   - `email`
   - `password`
   - `role`
   - `phone`
   - `shipping_address`

2. Điền generator phù hợp vào bảng sau:

| Column | Generator/handling đề xuất |
| --- | --- |
| `name` |  |
| `email` |  |
| `password` |  |
| `phone` |  |
| `shipping_address` |  |
| `id` |  |

3. Trả lời: Vì sao không nên random `id` nếu bảng `orders` tham chiếu tới `user_id`?

4. Trả lời: Tonic.ai có tự động chứng minh rằng EShop chạy đúng hay không? Giải thích một câu.

## 7. Câu hỏi nộp lại (2 phút)

Nộp một file text hoặc một ảnh ghi rõ tên nhóm và câu trả lời:

1. DbUnit dùng định dạng dataset nào?
2. Database Rider dùng định dạng dataset nào trong demo này?
3. Cả hai tool trên dùng để làm gì với database trước/trong khi test?
4. Kết quả `mvn test` của nhóm là pass hay fail? Nếu fail, dán 1-2 dòng lỗi chính.
5. Một cột nhạy cảm và generator/handling phù hợp của cột đó trong Tonic.ai.
6. Trả lời cho hai câu hỏi ở Hoạt động C, bước 3 và 4.

Bằng chứng cần nộp:

- Một screenshot `mvn test` pass của DbUnit hoặc Database Rider.
- Một screenshot XML/YAML dataset hoặc giao diện Tonic.ai.

## 8. Hỗ trợ khi gặp lỗi

| Vấn đề | Cách xử lý nhanh |
| --- | --- |
| `mvn: command not found` | Kiểm tra Maven đã cài và `mvn -version` chạy được. |
| `Cannot find module 'sqlite3'` | Vào `backend`, chạy `npm install`, sau đó chạy lại `node database.js`. |
| Database test fail vì dữ liệu khác | Quay lại thư mục gốc, chạy `cd backend && node database.js`, rồi chạy lại test. |
| Không đăng nhập được Tonic.ai | Làm phần nhận diện generator trên CSV/screenshot của người trình bày. |

## 9. Chuẩn bị debrief

Cuối hoạt động, chọn một đại diện nhóm sẵn sàng chia sẻ:

- Một điểm khác nhau giữa XML và YAML dataset.
- Lý do cần kiểm soát trạng thái database trước khi test.
- Một cách Tonic.ai giúp giảm rủi ro lộ dữ liệu nhạy cảm.
