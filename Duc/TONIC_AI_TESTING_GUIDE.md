# Huong dan test bang Tonic.ai voi EShop SUT

Muc tieu: dung Tonic.ai Structural de tao du lieu test gia lap/an toan tu database EShop, sau do dung bo du lieu do de test lai EShop.

Tai lieu chinh thuc da tham khao:

- Tonic Structural User Guide: https://docs.tonic.ai/app
- Getting started with Structural free trial: https://docs.tonic.ai/app/quick-start-guide
- File connector: https://docs.tonic.ai/app/setting-up-your-database/file-connector
- Generator information: https://docs.tonic.ai/app/generation/generators
- Running data generation jobs: https://docs.tonic.ai/app/workflows/data-generation-run-job

## 1. Chon cach demo phu hop voi EShop

EShop dang dung SQLite:

```bash
/Users/jiduckiess/Documents/SeminarTesting/backend/database.sqlite
```

Tonic.ai Structural free trial thuong ho tro ket noi truc tiep cac database nhu PostgreSQL, MySQL, SQL Server, Snowflake, MongoDB, BigQuery. SQLite khong phai connector chinh trong free trial, nen cach de demo nhat la:

1. Export bang SQLite ra file CSV.
2. Upload CSV vao Tonic.ai bang Local files/File connector.
3. Gan generator cho cac cot nhay cam.
4. Generate du lieu moi.
5. Tai CSV da duoc generate ve.
6. Dung CSV do lam test data cho EShop.

## 2. Xac dinh bang va cot can bao ve

Bang quan trong nhat de demo voi Tonic.ai la `users`.

Cot nhay cam:

| Table | Column | Ly do |
| --- | --- | --- |
| `users` | `name` | Ten nguoi dung |
| `users` | `email` | Thong tin dinh danh |
| `users` | `password` | Thong tin xac thuc |
| `users` | `shipping_address` | Dia chi giao hang |
| `users` | `phone` | So dien thoai |

Bang co the export them de giu ngu canh eShop:

| Table | Muc dich |
| --- | --- |
| `products` | Du lieu san pham de test gio hang/checkout |
| `orders` | Du lieu don hang de test lich su don hang/admin |
| `coupons` | Du lieu coupon de test ma giam gia |

## 3. Reset va export database EShop ra CSV

Reset database ve du lieu mau:

```bash
cd /Users/jiduckiess/Documents/SeminarTesting/backend
node database.js
```

Tao thu muc chua file export:

```bash
cd /Users/jiduckiess/Documents/SeminarTesting
mkdir -p tonic-export/source
```

Export cac bang can demo:

```bash
sqlite3 -header -csv backend/database.sqlite "SELECT * FROM users;" > tonic-export/source/users.csv
sqlite3 -header -csv backend/database.sqlite "SELECT * FROM products;" > tonic-export/source/products.csv
sqlite3 -header -csv backend/database.sqlite "SELECT * FROM coupons;" > tonic-export/source/coupons.csv
sqlite3 -header -csv backend/database.sqlite "SELECT * FROM orders;" > tonic-export/source/orders.csv
```

Kiem tra file da tao:

```bash
ls -la tonic-export/source
head -n 5 tonic-export/source/users.csv
```

Bang chung can chup:

- Terminal sau khi export CSV.
- Noi dung `users.csv` truoc khi dua vao Tonic.ai.

## 4. Dang ky/dang nhap Tonic.ai Structural

1. Vao https://app.tonic.ai.
2. Chon `Create Account` neu chua co tai khoan.
3. Kich hoat tai khoan qua email.
4. Dang nhap vao Tonic Structural.

Luu y:

- Free trial co the yeu cau email cong ty/truong, khong phai email public.
- Neu khong tao duoc free trial, van co the ghi vao log la `Blocked: account/free trial unavailable`, kem anh man hinh loi.

Bang chung can chup:

- Trang dang nhap hoac trang workspace cua Tonic.ai.
- Neu bi chan free trial, chup man hinh thong bao loi.

## 5. Tao workspace bang Local files

Trong Tonic.ai:

1. Chon tao workspace moi.
2. Chon cach dung du lieu rieng.
3. Chon `Upload Files` hoac `Local files`.
4. Dat ten workspace, vi du:

```text
EShop SQLite CSV Demo
```

5. Upload cac file CSV trong:

```bash
tonic-export/source/
```

6. Tao file group/table tu moi CSV:

| File | File group/table nen dat |
| --- | --- |
| `users.csv` | `users` |
| `products.csv` | `products` |
| `coupons.csv` | `coupons` |
| `orders.csv` | `orders` |

Bang chung can chup:

- Workspace vua tao.
- Man hinh file groups/tables.
- Cac file CSV da upload.

## 6. Cau hinh generator cho du lieu nhay cam

Vao bang `users`, gan generator cho cac cot:

