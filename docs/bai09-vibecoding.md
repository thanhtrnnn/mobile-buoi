# Bài 09 – Ví điện tử, phần 4: vibecoding

Bám theo `slide/b09 - Lap trinh mobile - CS vi dien tu p4.pdf`. Slide chia ứng dụng ví điện tử thành ba phần và yêu cầu **dùng công cụ AI sinh mã rồi nộp lại các lệnh prompt** (slide 12 và 23). Mỗi phần là một app riêng, phần sau xây chồng lên phần trước:

| App | Phần | Có gì | Slide |
| --- | --- | --- | --- |
| `bai09p1` | 1 – Quản lí giao dịch | Trang chủ ngày hiện tại, thêm/sửa/xóa giao dịch, mục thu/chi dạng cây, thêm/sửa mục | 4–12 |
| `bai09p2` | 1 + 2 – Xem theo tuần/tháng | Thêm lịch tuần trên trang chủ, lịch tháng, vuốt đổi tuần/tháng | 13–16 |
| `bai09p3` | 1 + 2 + 3 – Xem thống kê | Thêm menu thống kê, thống kê theo tỉ lệ, theo quá trình 12 tháng | 17–23 |

Ba app có package riêng (`ptit.cnpm1.tranxuanthanh.bai09p1`…`p3`) nên cài cùng lúc trên một máy được, mỗi app một CSDL riêng. Mã nguồn dùng chung một bộ lớp: `bai09p2` là `bai09p3` bỏ phần thống kê, `bai09p1` là `bai09p2` bỏ phần lịch.

## Chạy ứng dụng

```bash
./build_and_run.sh bai09p1        # hoặc bai09p2, bai09p3 (mặc định bai09p3)
./gradlew -Plesson=bai09p1 :app:assembleDebug
```

## Những gì đã sửa so với bài 08

| Chỗ | Bài 08 | Bài 09 |
| --- | --- | --- |
| Slide 18 – biểu đồ tỉ lệ | Giữa vành khuyên ghi “N mục” | Ghi phần trăm của mục lớn nhất (vd “36%”) và tên mục, giống ô “65%” trong wireframe |
| Slide 19 – thu/chi 12 tháng | Màn dọc, biểu đồ rộng 1008dp, phải vuốt ngang | Màn nằm ngang (`sensorLandscape`) như wireframe, đủ 12 cột trong một màn |
| Form thêm/sửa giao dịch và mục | Màn ngang trên tablet đẩy nút Thêm/Lưu/Hủy ra ngoài, không cuộn được | Bọc form trong `ScrollView` |
| Trang chủ phần 1 | (không có app riêng) | Chỉ có ngày hiện tại, tổng thu/chi, danh sách và nút + như slide 5 |
| Chú thích trong mã | Số slide theo bộ slide b08 | Đổi sang số slide của b09 |

Các yêu cầu còn lại của phần 3 đã đúng và giữ nguyên: menu ☰ có hai lựa chọn; Spinner thứ nhất (tháng/năm) đổi danh sách của Spinner thứ hai; công tắc Chi/Thu; danh sách mục giảm dần theo tổng tiền; chạm mục mở giao dịch của mục trong kỳ, xếp theo thời gian; chạm giao dịch mở màn sửa; chạm tháng ở biểu đồ 12 tháng mở thống kê tỉ lệ của tháng đó.

## Các lệnh prompt

Gửi lần lượt cho công cụ AI (Claude Code, Copilot, Cursor…), chạy thử xong prompt này mới gửi prompt sau. Prompt chỉ mô tả ứng dụng cần làm gì, để công cụ tự chọn cách tổ chức mã.

## Phần 1 – Quản lí giao dịch (app `bai09p1`)

### P1.1 – Trang chủ

> Làm ứng dụng Android ví điện tử bằng Kotlin, package `ptit.cnpm1.tranxuanthanh.myvibewallet`, giao diện đơn giản như wireframe trong slide, dữ liệu lưu trên máy. Trang chủ hiện ngày hiện tại, tổng thu và tổng chi của ngày, bên dưới là danh sách giao dịch trong ngày, mỗi dòng có logo, tên mục và số tiền. Khoản thu màu xanh, khoản chi màu đỏ. Cuối màn hình có nút “+” để thêm giao dịch; bấm một dòng để sửa giao dịch đó.

### P1.2 – Hiện số tiền

> Số tiền từ 1.000 đến 999.999 hiện đơn vị k, từ 1.000.000 đến 999.999.999 hiện M, từ 1.000.000.000 hiện B, làm tròn 2 chữ số sau dấu phẩy. Ví dụ 1537000 hiện 1,54M.

### P1.3 – Thêm và sửa giao dịch

