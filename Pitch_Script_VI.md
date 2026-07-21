# Kịch bản pitch seminar - Database Testing for EShop

Thời lượng mục tiêu: khoảng 8-10 phút, sau đó chuyển sang phần live demo.

## Slide 1 - Kiểm thử cơ sở dữ liệu cho EShop

Chào thầy và các bạn. Hôm nay nhóm mình trình bày về kiểm thử cơ sở dữ liệu thông qua hệ thống EShop. Nhóm sử dụng ba công cụ: DbUnit, Database Rider và Tonic.ai.

EShop là một ứng dụng bán hàng sử dụng Node.js và SQLite. Trong phần trình bày, nhóm sẽ giải thích sự khác nhau giữa kiểm thử cơ sở dữ liệu quan hệ và NoSQL, demo một kịch bản checkout, kiểm thử bằng DbUnit và Database Rider, mask dữ liệu bằng Tonic.ai, rồi refactor cấu trúc test để có thể tái sử dụng cho project khác.

## Slide 2 - Vì sao cần kiểm thử cơ sở dữ liệu?

Một thao tác thành công trên giao diện chưa chứng minh dữ liệu bên dưới hoàn toàn đúng. Ví dụ, checkout có thể hiển thị thành công nhưng order lại lưu sai người dùng, sai tổng tiền hoặc sai trạng thái.

Database testing kiểm tra sự thật được lưu trong database: order có được tạo hay không, user nào sở hữu order, tổng tiền có khớp với giỏ hàng và trạng thái ban đầu có đúng là `pending` hay không. Khi có assertion trên database, chúng ta có bằng chứng cụ thể và có thể lặp lại test nhiều lần.

## Slide 3 - Vòng đời chung của database testing

Relational database và NoSQL đều có thể áp dụng cùng một vòng đời: chuẩn bị dữ liệu, thực thi hành động, xác minh kết quả và dọn dẹp dữ liệu.

Điểm khác nhau nằm ở loại lỗi cần kiểm tra. Relational database tập trung vào bảng, cột, constraint, join và transaction. NoSQL tập trung nhiều hơn vào cấu trúc document, dữ liệu lồng nhau, consistency, partition, replication và tính toàn vẹn do ứng dụng quản lý.

## Slide 4 - Kiểm thử cơ sở dữ liệu quan hệ

Trong relational database, schema và các ràng buộc thường được khai báo rõ ràng. Chúng ta kiểm tra kiểu dữ liệu, primary key, foreign key, `UNIQUE`, `NOT NULL`, `CHECK`, phép join, transaction, migration, index và query plan.

Ưu điểm là database engine có thể trực tiếp từ chối nhiều trạng thái không hợp lệ. Ví dụ, foreign key có thể ngăn `orders.user_id` tham chiếu đến một user không tồn tại. Với EShop, đây là mô hình chính mà DbUnit và Database Rider hỗ trợ thông qua JDBC.

## Slide 5 - Kiểm thử cơ sở dữ liệu NoSQL

NoSQL là một nhóm rộng gồm document database, key-value store, wide-column database và graph database. Với document database như MongoDB, chúng ta kiểm tra field bắt buộc, kiểu dữ liệu, object lồng nhau, array, document reference và aggregation pipeline.

Ngoài ra, test cần kiểm tra consistency, partition key, shard, replication, retry và idempotency. Khi database dùng eventual consistency, assertion ngay sau thao tác ghi có thể chưa ổn định. Test nên dùng retry có giới hạn và timeout rõ ràng, thay vì sleep cố định.

## Slide 6 - Ranh giới của ba công cụ

Ba công cụ không thay thế hoàn toàn cho nhau.

DbUnit và Database Rider là công cụ kiểm thử database quan hệ, dựa trên JDBC và mô hình table. Hai công cụ này không hỗ trợ native cho MongoDB hoặc các loại NoSQL khác.

