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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edituser)

        formUser = findViewById(R.id.formUser)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        // Điền sẵn thông tin cũ vào 3 ô của component
        formUser.user = getUserExtra() ?: User()

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

                val result = Intent().putExtra("user", formUser.user)
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
