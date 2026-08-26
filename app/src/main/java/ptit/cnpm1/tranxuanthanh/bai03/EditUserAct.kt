package ptit.cnpm1.tranxuanthanh.bai03

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class EditUserAct : Activity(), View.OnClickListener {

    private lateinit var formUser: UserFormView
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    /** Người dùng đang được sửa, giữ nguyên bản gốc nhận từ ViewUserAct. */
    private lateinit var user: User

    /** Những username đã có sẵn, nhận từ ViewUserAct để chặn trùng. */
    private var usernames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edituser)

        formUser = findViewById(R.id.formUser)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        user = getUserExtra() ?: User()
        usernames = intent.getStringArrayListExtra("usernames") ?: ArrayList()
        // Điền sẵn thông tin cũ vào 3 ô của component
        formUser.user = user

        btnSave.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                if (!formUser.isFilled()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return
                }

                val edited = formUser.user
                // Giữ nguyên username cũ thì hợp lệ; chỉ chặn khi đổi sang tên của người khác
                if (!edited.username.equals(user.username, ignoreCase = true) &&
                    usernames.any { it.equals(edited.username, ignoreCase = true) }
                ) {
                    Toast.makeText(this, "Username \"" + edited.username + "\" đã tồn tại", Toast.LENGTH_SHORT).show()
                    return
                }

                user = edited
                val result = Intent().putExtra("user", user)
                setResult(RESULT_OK, result)
                finish()
            }
            R.id.btnCancel -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }

    private fun getUserExtra(): User? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("user", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("user") as? User
        }
    }
}
