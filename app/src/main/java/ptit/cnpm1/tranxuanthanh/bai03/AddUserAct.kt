package ptit.cnpm1.tranxuanthanh.bai03

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class AddUserAct : Activity(), View.OnClickListener {

    private lateinit var formUser: UserFormView
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button

    private var usernames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adduser)

        formUser = findViewById(R.id.formUser)
        btnAdd = findViewById(R.id.btnAdd)
        btnCancel = findViewById(R.id.btnCancel)

        usernames = intent.getStringArrayListExtra("usernames") ?: ArrayList()

        btnAdd.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAdd -> {
                if (!formUser.isFilled()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return
                }

                val newUser = formUser.user
                if (usernames.any { it.equals(newUser.username, ignoreCase = true) }) {
                    Toast.makeText(this, "Username \"" + newUser.username + "\" đã tồn tại", Toast.LENGTH_SHORT).show()
                    return
                }

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