Tonic.ai tập trung vào việc tạo và mask dữ liệu kiểm thử an toàn. Tonic có thể làm việc với relational database và một số connector NoSQL. Tuy nhiên, Tonic tạo dữ liệu chứ không tự chứng minh application hoạt động đúng. Sau khi có dữ liệu, chúng ta vẫn cần test API, browser hoặc database assertion phù hợp.

## Slide 7 - DbUnit: kiểm soát trạng thái database

DbUnit đưa database về một trạng thái biết trước bằng dataset XML. Trong demo, nhóm dùng `CLEAN_INSERT` để dọn dữ liệu cũ và nạp lại dataset trước khi test.

Sau đó test thực hiện hoặc kiểm tra kết quả của nghiệp vụ checkout, rồi truy vấn database để assertion. Giá trị chính của DbUnit là tính xác định: mỗi lần test đều bắt đầu từ cùng một database state nên kết quả dễ lặp lại và dễ debug.

## Slide 8 - Database Rider: giảm phần setup lặp lại

Database Rider xây dựng trên ý tưởng của DbUnit nhưng làm workflow gọn hơn. Annotation và YAML dataset giúp test dễ đọc, giảm phần cấu hình thủ công và phù hợp khi project có nhiều database test.

Trong demo, nhóm dùng Rider để nạp dataset YAML, chạy cùng kịch bản checkout và kiểm tra các kết quả nghiệp vụ giống DbUnit. Điểm khác là cách tổ chức test và dataset dễ bảo trì hơn, còn assertion vẫn phải mô tả đúng business rule.

## Slide 9 - Tonic.ai: tạo dữ liệu test an toàn

Tonic.ai giải quyết bài toán khác: không dùng trực tiếp dữ liệu nhạy cảm trong môi trường test hoặc demo.

Nhóm export dữ liệu EShop thành các file CSV, sau đó đưa từng bảng vào file group riêng. Với bảng `users`, nhóm mask `name`, `email`, `password`, `shipping_address` và `phone`. Các cột như `id` và `role` có thể được giữ lại nếu scenario cần duy trì quan hệ hoặc kiểm tra phân quyền.

Tonic giữ format dữ liệu phù hợp nhưng không tự kiểm tra checkout đúng hay sai. Đây là công cụ chuẩn bị test data, không phải công cụ thay thế test framework.

## Slide 10 - Live demo checkout EShop

Bây giờ nhóm chuyển sang kịch bản live demo. Nhóm sẽ reset và seed SQLite, đăng nhập vào EShop, thêm sản phẩm vào giỏ hàng và hoàn tất checkout.

Kết quả mong đợi gồm bốn điểm: bảng `orders` có thêm đơn hàng, `user_id` thuộc về user đang đăng nhập, `total_amount` khớp tổng tiền giỏ hàng và `status` mặc định bằng `pending`.

Sau thao tác trên giao diện, nhóm sẽ chạy DbUnit với XML dataset và Database Rider với YAML dataset. Hai test dùng cùng một nghiệp vụ nhưng minh họa hai cách tổ chức database test khác nhau.

## Slide 11 - Live demo Tonic.ai

Sau phần checkout, nhóm demo Tonic.ai. Nhóm upload `users.csv` vào file group `users`, cấu hình generator cho các cột nhạy cảm, chạy data generation và tải CSV đã được mask.

Nhóm sẽ chỉ ra rằng dữ liệu mới vẫn giữ cấu trúc cần thiết nhưng không còn giữ nguyên thông tin nhạy cảm. Nếu email hoặc password đã bị mask, tài khoản demo ban đầu có thể không đăng nhập được. Vì vậy, dữ liệu generated cần được kiểm tra trước khi import vào database hoặc dùng cho browser test.

## Slide 12 - Giới hạn của proof of concept

Nhóm cũng muốn nêu rõ giới hạn của bản demo. Code hiện tại còn chứa các thông tin đặc thù của EShop như driver, connection URL, đường dẫn dataset, tên bảng, câu SQL và expected value.