| Column | Generator nen dung | Ky vong |
| --- | --- | --- |
| `name` | Name/Person generator | Tao ten gia lap |
| `email` | Email generator | Tao email gia dung format |
| `password` | Constant/Character scramble | Khong giu password that |
| `shipping_address` | Address generator | Tao dia chi gia lap |
| `phone` | Phone generator | Tao so dien thoai gia lap |

Cot nen giu nguyen:

| Column | Ly do |
| --- | --- |
| `id` | Giu quan he voi order/user |
| `role` | Can giu `admin`/`user` de test phan quyen |
| `login_attempts` | Can giu de test khoa tai khoan |
| `locked_until` | Co the giu/null tuy scenario |

Luu y quan trong:

- Khong nen random `id` neu cac bang khac tham chieu `user_id`.
- Neu thay doi `email`, tai khoan login mac dinh `test@eshop.com` co the khong con dung. De demo login, nen tao rieng mot user test sau khi import hoac giu lai mot dong user test.

Bang chung can chup:

- Man hinh gan generator cho `users.name`.
- Man hinh gan generator cho `users.email`.
- Privacy/sensitivity report neu Tonic hien thi.

## 7. Chay data generation

Trong workspace Tonic.ai:

1. Kiem tra cac table/file groups da cau hinh xong.
2. Chon `Generate Data` hoac `Run Generation`.
3. Doi job chay xong.
4. Mo job details de xem trang thai.
5. Tai output CSV da generate ve may.

Tonic Structural dung cac table mode va generator da cau hinh de tao du lieu transformed cho destination database hoac file output.

Bang chung can chup:

- Nut/lua chon chay generation.
- Job status success.
- File output da download.

## 8. Kiem tra du lieu generated

Sau khi tai file generated ve, luu vao:

```bash
/Users/jiduckiess/Documents/SeminarTesting/tonic-export/generated
```

Kiem tra nhanh:

```bash
ls -la tonic-export/generated
head -n 5 tonic-export/generated/users.csv
```

Can xac nhan:

| Kiem tra | Ket qua mong doi |
| --- | --- |
| `name` | Khac du lieu goc, nhung van giong ten nguoi |
| `email` | Khac email goc, van dung format email |
| `phone` | Khac so goc, van giong so dien thoai |
| `shipping_address` | Khac dia chi goc |
| `role` | Van giu `admin`/`user` neu can test phan quyen |

## 9. Dung du lieu Tonic.ai de test EShop

Co 2 cach demo:

### Cach A - Demo bang chung du lieu

Cach nay de nhat cho seminar:

1. Show `users.csv` goc.
2. Show `users.csv` sau khi generate bang Tonic.ai.
3. So sanh cac cot nhay cam da bi thay doi.
4. Giai thich rang du lieu generated co the dung cho moi truong test/staging.

### Cach B - Import lai vao SQLite de chay EShop

Cach nay kho hon vi can dam bao CSV khop schema.

Quy trinh:

1. Backup database hien tai:

```bash
cp backend/database.sqlite backend/database.before-tonic.sqlite
```

2. Reset database:

```bash
cd /Users/jiduckiess/Documents/SeminarTesting/backend
node database.js
```

3. Import file generated vao bang `users`.

Luu y: khong nen import de len toan bo database neu chua kiem tra schema va quan he khoa ngoai. Voi seminar, chi can demo generated CSV va chay EShop voi seed data goc la du.

## 10. Scenario demo de ghi vao Stage S3

Scenario de trinh bay:

1. EShop co bang `users` chua thong tin nhay cam.
2. Export `users` ra CSV.
3. Upload CSV vao Tonic.ai Structural bang Local files.
4. Gan generator cho `name`, `email`, `phone`, `shipping_address`.
5. Run data generation.
6. Download generated CSV.
7. So sanh truoc/sau.
8. Ket luan: Tonic.ai phu hop voi test data management vi tao du lieu test an toan hon, gan voi database testing.

## 11. Noi dung can ghi vao private team log

Ghi lai:

| Muc | Noi dung |
| --- | --- |
| Tool | Tonic.ai Structural |
| Muc tieu | Tao synthetic/anonymized test data cho EShop |
| Input | CSV export tu SQLite EShop |
| Output | CSV generated tu Tonic.ai |
| Bang da test | `users`, co the them `products`, `coupons`, `orders` |
| Ket qua | Generated data thay the du lieu nhay cam |
| Loi gap phai | Ghi ro neu khong tao duoc account/free trial hoac upload loi |

## 12. Cau noi ngan gon khi thuyet trinh

Co the noi:

> Nhom em dung Tonic.ai Structural de quan ly test data cho EShop. Vi EShop dung SQLite, nhom export cac bang thanh CSV, upload vao Tonic bang Local files, gan generator cho cac cot nhay cam nhu name, email, phone va shipping_address, sau do generate bo du lieu test moi. Ket qua la du lieu test van co cau truc giong du lieu that nhung khong dung truc tiep thong tin nhay cam.

