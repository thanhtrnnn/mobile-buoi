package ptit.cnpm1.tranxuanthanh.bai02

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginAct : Activity(), View.OnClickListener {

    private val HARDCODE_MASV = "B23DCAT280"
    private val HARDCODE_PW = "20082005"
    private val HARDCODE_FULLNAME = "Trần Xuân Thành"

    private lateinit var txtUN: EditText
    private lateinit var txtPW: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        txtUN = findViewById(R.id.txtUN)
        txtPW = findViewById(R.id.txtPW)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnLogin || v == btnLogin) {
            val usernameInput = txtUN.text.toString().trim()
            val passwordInput = txtPW.text.toString().trim()

            val user = User(username = usernameInput, password = passwordInput)

            if (user.username == HARDCODE_MASV && user.password == HARDCODE_PW) {
                user.fullname = HARDCODE_FULLNAME

                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, UserhomeAct::class.java).apply {
                    putExtra("user", user)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
