# Bài 09 – Ví điện tử, phần 4: vibecoding

Bám theo `slide/b09 - Lap trinh mobile - CS vi dien tu p4.pdf`. Nội dung ba phần (quản lí giao dịch, xem theo tuần/tháng, xem thống kê) giống bài 08; điểm mới là **dùng công cụ AI sinh mã và nộp lại các lệnh prompt** (slide 12 và 23). Bài 09 được tạo từ bài 08 với package, manifest và tài nguyên riêng, rồi rà lại phần 3 theo slide 17–22.

## Chạy ứng dụng

```bash
./build_and_run.sh bai09          # mặc định đã là bai09
./gradlew -Plesson=bai09 :app:assembleDebug
```

## Những gì đã sửa so với bài 08 khi rà phần 3

| Slide | Bài 08 | Bài 09 |
| --- | --- | --- |
| 18 – biểu đồ tỉ lệ | Giữa vành khuyên ghi “N mục” | Ghi phần trăm của mục lớn nhất (vd “36%”) và tên mục, giống ô “65%” trong wireframe |
| 19 – thu/chi 12 tháng | Màn dọc, biểu đồ rộng 1008dp, phải vuốt ngang | Màn nằm ngang (`sensorLandscape`) như wireframe, đủ 12 cột trong một màn, chú thích Thu/Chi đưa lên hàng tiêu đề |
| Chú thích trong mã | Số slide theo bộ slide b08 | Đổi sang số slide của b09 |

Các yêu cầu còn lại đã đúng và giữ nguyên: menu ☰ có hai lựa chọn; Spinner thứ nhất (tháng/năm) đổi danh sách của Spinner thứ hai; công tắc Chi/Thu; danh sách mục giảm dần theo tổng tiền; chạm mục mở giao dịch của mục trong kỳ, xếp theo thời gian; chạm giao dịch mở màn sửa; chạm tháng ở biểu đồ 12 tháng mở thống kê tỉ lệ của tháng đó.

## Các lệnh prompt

Mỗi prompt gửi cho công cụ AI (Claude Code, Copilot, Cursor…) mở sẵn project đã chạy được sau phần 2. Gửi lần lượt, chạy thử xong prompt này mới gửi prompt sau.

### Prompt 0 – Ngữ cảnh chung

> Project Android Kotlin này là ứng dụng ví điện tử đã chạy được phần 1 (thêm/sửa/xóa giao dịch, mục thu/chi dạng cây cha–con–cháu) và phần 2 (lịch tuần trên trang chủ, lịch tháng). CSDL SQLite gồm `tblType`, `tblCategory`, `tblTransaction`; ngày lưu dạng `dd/MM/yyyy`. Đọc `HomeAct`, `TransactionDAO`, `CategoryDAO`, `ShowTransFrag`, `Money` trước khi sửa. Quy ước: giao diện đơn giản như wireframe, khoản thu màu xanh `@color/thu`, khoản chi màu đỏ `@color/chi`, số tiền luôn định dạng bằng `Money.format` (k, M, B, làm tròn 2 chữ số). Không thêm thư viện ngoài. Chưa sửa gì, chỉ tóm tắt cấu trúc hiện có.

### Prompt 1 – Menu thống kê ở trang chủ

> Thêm nút ☰ ở góc trên bên trái trang chủ. Bấm vào hiện `PopupMenu` có hai mục “Thống kê theo tỉ lệ” và “Thống kê theo quá trình”. Mục thứ nhất mở `RatioStatAct`, mục thứ hai mở `ProcessStatAct`; cả hai nhận kỳ mặc định là tháng chứa ngày đang chọn ở trang chủ. Ẩn nút này khi trang chủ đang mở ở chế độ xem chi tiết một ngày từ lịch tháng.

### Prompt 2 – Lớp kỳ thống kê và truy vấn DAO

> Tạo `data class StatPeriod(year, month)` với `month = 0` nghĩa là cả năm; có `title` (“Tháng 7/2026”, “Năm 2026”), `start`/`end` dạng `yyyy-MM-dd` là khoảng [đầu kỳ, đầu kỳ sau), hàm ghi/đọc kỳ qua `Intent`, và `lastTwelveMonths()` trả đủ 12 tháng từ cũ đến mới, kết thúc ở tháng hiện tại.
> Trong `TransactionDAO` thêm:
> - `getStatPeriods(byYear, selected)`: các tháng (hoặc năm) có giao dịch, cộng kỳ hiện tại và kỳ đang chọn, xếp mới nhất trước.
> - `getCategoryStats(period, idType)`: tổng tiền theo từng mục của kiểu thu hoặc chi trong kỳ, chỉ lấy mục có tổng > 0, xếp giảm dần theo tổng. Mỗi giao dịch chỉ tính vào đúng mục của nó, không cộng trùng lên mục cha.
> - `getTransactions(period, idCategory)`: giao dịch của một mục trong kỳ, xếp theo ngày tăng dần.
> - `getMonthlyStats(periods)`: tổng thu và tổng chi của từng tháng bằng một truy vấn; tháng không có giao dịch trả về 0.
> Vì ngày lưu `dd/MM/yyyy`, khi so sánh khoảng thời gian phải đổi sang `yyyy-MM-dd` bằng `substr` trong SQL.