Điều này chứng minh tool chạy được trong EShop, nhưng chưa có nghĩa là có thể copy nguyên code sang mọi project. Khi chuyển sang project khác, chúng ta vẫn phải thay schema, dataset, query và business assertion.

## Slide 13 - Khả năng tái sử dụng

Với proof of concept hiện tại, nhóm ước lượng có thể tái sử dụng trực tiếp khoảng 30 đến 40 phần trăm code. Đây là ước lượng kỹ thuật dựa trên cấu trúc code, không phải benchmark đo trên nhiều project.

Sau khi refactor, phần test harness có thể tái sử dụng khoảng 70 đến 80 phần trăm. Những phần có thể giữ lại gồm dependency, connection factory, test lifecycle, dataset loader, cleanup và reporting. Những phần phải thay vẫn là schema, dataset, truy vấn và assertion nghiệp vụ.

## Slide 14 - Refactor để dùng lại cho project khác

Giải pháp là tách hạ tầng test khỏi logic riêng của EShop. Connection factory nên đọc driver và URL từ configuration hoặc environment variable. Schema nên nằm trong `schema.sql`, dataset nằm trong thư mục resources, còn phần hỗ trợ chung như cleanup và lifecycle nằm trong `DatabaseTestSupport`.

Khi chuyển sang một relational project khác, nhóm chỉ cần thay driver, connection, schema, dataset và các assertion nghiệp vụ. Với NoSQL, DbUnit và Database Rider không thể giữ nguyên vì cần native driver và framework phù hợp, nhưng vòng đời arrange, act, assert và cleanup vẫn có thể tái sử dụng.

## Slide 15 - Điểm cần nhớ

Tóm lại, DbUnit giúp kiểm soát trạng thái database quan hệ và xác minh kết quả ổn định. Database Rider giữ nguyên ý tưởng đó nhưng giúp dataset và test dễ đọc, dễ bảo trì hơn. Tonic.ai giúp tạo dữ liệu kiểm thử an toàn, bao gồm relational database và một số hệ NoSQL được hỗ trợ.

Thông điệp cuối cùng của nhóm là: không có một công cụ hoặc một đoạn test duy nhất phù hợp cho mọi project. Chúng ta tái sử dụng hạ tầng và quy trình, nhưng vẫn phải thiết kế lại schema và business assertion cho từng hệ thống.

Bây giờ nhóm sẽ chuyển sang live demo trên EShop để minh họa trực tiếp kịch bản checkout, hai cách kiểm thử bằng DbUnit và Database Rider, quy trình mask bằng Tonic.ai và phần refactor test harness.

## Slide 16 - Ngoài chạy tool, còn phải kiểm tra gì?

Sau khi đã nói về công cụ, nhóm muốn mở rộng phạm vi của database testing. Chạy tool chỉ là một phần của hoạt động kiểm thử; điều quan trọng hơn là xác định đúng dữ liệu và điều kiện cần được chứng minh.

Ở lớp dữ liệu và nghiệp vụ, chúng ta kiểm tra giá trị lưu có đúng không, dữ liệu giữa các bảng có mapping đúng không, user có đúng role không, trạng thái và phép tính business có đúng không.

Ở lớp toàn vẹn, chúng ta kiểm tra primary key, unique, foreign key, `NOT NULL`, `CHECK` và trigger. Ở lớp giao dịch, chúng ta kiểm tra ACID, rollback, isolation, duplicate request và các trường hợp deadlock hoặc lỗi giữa chừng.

Nói ngắn gọn, DbUnit và Database Rider giúp thực thi và so sánh test state, nhưng tester vẫn phải quyết định state nào là đúng và rule nào cần kiểm tra.

## Slide 17 - Thiết kế case ở nhiều lớp

Database testing cần nhiều nhóm test chứ không chỉ một test dataset.

