package ptit.cnpm1.tranxuanthanh.bai03

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class EditUserAct : Activity(), View.OnClickListener {

    private lateinit var txtUN: EditText
    private lateinit var txtPW: EditText
    private lateinit var txtName: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edituser)

        txtUN = findViewById(R.id.txtUN)
        txtPW = findViewById(R.id.txtPW)
        txtName = findViewById(R.id.txtName)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        user = getUserExtra() ?: User()
        txtUN.setText(user.username)
        txtPW.setText(user.password)
        txtName.setText(user.fullname)

        btnSave.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                val username = txtUN.text.toString().trim()
                val password = txtPW.text.toString().trim()
                val fullname = txtName.text.toString().trim()

                if (username.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return
                }

                val updated = User(username, password, fullname)
                val result = Intent().putExtra("user", updated)
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