> Màn thêm giao dịch có: kiểu giao dịch thu/chi là nút bật/tắt, mục thu/chi chọn từ danh sách, số tiền, ghi chú, thời gian chọn bằng lịch; nút Thêm và Hủy. Màn sửa giống vậy nhưng có Lưu, Xóa, Hủy. Đổi kiểu thu/chi thì danh sách mục đổi theo. Các mục xếp theo nhóm cha, con, cháu: lần đầu theo abc của mục cha, về sau mục cha nào được dùng nhiều hơn (tính cả các mục con) thì xếp trước; các mục con của cùng một cha luôn theo abc.

### P1.4 – Thêm và sửa mục thu/chi

> Cạnh ô chọn mục có nút “+” để thêm mục thu/chi mới. Màn thêm mục có: kiểu thu/chi, mục cha (chọn trong cây mục đã có, để trống là thêm mục cha mới), tên mục, ghi chú, logo; nút Thêm và Hủy. Sửa mục làm tương tự, có thêm nút Xóa.

## Phần 2 – Xem theo tuần/tháng (app `bai09p2`)

### P2.1 – Tuần trên trang chủ

> Trang chủ thêm 7 ngày của tuần hiện tại, đang chọn ngày hôm nay. Mỗi ngày ghi thứ, tổng thu (xanh) và tổng chi (đỏ) của ngày đó, số nào bằng 0 thì không hiện. Bấm ngày nào thì danh sách giao dịch bên dưới đổi sang ngày đó. Vuốt sang trái hoặc phải để xem tuần sau hoặc tuần trước.

### P2.2 – Xem theo tháng

> Trang chủ có thêm nút xem theo tháng. Màn tháng hiện đủ các tuần có ngày của tháng, mỗi ngày ghi ngày trong tháng, tổng thu và tổng chi như ô tuần; ngày ngoài tháng làm mờ. Bấm một ngày thì xem các giao dịch của ngày đó giống trang chủ. Vuốt sang trái hoặc phải để xem tháng sau hoặc tháng trước.

## Phần 3 – Xem thống kê (app `bai09p3`)

### P3.1 – Menu thống kê

> Trang chủ thêm menu thống kê có 2 lựa chọn: thống kê theo tỉ lệ và thống kê theo quá trình.

### P3.2 – Thống kê theo tỉ lệ

> Màn thống kê theo tỉ lệ có ô chọn xem theo tháng hay theo năm, ô thứ hai chọn tháng hoặc năm cụ thể (đổi ô thứ nhất thì danh sách ô thứ hai đổi theo), và nút bật/tắt thu hoặc chi. Hiện biểu đồ hình tròn theo tỉ lệ các mục trong thời gian đã chọn, giữa biểu đồ ghi phần trăm của mục lớn nhất. Bên dưới là tổng tiền và danh sách các mục với số tiền, xếp giảm dần theo tiền. Bấm một mục thì hiện các giao dịch của mục đó trong thời gian đã chọn, xếp theo thời gian; bấm một giao dịch thì mở màn sửa giao dịch.

### P3.3 – Thống kê theo quá trình

> Màn thống kê theo quá trình là biểu đồ cột thu/chi của 12 tháng gần nhất, màn hình nằm ngang như wireframe: mỗi tháng một cột, phần thu màu xanh, phần chi màu đỏ, có ghi số tiền và tên tháng. Bấm vào một tháng thì mở thống kê theo tỉ lệ của tháng đó.

## Kiểm tra đã thực hiện

- `./gradlew -Plesson=<app> :app:assembleDebug :app:lintDebug` cho cả `bai09p1`, `bai09p2`, `bai09p3`: build thành công, lint không có lỗi. `bai09p3` có thêm cảnh báo `DiscouragedApi` vì khóa màn 12 tháng ở chiều ngang; giữ lại vì wireframe slide 19 vẽ màn ngang.
- Cài cả ba app cùng lúc trên emulator Pixel Tablet (chế độ `-read-only`, dữ liệu không được giữ lại):
  - `bai09p1`: trang chủ chỉ có ngày, tổng thu/chi, danh sách và “+”; thêm giao dịch 1537000 vào Ăn uống qua form thì trang chủ hiện “1,54M” và “Tổng chi: 1,54M”.
  - `bai09p2`: trang chủ có hàng 7 ngày, không có nút ☰; bấm “Tháng” mở lịch tháng.
  - `bai09p3`, với dữ liệu thử 12 tháng: biểu đồ tỉ lệ tháng 10/2026 ghi 36% cho Nhà cửa (5,1M / 14,04M); biểu đồ 12 tháng hiện đủ 11/2025–10/2026 trong một màn; chạm cột 7/2026 mở tỉ lệ tháng 7 với tổng chi 13,1M khớp nhãn cột; chạm mục Nhà cửa mở đúng một giao dịch 08/07/2026 – 5M.
