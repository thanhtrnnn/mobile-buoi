# Bài 08 – Ví điện tử, phần 3: xem thống kê

Bám theo `slide/b08 - Lap trinh mobile - CS vi dien tu p3.pdf`, đặc biệt slide 23–29. Bài 08 kế thừa chức năng quản lý giao dịch và lịch tuần/tháng của bài 07; package, manifest và tài nguyên riêng giống các bài trước.

## Chạy ứng dụng

```bash
./build_and_run.sh bai08
```

Hoặc build bằng Gradle:

```bash
./gradlew -Plesson=bai08 :app:assembleDebug
```

Bài 08 là lựa chọn mặc định của cả Gradle và script. Có thể truyền `bai03` đến `bai07` để chạy các bài trước. APK của script nằm ở `build/bai08/app-signed.apk`.

## Các luồng theo slide

- **Menu ở trang chủ:** nút ☰ mở hai lựa chọn “Thống kê theo tỉ lệ” và “Thống kê theo quá trình”.
- **Theo tỉ lệ:** Spinner đầu chọn tháng hoặc năm; Spinner sau chọn kỳ cụ thể. Danh sách kỳ gồm các kỳ có giao dịch, kỳ hiện tại và kỳ đang xem. Công tắc trái là Chi, phải là Thu. Biểu đồ vành khuyên có màu đối chiếu với chấm màu từng mục; các dòng hiển thị số tiền và phần trăm, giảm dần theo tổng tiền.
- **Chi tiết mục:** bấm một mục để xem các giao dịch trong đúng kỳ đã chọn, tăng dần theo ngày rồi theo mã giao dịch. Dòng hiển thị logo, ngày và tiền. Bấm giao dịch để mở màn sửa/xóa có sẵn.
- **Theo quá trình:** đủ 12 tháng tính đến tháng hiện tại, kể cả tháng không có giao dịch. Cột chồng có thu màu xanh và chi màu đỏ, dùng chung thang đo. Vuốt ngang xem các tháng; bấm tháng để mở tỉ lệ tháng đó.
- **Cập nhật:** quay lại sau khi sửa/xóa giao dịch sẽ nạp lại danh sách, tổng tiền và biểu đồ. Kỳ trống hiển thị thông báo và tổng bằng 0.

## Các lớp chính

| Lớp | Vai trò |
| --- | --- |
| `StatPeriod` | Biểu diễn tháng/năm, khoảng đầu kỳ đến đầu kỳ tiếp theo và truyền bộ lọc qua Intent |
| `TransactionDAO` | Tổng theo mục, danh sách chi tiết và tổng 12 tháng |
| `RatioStatAct` | Bộ chọn kỳ, công tắc Thu/Chi, biểu đồ và danh sách mục |
| `CategoryTransactionsAct` | Giao dịch của mục và điều hướng sang sửa |
| `ProcessStatAct` | Thống kê 12 tháng và điều hướng sang tỉ lệ |
| `PieChartView` | Vẽ biểu đồ vành khuyên bằng Canvas |
| `ProcessChartView` | Vẽ cột chồng và phân biệt chạm với vuốt |
| `StatRow` | Logo, tên mục, tổng tiền và phần trăm |

## Cách tính

Cấu trúc CSDL giữ nguyên: `tblType`, `tblCategory`, `tblTransaction`. Ngày vẫn lưu `dd/MM/yyyy` như bài 07. Truy vấn thống kê đổi ngày sang `yyyy-MM-dd` bằng `substr` rồi lọc khoảng `[đầu kỳ, đầu kỳ tiếp theo)`, tránh so sánh sai khi qua tháng/năm.

Mỗi giao dịch chỉ được tính vào mục thực tế của nó, không cộng trùng vào mục cha. Mục không có khoản tiền dương trong kỳ không tạo lát biểu đồ. Tổng từng mục chia cho tổng các mục cùng kiểu thu/chi để ra phần trăm. Các lớp `CategoryStat` và `TimeStat` giữ vai trò như sơ đồ slide 25.

Toàn bộ comment Kotlin trong `bai08/` viết bằng tiếng Việt. Các phần kế thừa nằm cùng package để bài 08 chạy độc lập, không phụ thuộc mã bài 07.

## Kiểm tra đã thực hiện

- Build APK bằng script và bằng Gradle; `lintDebug` không có lỗi.
- Dữ liệu thử có mục cha/con, khoản thu/chi và giao dịch ở tháng/năm khác nhau. Kiểm tra tổng tháng, tổng năm, phần trăm và thứ tự mục; mục con không cộng trùng vào cha.
- Kiểm tra chi tiết chỉ chứa giao dịch đúng kỳ và xếp ngày tăng dần; sửa số tiền cập nhật tổng ở màn chi tiết và màn tỉ lệ.
- Kiểm tra năm trước, tháng trống và ngày nhuận 29/02; 12 tháng gồm cả tháng trống và không lấy tháng tương lai.
- Dữ liệu kiểm tra được khôi phục sau khi chạy, không thêm dữ liệu mẫu vào ứng dụng.
