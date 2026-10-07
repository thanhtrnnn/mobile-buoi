# Cheat sheet DAO bai07

## DAO là gì?

**DAO (Data Access Object)** là lớp phụ trách đọc và ghi dữ liệu SQLite. DAO trả về các đối tượng Kotlin hoặc giá trị đơn giản; Activity, Fragment hoặc Adapter dùng kết quả đó để cập nhật giao diện.

```text
Activity / Fragment / Adapter  ->  CategoryDAO hoặc TransactionDAO  ->  DBHelper  ->  SQLite
         hiển thị giao diện    <-       model và số liệu thống kê   <-
```

## Vai trò của từng file

| File | Vai trò |
| --- | --- |
| `DBHelper.kt` | Tạo database và các bảng, thêm dữ liệu mẫu ban đầu, xử lý nâng cấp database. |
| `CategoryDAO.kt` | Đọc loại thu/chi và danh mục; kiểm tra tên trùng; thêm, sửa, xóa danh mục. Khi xóa danh mục, DAO cũng xóa các danh mục con và giao dịch thuộc chúng. |
| `TransactionDAO.kt` | Đọc, thêm, sửa, xóa giao dịch; tính `TimeStat` và `CategoryStat`; chuyển đổi ngày giữa `Date` và chuỗi `dd/MM/yyyy`. |
| `HomeAct.kt`, `MonthAct.kt` | Gọi DAO lấy giao dịch/tổng tiền rồi cập nhật lịch và các nhãn trên màn hình. |
| `ShowTransFrag.kt`, `CategoryAdapter.kt` | Hiển thị từng dòng giao dịch hoặc danh mục. |

`TransactionDAO` dùng `CategoryDAO` khi đọc giao dịch để mỗi `Transaction` có cả đối tượng `Category` tương ứng.

`TimeStat` giữ tổng tiền thu và chi theo một khoảng thời gian. `CategoryStat` mở rộng `Category` bằng trường `total`. Lịch dùng `TimeStat`; `TransactionDAO.getCategoryStats(date)` trả về tổng tiền của từng danh mục trong ngày đó.

## Lấy ngày hiện tại và truyền ngày đã chọn

Trong `HomeAct`, `selectedDate` mặc định được khởi tạo bằng `Date()`, tức ngày giờ hiện tại của thiết bị. Nếu Activity nhận ngày từ màn hình khác, ngày trong Intent sẽ được dùng thay thế. Vì vậy, ngày ban đầu thường là hôm nay, còn sau khi người dùng chọn một ngày trên lịch thì `selectedDate` là ngày đã chọn.

Khi mở màn hình thêm giao dịch, `HomeAct` truyền ngày đang chọn dưới dạng mili giây kể từ epoch:

```kotlin
Intent(this, AddAct::class.java)
    .putExtra(AddAct.EXTRA_INITIAL_DATE, selectedDate.time)
```

`AddAct` nhận ngày đó, hoặc dùng ngày hiện tại nếu không có extra, rồi đưa ngày vào ô nhập theo định dạng `dd/MM/yyyy`:

```kotlin
val initialDate = Date(intent.getLongExtra(EXTRA_INITIAL_DATE, Date().time))
txtDate.setText(TransactionDAO.toText(initialDate))
```

Khi người dùng bấm vào ô ngày, DatePicker mở tại ngày đang có trong ô. Chọn ngày mới sẽ cập nhật lại ô `txtDate`. Khi lưu, Activity đổi nội dung ô thành `Date` rồi gửi giao dịch cho DAO:

```kotlin
date = TransactionDAO.toDate(txtDate.text.toString())
```

`TransactionDAO.toText()` và `toDate()` cùng dùng định dạng `dd/MM/yyyy`. DAO lưu ngày theo chuỗi này trong database. Màn hình sửa giao dịch cũng đưa ngày có sẵn của giao dịch vào ô ngày.

## Lịch tháng: ngày thuộc tháng và ngày thuộc tháng liền kề