### Prompt 3 – Màn thống kê theo tỉ lệ

> Tạo `RatioStatAct` theo wireframe: nút quay lại và tiêu đề; Spinner thứ nhất chọn “Theo tháng”/“Theo năm”; Spinner thứ hai liệt kê kỳ tương ứng và được nạp lại mỗi khi đổi Spinner thứ nhất; công tắc `Switch` chọn kiểu giao dịch Chi/Thu. Bên dưới là biểu đồ vành khuyên tự vẽ bằng `Canvas` (`PieChartView`), mỗi lát là tỉ lệ một mục; giữa vành ghi phần trăm của mục lớn nhất và tên mục đó. Tiếp theo là dòng “Tổng thu/Tổng chi”, một đường kẻ, và `ListView` các mục với chấm màu trùng màu lát, logo, tên, tổng tiền và phần trăm, giảm dần theo tổng tiền. Kỳ trống hiển thị “Chưa có giao dịch trong kỳ này”. Quay lại màn này sau khi sửa giao dịch thì nạp lại số liệu.

### Prompt 4 – Danh sách giao dịch của một mục

> Chạm một dòng ở `RatioStatAct` mở `CategoryTransactionsAct`: tiêu đề là tên mục, dòng kỳ đang xem, dòng tổng tiền (xanh nếu là mục thu, đỏ nếu là mục chi), đường kẻ, rồi danh sách giao dịch của mục trong đúng kỳ đó theo thứ tự thời gian. Mỗi dòng dùng lại `ShowTransFrag` nhưng hiện ngày giao dịch thay cho tên mục. Chạm một giao dịch mở `EditAct` có sẵn; quay lại thì đọc lại danh sách và tổng.

### Prompt 5 – Màn thu/chi 12 tháng gần nhất

> Tạo `ProcessStatAct` tiêu đề “Thu/chi 12 tháng gần nhất”, màn hình nằm ngang như wireframe. `ProcessChartView` tự vẽ 12 cột chồng, mỗi tháng một cột: phần dưới màu xanh là tổng thu, phần trên màu đỏ là tổng chi, mọi cột dùng chung một thang đo. Dưới mỗi cột ghi tổng thu, tổng chi và “tháng/năm”. Cả 12 cột phải nằm vừa một màn hình, không cần cuộn. Chạm vào một cột mở `RatioStatAct` của đúng tháng đó.

### Prompt 6 – Kiểm tra

> Build bằng `./gradlew -Plesson=bai09 :app:assembleDebug` và `lintDebug`, sửa hết lỗi. Sau đó liệt kê cách kiểm tra bằng tay: có giao dịch ở nhiều tháng và năm khác nhau; tổng từng mục khớp với biểu đồ; phần trăm cộng lại 100%; tháng trống hiển thị 0; đổi Spinner thứ nhất thì Spinner thứ hai đổi theo; sửa số tiền một giao dịch rồi quay lại thì tổng và biểu đồ cập nhật.

## Kiểm tra đã thực hiện

- `./gradlew -Plesson=bai09 :app:assembleDebug :app:lintDebug`: build thành công, lint không có lỗi. Lint có thêm cảnh báo `DiscouragedApi` vì khóa màn 12 tháng ở chiều ngang; giữ lại vì wireframe slide 19 vẽ màn ngang.
- Chạy trên emulator Pixel Tablet với dữ liệu thử 12 tháng (chế độ `-read-only`, dữ liệu không được giữ lại): biểu đồ tỉ lệ tháng 10/2026 ghi 36% cho Nhà cửa (5,1M / 14,04M); biểu đồ 12 tháng hiện đủ 11/2025–10/2026 trong một màn; chạm cột 7/2026 mở tỉ lệ tháng 7 với tổng chi 13,1M khớp nhãn cột; chạm mục Nhà cửa mở đúng một giao dịch 08/07/2026 – 5M.
