# Giải thích code — Thực hành 3: Quản lý nhân sự (SQLite)

## Cách dùng file này

Mỗi vị trí ghi dạng `Tên.java:dòng`.

- **VS Code / GitHub:** bấm link là mở đúng file, đúng dòng.
- **Android Studio:** mở thư mục `thuchanh3/` làm project, bấm **Cmd+Shift+O** (Go to File), dán nguyên chữ của link, ví dụ `DBHelper.java:66`, rồi Enter: nhảy thẳng tới dòng đó. Mở file `.md` này ngay trong Android Studio thì bấm link cũng mở được file, nhưng không nhảy tới dòng.

Viết tắt dùng bên dưới: `J/` = `app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/`, `R/` = `app/src/main/res/`.

---

## 1. Bức tranh tổng

```
                 ┌──────────────────────────── SQLite: quanlynhansu.db, bảng nhansu ────┐
                 │                                DBHelper                              │
                 └──────▲───────────────────────────▲──────────────────────────▲────────┘
          getAll/delete │                insert     │          getByMa/update  │
                        │                           │                          │
 ┌──────────────────────┴───┐  ADD       ┌──────────┴──────────┐  bấm 1 dòng  ┌─┴───────────────────┐
 │ MainActivity             │ ─────────▶ │ AddActivity         │              │ DetailActivity      │
 │  activity_main.xml       │            │  activity_add.xml   │◀─┐       ┌──▶│  activity_detail.xml│
 │  ListView + NhanSuAdapter│ ◀───────── │  Submit / Reset     │  │       │   │  Update             │
 │  Search, Sắp xếp, xoá    │  finish()  └─────────────────────┘  │       │   └─────────────────────┘
 └──────────────────────────┘  → onResume() đọc lại DB        NhanSuForm (form_nhansu.xml) dùng chung
```

| Lớp | Vai trò |
|-----|---------|
| [NhanSu](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSu.java) | Model: 1 dòng của bảng `nhansu` (9 trường). |
| [DBHelper](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java) | `SQLiteOpenHelper`: tạo DB, tạo bảng, CRUD, tìm kiếm, sắp xếp. |
| [MainActivity](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java) | Màn danh sách (I), tìm kiếm (V), sắp xếp, xoá (IV), mở màn thêm/sửa. |
| [NhanSuAdapter](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuAdapter.java) | `ArrayAdapter` đổ từng `NhanSu` vào 1 dòng của `ListView`. |
| [AddActivity](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/AddActivity.java) | Màn thêm (II): Submit, Reset. |
| [DetailActivity](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DetailActivity.java) | Màn sửa (III): điền sẵn, Update. |
| [NhanSuForm](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java) | Gom 9 ô nhập dùng chung cho màn thêm và màn sửa: điền, xoá trắng, chọn ngày, chọn ảnh, kiểm tra. |

Số La Mã (I)–(V) trùng với số thầy đánh trên sơ đồ bảng và cũng được ghi trong comment của code.

---

## 2. Tra nhanh: muốn tìm … thì ở đâu

