# Luồng dữ liệu giữa bốn màn hình — bai03

> Mở bằng VS Code (Ctrl/Cmd + Shift + V) hoặc xem trên GitHub để thấy biểu đồ.

## 1 · Mở màn con, nhận kết quả về

Nét liền = đi ra. Nét đứt = trả về. Chỉ `UserhomeAct` sửa `listUser`.

```mermaid
graph LR
    UH["UserhomeAct<br/>giữ listUser"]
    ADD["AddUserAct"]
    VIEW["ViewUserAct"]
    EDIT["EditUserAct"]

    UH -->|"REQ_ADD<br/>+ usernames"| ADD
    ADD -.->|"RESULT_OK + user"| UH
    UH -->|"REQ_VIEW<br/>+ user, position, usernames"| VIEW
    VIEW -.->|"RESULT_OK<br/>+ action, position"| UH
    VIEW -->|"REQ_EDIT<br/>+ user, usernames"| EDIT
    EDIT -.->|"RESULT_OK + user"| VIEW

    classDef owner stroke-width:2px
    class UH owner
```

`EditUserAct` không trả thẳng về `UserhomeAct` — nó trả cho `ViewUserAct`, rồi màn này mới gói
lại thành `action="update"` gửi tiếp.

## 2 · onActivityResult rẽ nhánh thế nào

Hai cửa ải thoát sớm, rồi mới tới chỗ sửa dữ liệu.

```mermaid
flowchart TD
    A["onActivityResult(requestCode, resultCode, data)"] --> B{"resultCode == RESULT_OK ?"}
    B -->|"không — bấm Hủy"| X["return, không đụng listUser"]
    B -->|"có"| C{"requestCode ?"}
    C -->|"REQ_ADD"| D["listUser.add(user)"]
    C -->|"REQ_VIEW"| E{"action ?"}
    E -->|"delete"| F["listUser.removeAt(position)"]
    E -->|"update"| G["gán đè listUser tại position"]
    D --> H["notifyDataSetChanged()"]
    F --> H
    G --> H
```

Sửa `listUser` mà quên `notifyDataSetChanged()` thì màn hình không đổi gì cả, và không có báo lỗi nào.

## 3 · Thứ tự chạy khi màn con đóng

`onActivityResult` chạy **trước** `onResume` — nên lệnh làm mới trong `onResume` là thừa.

```mermaid
sequenceDiagram
    autonumber
    participant U as Người dùng
    participant A as AddUserAct
    participant H as UserhomeAct
    participant L as ListView

    U->>A: bấm nút Thêm
    A->>A: setResult(RESULT_OK, data)
    A->>H: finish()
    H->>H: onActivityResult() — listUser đổi ở đây
    H->>H: onResume() — notifyDataSetChanged() lần 2
    H->>L: vẽ lại danh sách
```

## 4 · Chặn trùng tên: đặt phép kiểm tra ở đâu

Cùng một điều kiện `if`, đặt sớm hay muộn khác hẳn nhau.

```mermaid
flowchart LR
    subgraph NOW["Đang dùng — kiểm tra trong màn con"]
        direction LR
        A1["AddUserAct<br/>kiểm tra trùng"] -->|"trùng"| A2["Toast<br/>màn hình còn mở, chữ đã gõ còn nguyên"]
        A1 -->|"hợp lệ"| A3["setResult + finish"]
    end
    subgraph OLD["Đã loại — kiểm tra sau khi màn con đóng"]
        direction LR
        B1["AddUserAct<br/>setResult + finish"] --> B2["UserhomeAct<br/>phát hiện trùng"]
        B2 --> B3["gõ lại từ đầu cả 3 ô"]
    end
```

Bẫy ở màn Sửa — giữ nguyên tên của chính mình là hợp lệ:

```kotlin
if (!edited.username.equals(user.username, ignoreCase = true) &&
    usernames.any { it.equals(edited.username, ignoreCase = true) }
) {
    // chỉ chặn khi đổi sang tên của NGƯỜI KHÁC
    Toast.makeText(this, "Username đã tồn tại", Toast.LENGTH_SHORT).show()
    return
}
```

## Ba quy tắc

- **Một chủ sở hữu dữ liệu** — màn con đề nghị bằng cách trả kết quả, không tự sửa `listUser`.
- **requestCode để nhận diện** — một màn mở nhiều loại màn con, đây là thứ duy nhất phân biệt được.
- **Kiểm tra nơi người dùng còn sửa được** — báo lỗi lúc màn hình đã đóng là bắt gõ lại từ đầu.

---

Mô tả đúng code bai03 tại commit `ed01c71`.