Với input, chúng ta kiểm tra giá trị nhỏ nhất, lớn nhất, vượt biên, `NULL`, blank, Unicode, ký tự đặc biệt và kiểu dữ liệu. Với ngày tháng, cần chú ý ngày không hợp lệ, năm nhuận, tháng 0 hoặc 13 và những format mà hệ thống cho phép.

Với integrity và mapping, chúng ta kiểm tra unique, foreign key, trigger, import, migration và rollback. Với performance, chúng ta kiểm tra search, join nhiều bảng, pagination, batch insert/update, query timeout và query plan.

Các case này có thể được thực hiện ở nhiều lớp: GUI hoặc API để kiểm tra flow người dùng, SQL để kiểm tra database state, và automation để chạy lặp lại với dữ liệu có kiểm soát. Một test chỉ thực sự có ý nghĩa khi expected result, dữ liệu và môi trường được xác định rõ.

## Slide 18 - SQL, giao dịch logic và truy cập đồng thời

Ngoài kiểm tra dữ liệu đầu vào và constraint, nhóm cần thiết kế những trường hợp có thể làm SQL hoặc transaction thất bại.

Đầu tiên là SQL test cases. Tester dựa trên hiểu biết về SQL để tạo các trường hợp gây lỗi có chủ đích, kiểm tra câu lệnh SQL tương tác với component của ứng dụng, đồng thời dùng database client hoặc công cụ dữ liệu để chạy và quan sát kết quả của query hay stored procedure. Mục tiêu không chỉ là xem câu lệnh có chạy được hay không, mà còn phải xác nhận dữ liệu sau đó có đúng hay không.

Tiếp theo là logical transaction. Một transaction có thể gồm nhiều thao tác update liên quan. Chúng ta cần kiểm tra mọi nhánh thành công và thất bại. Nếu một bước không thành công, các bước còn lại cũng phải được rollback để database không rơi vào trạng thái chỉ cập nhật một phần.

Cuối cùng là concurrency. Database có thể xử lý nhiều transaction cùng lúc, nên test cần kiểm tra lock, isolation, race condition và deadlock. Hai request chạy riêng có thể đều pass, nhưng khi chạy đồng thời lại tranh chấp cùng dữ liệu. Vì vậy concurrency test là phần quan trọng để bảo vệ tính toàn vẹn dữ liệu trong điều kiện thực tế.

## Slide 19 - AI có thể hỗ trợ ở đâu?

AI có thể hỗ trợ database testing ở nhiều bước, nhưng không thay thế trách nhiệm của tester.

Ở bước chuẩn bị, AI có thể đọc requirement hoặc schema để gợi ý test case boundary, dataset mẫu, câu SQL seed và dữ liệu synthetic. Ở bước phân tích, AI có thể hỗ trợ so sánh expected với actual, phát hiện anomaly, đọc log lỗi và gợi ý những query có khả năng chậm.

Ở bước triển khai, AI có thể tạo skeleton cho DbUnit hoặc Database Rider, sinh checklist về ACID và constraint, hoặc đề xuất kịch bản performance. Sau đó tester phải review lại query, expected result, quan hệ foreign key và chạy xác nhận trên database test.

Nhóm đặt ra ba nguyên tắc: không đưa production PII vào prompt, không chạy SQL do AI sinh trực tiếp trên production, và không xem câu trả lời của AI là bằng chứng test. AI là trợ lý tăng tốc; requirement, database state và evidence chạy thật mới là cơ sở kết luận.

## Slide 20 - Hoạt động cuối seminar

Trước khi kết thúc, nhóm xin mời thầy và các bạn dùng điện thoại quét mã QR trên slide.

Mọi người hãy thực hiện theo những gì nhóm vừa demo về database testing, DbUnit, Database Rider, Tonic.ai và các nội dung mở rộng như constraint, business rule và AI-assisted testing.

Phần hoạt động này giúp kiểm tra lại mức độ hiểu bài sau seminar. Cảm ơn thầy và các bạn đã theo dõi và tham gia cùng nhóm.