| Muốn xem | Java | Layout / resource |
|----------|------|-------------------|
| Tạo bảng (`execSQL CREATE TABLE`) | [DBHelper.java:40](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L40) | |
| 5 nhân sự mẫu | [DBHelper.java:52](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L52) | |
| Đổi cấu trúc bảng (tăng version) | [DBHelper.java:16](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L16), [DBHelper.java:60](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L60) | |
| **Hiển thị danh sách (I)** | [MainActivity.java:115](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L115) `loadData()` → [DBHelper.java:66](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L66) `getAll()` | [activity_main.xml:59](app/src/main/res/layout/activity_main.xml#L59), [item_nhansu.xml](app/src/main/res/layout/item_nhansu.xml) |
| Vẽ 1 dòng danh sách | [NhanSuAdapter.java:25](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuAdapter.java#L25) | [item_nhansu.xml:11](app/src/main/res/layout/item_nhansu.xml#L11) |
| **Thêm (II)** | [MainActivity.java:66](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L66) → [AddActivity.java:35](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/AddActivity.java#L35) → [DBHelper.java:102](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L102) | [activity_add.xml:32](app/src/main/res/layout/activity_add.xml#L32) |
| Báo trùng mã | [AddActivity.java:39](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/AddActivity.java#L39) | |
| Reset form | [AddActivity.java:26](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/AddActivity.java#L26) → [NhanSuForm.java:87](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L87) | |
| **Sửa (III)** | [MainActivity.java:69](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L69) → [DetailActivity.java:25](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DetailActivity.java#L25) → [DetailActivity.java:44](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DetailActivity.java#L44) → [DBHelper.java:106](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L106) | [activity_detail.xml:35](app/src/main/res/layout/activity_detail.xml#L35) |
| Khoá ô mã khi sửa | [DetailActivity.java:32](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DetailActivity.java#L32) | |
| **Xoá (IV)** | [MainActivity.java:76](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L76) → [MainActivity.java:102](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L102) → [DBHelper.java:110](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L110) | [strings.xml:22](app/src/main/res/values/strings.xml#L22) (câu hỏi xoá) |
| **Tìm kiếm (V)** | [MainActivity.java:49](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L49) → [MainActivity.java:95](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L95) → [DBHelper.java:70](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L70) (`LIKE`) | [activity_main.xml:35](app/src/main/res/layout/activity_main.xml#L35) |
| **Sắp xếp theo lương** | [MainActivity.java:59](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L59) → [DBHelper.java:74](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L74) (`ORDER BY`) | [activity_main.xml:52](app/src/main/res/layout/activity_main.xml#L52) |
| Danh sách tự cập nhật khi quay về | [MainActivity.java:84](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L84) `onResume()` | |
| Kiểm tra dữ liệu nhập | [NhanSuForm.java:102](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L102) `read()`, [NhanSuForm.java:130](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L130) `loi()` | [strings.xml:50](app/src/main/res/values/strings.xml#L50) (các câu báo lỗi) |
| Chọn ngày sinh | [NhanSuForm.java:139](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L139) | [form_nhansu.xml:65](app/src/main/res/layout/form_nhansu.xml#L65) |
| Chọn ảnh, lưu BLOB | [NhanSuForm.java:65](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L65), [NhanSuForm.java:186](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L186) | [form_nhansu.xml:208](app/src/main/res/layout/form_nhansu.xml#L208) |
| Spinner trình độ / phòng ban | [NhanSuForm.java:58](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L58), [NhanSuForm.java:165](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L165) | [strings.xml:55](app/src/main/res/values/strings.xml#L55), [strings.xml:63](app/src/main/res/values/strings.xml#L63) |
| Dòng MSSV - Họ tên | | [activity_main.xml:13](app/src/main/res/layout/activity_main.xml#L13) (2 dòng), [activity_add.xml:17](app/src/main/res/layout/activity_add.xml#L17), [activity_detail.xml:17](app/src/main/res/layout/activity_detail.xml#L17), [strings.xml:6](app/src/main/res/values/strings.xml#L6) |
| Màu sắc, kiểu nút | | [themes.xml:4](app/src/main/res/values/themes.xml#L4), [bg_button.xml](app/src/main/res/drawable/bg_button.xml) |
| Cỡ chữ điện thoại / tablet | | [values/dimens.xml](app/src/main/res/values/dimens.xml), [values-sw600dp/dimens.xml](app/src/main/res/values-sw600dp/dimens.xml) |
| Khai báo 3 Activity, màn hình mở đầu | | [AndroidManifest.xml:13](app/src/main/AndroidManifest.xml#L13) |

---

## 3. Giải thích từng file Java

### 3.1. `NhanSu.java` — model

[NhanSu.java](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSu.java)

- 9 trường `private final`, tạo một lần qua constructor ([NhanSu.java:18](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSu.java#L18)) rồi chỉ đọc bằng getter. Muốn sửa thì tạo đối tượng mới từ form, không sửa trực tiếp.
- `ngaySinh` lưu dạng `yyyy-MM-dd` ([NhanSu.java:10](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSu.java#L10)). Dạng này so sánh và sắp xếp như chuỗi vẫn đúng thứ tự thời gian. Khi hiển thị thì đổi sang `dd/MM/yyyy` bằng `hienThiNgay()` ([NhanSu.java:42](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSu.java#L42)).
- `anh` là `byte[]` (ảnh JPEG đã nén), `null` nếu chưa có ảnh ([NhanSu.java:14](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSu.java#L14)).
- `getLuongText()` ([NhanSu.java:48](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSu.java#L48)): `15000000` → `"15.000.000 đ"`. `%,.0f` chèn dấu phẩy ngăn cách hàng nghìn, rồi đổi phẩy thành chấm theo kiểu Việt Nam.

### 3.2. `DBHelper.java` — CSDL

[DBHelper.java](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java) kế thừa `SQLiteOpenHelper`, đúng như sơ đồ trên bảng (db → `execSQL`, `query`, `rawQuery`, `insert`, `update`, `delete`).

| Dòng | Nội dung |
|------|----------|
| [DBHelper.java:15](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L15) | Tên file DB `quanlynhansu.db`, version 2. |
| [DBHelper.java:18](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L18) | Tên bảng và tên cột để trong hằng số, viết một lần dùng mọi nơi, tránh gõ sai. |
| [DBHelper.java:30](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L30) | 3 kiểu sắp xếp: không sắp, lương tăng, lương giảm. |
| [DBHelper.java:39](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L39) | `onCreate`: Android tự gọi **một lần** khi file DB chưa tồn tại. Tạo bảng bằng `execSQL` (`ma` là `PRIMARY KEY`, `anh` là `BLOB`), rồi `insert` 5 nhân sự mẫu. |
| [DBHelper.java:60](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L60) | `onUpgrade`: Android gọi khi `DB_VERSION` tăng. Ở đây xoá bảng rồi tạo lại (bản 1 → 2 đổi năm sinh thành ngày sinh), nên dữ liệu cũ sẽ mất. |
| [DBHelper.java:66](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L66) | `getAll(keyword, sort)`: một hàm làm cả **danh sách, tìm kiếm và sắp xếp**. |
| [DBHelper.java:69](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L69) | Có từ khoá thì thêm điều kiện `ma LIKE ? OR hoten LIKE ?` với `%từ khoá%` (chứa từ khoá ở bất kỳ vị trí nào). Dùng `?` chứ không cộng chuỗi để tránh SQL injection. |
| [DBHelper.java:74](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L74) | Chọn `ORDER BY`: `luong ASC` / `luong DESC`. Thêm `ma ASC` để hai người cùng lương luôn xếp cố định. Mặc định xếp theo mã. |
| [DBHelper.java:85](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L85) | `db.query(...)` trả `Cursor`. Duyệt từng dòng, đổi thành `NhanSu` bằng `fromCursor`. `try (...)` tự đóng Cursor. |
| [DBHelper.java:94](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L94) | `getByMa`: lấy 1 nhân sự bằng `rawQuery("SELECT * … WHERE ma = ?")`, không có thì trả `null`. |
| [DBHelper.java:102](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L102) | `insert`: trả `false` nếu SQLite từ chối (ví dụ trùng khoá chính, khi đó `insert` trả về `-1`). |
| [DBHelper.java:106](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L106) | `update … WHERE ma = ?`: ghi đè cả dòng của mã đó. |
| [DBHelper.java:110](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L110) | `delete … WHERE ma = ?`. |
| [DBHelper.java:114](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L114) | `toValues`: `NhanSu` → `ContentValues` (cặp cột = giá trị) cho `insert` và `update`. |
| [DBHelper.java:128](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DBHelper.java#L128) | `fromCursor`: dòng Cursor → `NhanSu`. Dùng `getColumnIndexOrThrow` lấy cột theo tên, không phụ thuộc thứ tự cột. |

### 3.3. `MainActivity.java` — màn danh sách

[MainActivity.java](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java)

- Trạng thái của màn: `list` (dữ liệu đang hiện), `keyword` (từ khoá đang tìm), `sort` (kiểu sắp xếp) — [MainActivity.java:21](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L21).
- `onCreate` ([MainActivity.java:32](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L32)):
  - Gắn `NhanSuAdapter` vào `ListView`. `setEmptyView` hiện chữ "Không có nhân sự nào" khi danh sách rỗng ([MainActivity.java:44](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L44)).
  - **(V)** Nút Search, hoặc phím Search trên bàn phím, gọi `search()` ([MainActivity.java:49](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L49)).
  - **Sắp xếp**: mỗi lần bấm đổi tăng ↔ giảm, đổi chữ trên nút cho biết đang sắp kiểu nào, rồi tải lại ([MainActivity.java:59](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L59)).
  - **(II)** ADD mở `AddActivity` bằng `Intent` ([MainActivity.java:66](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L66)).
  - **(III)** Bấm 1 dòng: gửi **mã** nhân sự qua `putExtra` sang `DetailActivity` ([MainActivity.java:69](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L69)). Chỉ gửi mã, không gửi cả đối tượng (có ảnh khá nặng); màn chi tiết tự đọc lại từ DB.
  - **(IV)** Nhấn giữ 1 dòng thì `confirmDelete`. `return true` báo đã xử lý xong để không kích hoạt thêm click thường ([MainActivity.java:76](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L76)).
- `onResume` ([MainActivity.java:84](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L84)): chạy cả lúc mở màn lần đầu lẫn lúc quay về từ màn thêm/sửa. Vì vậy chỉ cần `loadData()` ở đây là danh sách luôn mới (mũi tên "refresh" trên sơ đồ).
- `search()` ([MainActivity.java:95](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L95)): lưu từ khoá, ẩn bàn phím, tải lại. Ô trống thì `keyword = ""`, hiện tất cả.
- `confirmDelete()` ([MainActivity.java:102](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L102)): `AlertDialog` tiêu đề "Delete". Bấm Xoá thì `db.delete`, hiện Toast, tải lại.
- `loadData()` ([MainActivity.java:115](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/MainActivity.java#L115)): xoá list, đổ kết quả `getAll(keyword, sort)` vào, rồi `notifyDataSetChanged()` để ListView vẽ lại. Vì `keyword` và `sort` được giữ riêng nên đang tìm mà bấm sắp xếp vẫn giữ kết quả tìm.

### 3.4. `NhanSuAdapter.java` — 1 dòng của danh sách

[NhanSuAdapter.java:25](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuAdapter.java#L25) `getView`:

- `convertView == null` mới inflate `item_nhansu.xml`; nếu không thì dùng lại dòng cũ đã cuộn khỏi màn hình (ListView tự tái sử dụng, đỡ tốn bộ nhớ).
- Có ảnh thì `BitmapFactory.decodeByteArray` từ BLOB, không có thì dùng hình mặc định `ic_person` ([NhanSuAdapter.java:32](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuAdapter.java#L32)).
- 3 dòng chữ: `mã - họ tên`, `phòng ban · trình độ`, `Mức lương: …` ([NhanSuAdapter.java:41](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuAdapter.java#L41)).

### 3.5. `NhanSuForm.java` — form dùng chung

Màn thêm và màn sửa có cùng 9 ô. Layout gom vào `form_nhansu.xml` (nhúng bằng `<include>`), còn code gom vào lớp này để không phải viết hai lần.

| Dòng | Nội dung |
|------|----------|
| [NhanSuForm.java:45](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L45) | Constructor: `findViewById` 9 ô, gắn adapter cho 2 Spinner. **Phải tạo trong `onCreate`** vì `registerForActivityResult` chỉ được gọi trước khi Activity chạy. |
| [NhanSuForm.java:62](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L62) | Bấm ô ngày sinh thì mở lịch. |
| [NhanSuForm.java:65](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L65) | Nút Chọn ảnh: `ActivityResultContracts.GetContent` mở trình chọn ảnh của máy (`image/*`). Chọn xong thì gọi `setAnh(uri)`. |
| [NhanSuForm.java:73](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L73) | `fill(ns)`: điền form từ 1 nhân sự (màn sửa). Spinner chọn đúng mục nhờ `selectSpinner` tìm vị trí trong mảng ([NhanSuForm.java:172](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L172)). |
| [NhanSuForm.java:87](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L87) | `reset()`: xoá chữ **và** xoá luôn icon lỗi còn treo, đưa Radio/Spinner/ảnh về mặc định. |
| [NhanSuForm.java:102](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L102) | `read()`: kiểm tra lần lượt mã, họ tên, ngày sinh, quê quán không trống, lương là số > 0. Sai ô nào thì `loi()` báo tại ô đó và trả `null`. Đúng hết thì tạo `NhanSu` (mã viết hoa). |
| [NhanSuForm.java:130](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L130) | `loi()`: `setError` hiện bóng lỗi đỏ. Ô ngày sinh không nhận focus nên không hiện bóng lỗi, vì vậy báo thêm bằng Toast. |
| [NhanSuForm.java:139](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L139) | `chonNgaySinh()`: `DatePickerDialog` mở ở ngày đang chọn, hoặc 01/01 của năm cách đây 25 năm. Chọn xong ghép thành `yyyy-MM-dd` ([NhanSuForm.java:152](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L152)). `setMaxDate` chặn ngày tương lai ([NhanSuForm.java:154](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L154)). Tháng của `DatePicker` đếm từ 0 nên phải `m + 1`. |
| [NhanSuForm.java:165](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L165) | Adapter Spinner dùng `spinner_item.xml` để cỡ chữ to theo tablet. |
| [NhanSuForm.java:186](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L186) | `setAnh(uri)`: đọc kích thước ảnh trước (`inJustDecodeBounds`), giảm độ phân giải ngay lúc đọc (`inSampleSize`, [NhanSuForm.java:195](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L195)), thu về cạnh dài ≤ 256px ([NhanSuForm.java:207](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L207)) rồi nén JPEG 85% ([NhanSuForm.java:210](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/NhanSuForm.java#L210)). Ảnh nhỏ mới cất vừa vào cột BLOB và đọc lên nhanh; ảnh chụp gốc vài MB sẽ làm Cursor bị lỗi. |

### 3.6. `AddActivity.java` — màn thêm

[AddActivity.java](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/AddActivity.java)

- `onCreate` ([AddActivity.java:16](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/AddActivity.java#L16)): tạo `DBHelper` và `NhanSuForm`; Submit gọi `submit()`, Reset gọi `form.reset()`.
- `submit()` ([AddActivity.java:35](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/AddActivity.java#L35)): `form.read()` sai thì dừng. Mã đã có (`getByMa`) hoặc `insert` thất bại thì báo "Mã … đã tồn tại" ở ô mã. Thêm được thì Toast và `finish()` để quay về danh sách; `onResume` bên đó sẽ tự tải lại.

### 3.7. `DetailActivity.java` — màn sửa

[DetailActivity.java](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DetailActivity.java)

- Nhận mã qua `EXTRA_MA` ([DetailActivity.java:12](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DetailActivity.java#L12)), đọc nhân sự từ DB ([DetailActivity.java:25](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DetailActivity.java#L25)). Không thấy thì đóng màn.
- `form.fill(ns)` điền sẵn. Ô mã bị khoá (`setEnabled(false)`) vì mã là khoá chính, dùng trong `WHERE ma = ?` khi update ([DetailActivity.java:30](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DetailActivity.java#L30)).
- `update()` ([DetailActivity.java:44](app/src/main/java/ptit/cnpm1/tranxuanthanh/quanlynhansu/DetailActivity.java#L44)): đọc form, `db.update`, Toast, `finish()`.

---

## 4. Layout và resource

| File | Nội dung |
|------|----------|
| [activity_main.xml](app/src/main/res/layout/activity_main.xml) | MSSV và họ tên trên 2 dòng ([activity_main.xml:13](app/src/main/res/layout/activity_main.xml#L13)), hàng ô tìm + Search ([activity_main.xml:35](app/src/main/res/layout/activity_main.xml#L35)), nút sắp xếp ([activity_main.xml:52](app/src/main/res/layout/activity_main.xml#L52)), `ListView` chiếm phần còn lại nhờ `layout_weight="1"` ([activity_main.xml:59](app/src/main/res/layout/activity_main.xml#L59)), chữ báo danh sách rỗng ([activity_main.xml:66](app/src/main/res/layout/activity_main.xml#L66)), nút ADD ở đáy ([activity_main.xml:75](app/src/main/res/layout/activity_main.xml#L75)). |
| [item_nhansu.xml](app/src/main/res/layout/item_nhansu.xml) | 1 dòng danh sách: ảnh bên trái, 3 dòng chữ bên phải. |
| [form_nhansu.xml](app/src/main/res/layout/form_nhansu.xml) | 9 hàng "nhãn + ô nhập": mã ([form_nhansu.xml:22](app/src/main/res/layout/form_nhansu.xml#L22)), họ tên, ngày sinh (không gõ được, bấm để mở lịch, [form_nhansu.xml:65](app/src/main/res/layout/form_nhansu.xml#L65)), giới tính (RadioGroup, [form_nhansu.xml:90](app/src/main/res/layout/form_nhansu.xml#L90)), quê quán, trình độ (Spinner, [form_nhansu.xml:147](app/src/main/res/layout/form_nhansu.xml#L147)), lương, phòng ban (Spinner, [form_nhansu.xml:188](app/src/main/res/layout/form_nhansu.xml#L188)), ảnh + nút Chọn ảnh ([form_nhansu.xml:208](app/src/main/res/layout/form_nhansu.xml#L208)). |
| [activity_add.xml](app/src/main/res/layout/activity_add.xml) | `ScrollView` → MSSV - Họ tên, `<include form_nhansu>` ([activity_add.xml:32](app/src/main/res/layout/activity_add.xml#L32)), Submit + Reset. |
| [activity_detail.xml](app/src/main/res/layout/activity_detail.xml) | Giống màn thêm nhưng chỉ có nút Update. |
| [spinner_item.xml](app/src/main/res/layout/spinner_item.xml) | 1 dòng của Spinner (cỡ chữ theo dimens). |
| [strings.xml](app/src/main/res/values/strings.xml) | Mọi chữ hiển thị; mảng trình độ ([strings.xml:55](app/src/main/res/values/strings.xml#L55)) và phòng ban ([strings.xml:63](app/src/main/res/values/strings.xml#L63)). |
| [values/dimens.xml](app/src/main/res/values/dimens.xml) và [values-sw600dp/dimens.xml](app/src/main/res/values-sw600dp/dimens.xml) | Kích thước cho điện thoại và cho tablet (màn hình ≥ 600dp). Android tự chọn bộ phù hợp, nên code không cần `if`. |
| [themes.xml](app/src/main/res/values/themes.xml) | Theme trơn đen trắng; đặt cỡ chữ mặc định cho TextView/EditText/RadioButton; style nút vuông `NutVuong` ([themes.xml:33](app/src/main/res/values/themes.xml#L33)) dùng [bg_button.xml](app/src/main/res/drawable/bg_button.xml). |
| [ic_person.xml](app/src/main/res/drawable/ic_person.xml) | Ảnh mặc định khi nhân sự chưa có ảnh. |
| [AndroidManifest.xml](app/src/main/AndroidManifest.xml) | Khai báo 3 Activity; `MainActivity` có `LAUNCHER` nên là màn mở đầu. |
| [app/build.gradle.kts](app/build.gradle.kts) | Package `ptit.cnpm1.tranxuanthanh.quanlynhansu`, `minSdk 24`, `targetSdk 35`. |

---

## 5. Luồng chạy từng chức năng (theo thứ tự gọi hàm)

**Mở app:** `MainActivity.onCreate` → `onResume` → `loadData` → `DBHelper.getAll("", SORT_NONE)`. Lần đầu, `getReadableDatabase` thấy chưa có file DB nên gọi `DBHelper.onCreate` (tạo bảng và 5 dòng mẫu).

**Thêm:** ADD → `AddActivity.onCreate` → nhập → Submit → `submit` → `NhanSuForm.read` → `DBHelper.getByMa` (kiểm tra trùng) → `DBHelper.insert` → `finish` → `MainActivity.onResume` → `loadData`.

**Sửa:** bấm dòng → `Intent` kèm `EXTRA_MA` → `DetailActivity.onCreate` → `getByMa` → `form.fill` → sửa → Update → `read` → `DBHelper.update` → `finish` → `onResume` → `loadData`.

**Xoá:** nhấn giữ dòng → `confirmDelete` → bấm Xoá → `DBHelper.delete` → `loadData`.

**Tìm kiếm:** gõ từ khoá → Search → `search` → `loadData` → `getAll(keyword, sort)` với `LIKE '%từ khoá%'`.

**Sắp xếp:** bấm nút → đổi `sort` → `loadData` → `getAll(keyword, sort)` với `ORDER BY luong ASC/DESC`.

---

## 6. Hay bị hỏi

- **Vì sao dùng `?` trong câu SQL?** Giá trị được truyền riêng, SQLite tự xử lý dấu nháy, nên gõ `' OR 1=1 --` vào ô tìm cũng không phá được câu lệnh (chống SQL injection).
- **`onCreate` của DBHelper chạy khi nào?** Chỉ khi file DB chưa có. Muốn chạy lại thì gỡ app, xoá dữ liệu app, hoặc tăng `DB_VERSION` (khi đó `onUpgrade` chạy).
- **Vì sao gọi `loadData()` trong `onResume` chứ không phải `onCreate`?** Khi màn thêm/sửa `finish()`, MainActivity không tạo lại mà chỉ chạy `onResume`. Đặt ở đây thì danh sách luôn mới sau mỗi lần thêm hoặc sửa.
- **Ảnh lưu ở đâu?** Ngay trong cột `anh` (BLOB) của bảng, sau khi thu nhỏ và nén JPEG. Không lưu đường dẫn, nên ảnh gốc bị xoá khỏi máy thì app vẫn còn ảnh.
- **Vì sao mã không sửa được?** Mã là khoá chính, dùng để tìm dòng cần `update`. Cho sửa mã thì `WHERE ma = ?` sẽ không tìm ra dòng cũ.
- **Cùng một form cho hai màn hình bằng cách nào?** Layout dùng `<include layout="@layout/form_nhansu"/>`, code dùng chung lớp `NhanSuForm`. Màn thêm và màn sửa chỉ khác phần nút bấm.