`WalletCalendar.monthDates(selectedDate)` tạo đủ các ô từ thứ Hai đến Chủ nhật để lịch có các hàng trọn vẹn. Vì vậy, đầu hoặc cuối lịch có thể chứa ngày của tháng trước hoặc tháng sau.

Mỗi ô được kiểm tra bằng:

```kotlin
val inMonth = WalletCalendar.isSameMonth(date, selectedDate)
```

Điều kiện này so sánh cả **tháng và năm**, tránh coi cùng số tháng ở hai năm khác nhau là một tháng. `inMonth` quyết định kiểu hiển thị và thao tác:

| Ngày trong ô | Hiển thị |
| --- | --- |
| Thuộc tháng đang xem (`inMonth == true`) | Số ngày màu đen, nền bình thường; tổng thu màu xanh và tổng chi màu đỏ. Bấm vào ngày sẽ mở chi tiết ngày đó. |
| Thuộc tháng trước/sau (`inMonth == false`) | Số ngày và số tiền màu xám, nền nhạt để phân biệt. Bấm vào ô không mở chi tiết ngày. |
| Ngày đang được chọn (`selected == true`) | Có nền xanh nhạt để đánh dấu ngày đã chọn. |

Ví dụ phần kiểm tra trong `MonthAct`:

```kotlin
val inMonth = WalletCalendar.isSameMonth(date, selectedDate)
val selected = WalletCalendar.isSameDay(date, selectedDate)
val textColor = if (inMonth) Color.BLACK else Color.LTGRAY
background = cellBackground(selected, muted = !inMonth)
```

Lưu ý: điều kiện `inMonth` **không đổi định dạng chuỗi ngày**. Nó chỉ đổi màu/nền và quyết định có mở chi tiết khi bấm hay không. Chuỗi dùng trong ô nhập và database vẫn là `dd/MM/yyyy`; ô lịch chỉ hiện số ngày, còn tiêu đề tháng có dạng `Tháng M, YYYY`.

## Ví dụ đọc dữ liệu

```kotlin
val categories = categoryDAO.getCategories(idType)
val transactions = transactionDAO.getTransactions(selectedDate)
```

Trong DAO, câu truy vấn trả về `Cursor`. DAO đọc các cột, chuyển mỗi dòng thành model, đóng `Cursor`, rồi trả model cho nơi gọi:

```text
SELECT ... -> Cursor -> Category / Transaction -> trả về Activity
```

Activity cập nhật giao diện từ kết quả DAO:

```kotlin
lblTotalChi.text = "Tổng chi: ${Money.format(totals.totalOut)}"
```

## Ví dụ ghi dữ liệu

Activity lấy dữ liệu từ form để tạo model rồi gọi DAO:

```kotlin
val transaction = Transaction(
    date = TransactionDAO.toDate(txtDate.text.toString()),
    amount = amount,
    note = txtNote.text.toString(),
    category = category
)

if (transactionDAO.addTransaction(transaction)) finish()
```

DAO đổi model thành `ContentValues` rồi gọi `insert`, `update` hoặc `delete`. DAO trả về `true`/`false`; Activity quyết định hiện thông báo, chuyển màn hình hoặc tải lại giao diện.

## Ghi nhớ

- Đặt SQL, đọc `Cursor`, chuyển dòng database thành model và lưu dữ liệu trong DAO.
- Đặt `findViewById`, `setText`, xử lý nút bấm, Toast và chuyển màn hình trong Activity/Fragment.
- Truyền model hoặc giá trị đơn giản qua lại; không trả `Cursor` cho giao diện.
- Dùng dấu `?` và tham số `rawQuery` cho giá trị đầu vào.
- Đóng mọi `Cursor` sau khi đọc.
- `CategoryDAO.getCategories()` trả cây danh mục đã làm phẳng theo thứ tự hiển thị; từng mục vẫn có `parent` để biết quan hệ cha-con.
- DAO làm CRUD: tạo (Create), đọc (Read), sửa (Update), xóa (Delete) — không chỉ đọc dữ liệu.
