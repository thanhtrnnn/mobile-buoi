package ptit.cnpm1.tranxuanthanh.bai03

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AddUserAct : Activity(), View.OnClickListener {

    private lateinit var txtUN: EditText
    private lateinit var txtPW: EditText
    private lateinit var txtName: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adduser)

        txtUN = findViewById(R.id.txtUN)
        txtPW = findViewById(R.id.txtPW)
        txtName = findViewById(R.id.txtName)
        btnAdd = findViewById(R.id.btnAdd)
        btnCancel = findViewById(R.id.btnCancel)

        btnAdd.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAdd -> {
                val username = txtUN.text.toString().trim()
                val password = txtPW.text.toString().trim()
                val fullname = txtName.text.toString().trim()

                if (username.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return
                }

                val newUser = User(username, password, fullname)
                val result = Intent().putExtra("user", newUser)
                setResult(RESULT_OK, result)
                finish()
            }
            R.id.btnCancel -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }
}
