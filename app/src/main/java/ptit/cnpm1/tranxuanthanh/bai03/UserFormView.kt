package ptit.cnpm1.tranxuanthanh.bai03

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * Component gộp 3 ô nhập Username / Password / Fullname thành một View duy nhất.
 *
 * Màn Thêm và màn Sửa người dùng có phần thân giống hệt nhau, nên thay vì lặp lại
 * 6 View trong 2 file layout, cả hai chỉ cần đặt một thẻ UserFormView.
 * Activity không còn phải tự findViewById từng ô rồi ghép chuỗi thành User nữa,
 * chỉ đọc/ghi qua thuộc tính [user].
 */
class UserFormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val txtUN: EditText
    private val txtPW: EditText
    private val txtName: EditText

    init {
        LayoutInflater.from(context).inflate(R.layout.view_user_form, this, true)
        txtUN = findViewById(R.id.txtUN)
        txtPW = findViewById(R.id.txtPW)
        txtName = findViewById(R.id.txtName)
    }

    /** Đọc ra User từ những gì đang gõ trong 3 ô, hoặc điền sẵn 3 ô từ một User. */
    var user: User
        get() = User(
            username = txtUN.text.toString().trim(),
            password = txtPW.text.toString().trim(),
            fullname = txtName.text.toString().trim()
        )
        set(value) {
            txtUN.setText(value.username)
            txtPW.setText(value.password)
            txtName.setText(value.fullname)
        }

    /** true khi cả 3 ô đều đã được nhập. */
    fun isFilled(): Boolean {
        val u = user
        return u.username.isNotEmpty() && u.password.isNotEmpty() && u.fullname.isNotEmpty()
    }
}
