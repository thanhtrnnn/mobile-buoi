# Thực hành 3 — Quản lý nhân sự bằng SQLite

## Đề bài (nguyên văn)

> Nhân sự bao gồm các thông tin: Mã nhân sự, Họ và tên, Năm sinh, Giới tính, Quê quán, Trình độ, Ảnh, Mức lương, Phòng ban (Hành chính, Phần mềm, Kỹ thuật, Dự án).
> Viết ứng dụng quản lý nhân sự hiển thị danh sách các nhân sự và với các chức năng thêm, sửa, xóa, tìm kiếm, sắp xếp (theo mức lương) bằng SQLite.
> Các giao diện đều phải có thông tin cá nhân (MSSV-Họ tên).
> Chụp hình các giao diện chạy các chức năng, tổng hợp vào 1 file word/pdf và nộp.

## Hiểu lại yêu cầu

Một app Android (Java) lưu nhân sự trong SQLite, làm đủ CRUD + tìm kiếm + sắp xếp. Bố cục màn hình theo sơ đồ thầy vẽ trên bảng (3 activity, đánh số I–V):

```
Activity_main                 Activity_add               Activity_detail
┌───────────────────┐         ┌──────────────────┐       ┌──────────────────┐
│ MSSV              │         │ MSSV - Họ tên    │       │ MSSV - Họ tên    │
│ Họ tên            │         │                  │       │                  │
│ [ô tìm] [Search]  │ (V)     │ Mã NS  ____      │       │ Mã NS  ____      │
│ [Sắp xếp lương]   │         │ Họ tên ____      │       │ Họ tên ____      │
│ ─ nhân sự 1 ───── │ (I)     │ ...              │ (II)  │ ...              │ (III)
│ ─ nhân sự 2 ───── │         │ [Submit] [Reset] │       │ [Update]         │
│ ...               │         └──────────────────┘       └──────────────────┘
│ [ADD]             │
└───────────────────┘
  long click 1 dòng → hộp thoại Delete (IV)
```

| # | Chức năng | Cách làm trong app |
|---|-----------|--------------------|
| I | Hiển thị danh sách | `ListView` + `ArrayAdapter` tự viết; mỗi dòng có ảnh, mã, họ tên, phòng ban, mức lương. Dữ liệu đọc từ SQLite mỗi khi quay lại màn hình chính (refresh). |
| II | Thêm | Nút **ADD** mở `Activity_add`: nhập đủ 9 trường, **Submit** ghi vào DB (`insert` với `ContentValues`) rồi quay về danh sách; **Reset** xoá trắng form. |
| III | Sửa | Bấm 1 dòng mở `Activity_detail` đã điền sẵn dữ liệu; sửa xong bấm **Update** (`update ... WHERE ma = ?`) rồi quay về danh sách. Mã nhân sự là khoá chính nên không cho sửa. |
| IV | Xoá | Nhấn giữ (long click) 1 dòng → hộp thoại xác nhận **Delete** (`delete ... WHERE ma = ?`), danh sách cập nhật ngay. |
| V | Tìm kiếm | Ô tìm + nút **Search**: lọc theo mã hoặc họ tên chứa từ khoá (`LIKE`). Để trống rồi Search là hiện lại tất cả. |
| — | Sắp xếp | Nút sắp xếp theo mức lương, bấm lần lượt đổi tăng dần ↔ giảm dần (`ORDER BY luong`). Áp dụng cùng lúc với từ khoá tìm kiếm đang có. |

### Dữ liệu

Bảng `nhansu` trong file `quanlynhansu.db`, tạo bằng `SQLiteOpenHelper`:

| Cột | Kiểu | Nhập bằng |
|-----|------|-----------|
| `ma` | TEXT, khoá chính | EditText |
| `hoten` | TEXT | EditText |
| `ngaysinh` | TEXT `yyyy-MM-dd` | Bấm ô để mở `DatePickerDialog` (không cho chọn ngày tương lai), hiển thị `dd/MM/yyyy` |
| `gioitinh` | TEXT | RadioButton Nam / Nữ |
| `quequan` | TEXT | EditText |
| `trinhdo` | TEXT | Spinner: Trung cấp, Cao đẳng, Đại học, Thạc sĩ, Tiến sĩ |
| `anh` | BLOB | Nút chọn ảnh từ máy (ảnh thu nhỏ, nén JPEG rồi lưu thẳng vào DB) |
| `luong` | REAL | EditText số |
| `phongban` | TEXT | Spinner: Hành chính, Phần mềm, Kỹ thuật, Dự án |

Đề và sơ đồ trên bảng ghi năm sinh (YoB); app lưu đủ **ngày sinh** (DoB) theo yêu cầu chỉnh lại.

Kiểm tra khi Submit/Update: không bỏ trống trường chữ và ngày sinh, lương là số; mã trùng thì báo lỗi. Lần đầu tạo DB có sẵn vài nhân sự mẫu để danh sách, tìm kiếm, sắp xếp có dữ liệu xem ngay.

### Yêu cầu chung

- Cả 3 màn hình đều có MSSV và họ tên ở trên cùng: Activity_main ghi trên 2 dòng (**B23DCAT280** / **Trần Xuân Thành**) như sơ đồ, hai màn còn lại ghi 1 dòng **B23DCAT280 - Trần Xuân Thành**.
- Giao diện giữ đơn giản, đúng khung như sơ đồ trên bảng.

## Sản phẩm nộp

- Project Android trong thư mục này (`thuchanh3/`), package `ptit.cnpm1.tranxuanthanh.quanlynhansu`.
- Ảnh chụp từng chức năng khi chạy trên máy ảo (`screenshots/`).
- 1 file Word và 1 file PDF tổng hợp đề bài, ảnh chụp các chức năng và mã nguồn chính, đặt tên `B23DCAT280-Trần Xuân Thành`.
