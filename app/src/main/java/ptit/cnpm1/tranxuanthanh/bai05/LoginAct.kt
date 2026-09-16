package ptit.cnpm1.tranxuanthanh.bai05

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginAct : Activity(), View.OnClickListener {

    private lateinit var txtUN: EditText
    private lateinit var txtPW: EditText
    private lateinit var btnLogin: Button

    private lateinit var dao: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        txtUN = findViewById(R.id.txtUN)
        txtPW = findViewById(R.id.txtPW)
        btnLogin = findViewById(R.id.btnLogin)

        dao = UserDAO(this)

        btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnLogin) {
            // Đóng gói UN và PW trên layout thành một đối tượng User
            val input = User(
                username = txtUN.text.toString().trim(),
                password = txtPW.text.toString().trim()
            )

            if (!dao.checkLogin(input)) {
                Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                return
            }

            // checkLogin chỉ trả về đúng/sai, nên tìm thêm bản ghi đầy đủ để
            // màn Home có họ tên mà chào. search() khớp gần đúng nên phải lọc
            // lại cho đúng username vừa nhập.
            val user = dao.search(input.username)
                .firstOrNull { it.username == input.username } ?: input

            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, UserhomeAct::class.java).apply {
                putExtra("user", user)
            }
            startActivity(intent)
            finish()
        }
    }
}
