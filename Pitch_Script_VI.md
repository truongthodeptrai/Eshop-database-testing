# Kịch bản pitch seminar - Database Testing for EShop

Thời lượng mục tiêu: 8-10 phút trước khi chuyển sang live demo.

## Slide 1 - Database Testing for EShop

Chào thầy và các bạn. Hôm nay nhóm mình trình bày về database testing thông qua ba công cụ là DbUnit, Database Rider và Tonic.ai. Hệ thống demo là EShop, sử dụng Node.js và SQLite. Ngoài việc demo ba tool, nhóm cũng làm rõ chúng phù hợp với relational database hay NoSQL, và code demo có thể tái sử dụng được bao nhiêu khi chuyển sang project khác.

## Slide 2 - Database bugs can survive a successful UI flow

Một thao tác thành công trên giao diện chưa chứng minh dữ liệu bên dưới hoàn toàn đúng. Ví dụ checkout có thể hiện thông báo thành công nhưng order bị ghi sai user, sai tổng tiền hoặc sai trạng thái. Database testing kiểm tra trạng thái được lưu thật sự, các quan hệ dữ liệu và khả năng lặp lại của test.

## Slide 3 - Same lifecycle, different failure model

Cả relational và NoSQL đều dùng quy trình arrange, act, assert và cleanup. Điểm khác nằm ở thứ cần assert. Relational tập trung vào table, constraint, join và transaction. NoSQL tập trung vào document structure, application-level integrity, consistency, partition và replication.

## Slide 4 - Relational testing asks the database to enforce integrity

Trong relational database, schema thường được khai báo rõ. Chúng ta kiểm tra primary key, foreign key, unique, not null, check constraint, transaction và migration. Database có thể trực tiếp từ chối dữ liệu sai. Với EShop, foreign key giúp bảo đảm một order tham chiếu đến đúng user và product.

## Slide 5 - NoSQL shifts more responsibility to application tests

NoSQL không chỉ có MongoDB mà còn gồm key-value, wide-column và graph database. Với document database, test phải kiểm tra field, nested object, array, document reference, aggregation và nhiều phiên bản document. Nếu hệ thống dùng eventual consistency, test không nên assert ngay lập tức hoặc sleep cố định, mà cần retry có giới hạn và timeout rõ ràng. Ngoài ra còn phải kiểm tra partition key, shard, replication và idempotency.

## Slide 6 - The three tools cover different jobs

DbUnit và Database Rider là tool kiểm thử relational vì chúng dựa trên JDBC và mô hình table. Tonic Structural khác ở chỗ nó chuẩn bị dữ liệu an toàn và hỗ trợ cả relational lẫn một số NoSQL như MongoDB và DynamoDB. Tuy nhiên Tonic không tự assert rằng application đúng; sau khi generate dữ liệu vẫn cần framework test riêng.

## Slide 7 - DbUnit makes database state deterministic

DbUnit đưa database về trạng thái biết trước bằng dataset XML. Trong demo, nhóm load dataset, chạy CLEAN_INSERT rồi assert dữ liệu. Giá trị chính của DbUnit là giúp test lặp lại được: cùng input thì mỗi lần test bắt đầu từ cùng database state.

## Slide 8 - Database Rider reduces test ceremony

Database Rider chạy trên DbUnit nhưng dùng annotation và YAML để giảm code setup. Workflow vẫn là seed, execute và verify, nhưng dataset dễ đọc và test ngắn hơn. Rider phù hợp khi dự án Java muốn duy trì nhiều database test mà không muốn lặp lại phần cấu hình DbUnit.

## Slide 9 - Tonic creates safer test data

Tonic.ai giải quyết bài toán khác: không dùng trực tiếp dữ liệu nhạy cảm trong môi trường test. Nhóm export users, products, coupons và orders sang CSV, cấu hình generator cho name, email, password, address và phone, rồi tạo dữ liệu thay thế. Các ID và foreign key cần được giữ ổn định để không phá quan hệ.

## Slide 10 - Our demo proves the workflow, not portability yet

Nhóm thừa nhận code proof of concept hiện tại còn hard-code. JDBC URL, HSQLDB, câu lệnh tạo bảng, tên USER_ACCOUNT và PRODUCT, dataset path, query và expected count đều nằm trực tiếp trong test. Vì vậy code hiện tại chứng minh tool chạy được, nhưng chưa phải một framework có thể copy nguyên sang project khác.

## Slide 11 - Current reuse is 30-40%; refactoring can reach 70-80%

Đây là ước lượng kỹ thuật dựa trên cấu trúc code, không phải benchmark. Trong một Java relational project khác, dependencies, annotation và test lifecycle có thể giữ lại. Schema, dataset, SQL và business assertion gần như phải thay hoàn toàn. Tổng thể, code hiện tại tái sử dụng trực tiếp khoảng 30 đến 40 phần trăm. Nếu tách connection, schema và dataset thành configuration, phần test harness có thể tái sử dụng khoảng 70 đến 80 phần trăm.

## Slide 12 - A reusable harness separates infrastructure from business rules

Giải pháp là tạo connection factory dùng biến môi trường, đưa schema ra schema.sql, đưa dataset vào resources và tạo base test support. Khi chuyển project relational, nhóm chỉ thay driver, URL, schema, dataset và assertion. Business assertion vẫn phải riêng vì đó chính là ý nghĩa của test. Với NoSQL, DbUnit và Rider phải được thay bằng native driver và framework phù hợp, nhưng quy trình arrange, act, assert, cleanup vẫn tái sử dụng.

## Slide 13 - Use the right tool at each boundary

Kết luận, DbUnit kiểm soát database state, Database Rider làm workflow đó dễ duy trì hơn, còn Tonic tạo dữ liệu test an toàn. Relational và NoSQL có cùng mục tiêu nhưng khác failure model. Nhóm không kỳ vọng một test dùng cho mọi project; nhóm tái sử dụng hạ tầng và quy trình, còn schema và business rule phải được thiết kế lại. Bây giờ nhóm sẽ chuyển sang live demo trên EShop.
