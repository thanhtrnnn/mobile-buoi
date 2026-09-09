package ptit.cnpm1.tranxuanthanh.bai03

import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Component gộp 4 ô nhập Username / Password / Fullname / DoB thành một View duy nhất.
 *
 * Màn Thêm và màn Sửa người dùng có phần thân giống hệt nhau, nên thay vì lặp lại
 * 8 View trong 2 file layout, cả hai chỉ cần đặt một thẻ UserFormView.
 * Activity đọc/ghi qua thuộc tính [user].
 */
class UserFormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DATE_PATTERN = "dd/MM/yyyy"
    }

    private val txtUN: EditText
    private val txtPW: EditText
    private val txtName: EditText
    private val txtDob: EditText

    private val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())

    /** Ngày sinh đang chọn. Giữ riêng vì ô DoB chỉ hiển thị, không gõ tay được. */
    private var dob: Date? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_user_form, this, true)
        txtUN = findViewById(R.id.txtUN)
        txtPW = findViewById(R.id.txtPW)
        txtName = findViewById(R.id.txtName)
        txtDob = findViewById(R.id.txtDob)

        txtDob.setOnClickListener { showDatePicker() }
    }

    /** Đọc ra User từ những gì đang nhập, hoặc điền sẵn cả 4 ô từ một User. */
    var user: User
        get() = User(
            username = txtUN.text.toString().trim(),
            password = txtPW.text.toString().trim(),
            fullname = txtName.text.toString().trim(),
            dob = dob
        )
        set(value) {
            txtUN.setText(value.username)
            txtPW.setText(value.password)
            txtName.setText(value.fullname)
            dob = value.dob
            txtDob.setText(value.dob?.let { dateFormat.format(it) } ?: "")
        }

    /** true khi cả 4 ô đều đã có dữ liệu. */
    fun isFilled(): Boolean {
        val u = user
        return u.username.isNotEmpty() && u.password.isNotEmpty() &&
            u.fullname.isNotEmpty() && u.dob != null
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        dob?.let { calendar.time = it }

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val picked = Calendar.getInstance()
                picked.set(year, month, dayOfMonth, 0, 0, 0)
                picked.set(Calendar.MILLISECOND, 0)
                dob = picked.time
                txtDob.setText(dateFormat.format(picked.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
