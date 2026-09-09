package ptit.cnpm1.tranxuanthanh.bai03

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class ViewUserAct : Activity(), View.OnClickListener {

    companion object {
        private const val REQ_EDIT = 1
    }

    private lateinit var lblUN: TextView
    private lateinit var lblPW: TextView
    private lateinit var lblName: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnDel: Button

    private lateinit var user: User
    private var position: Int = -1
    private var wasUpdated = false

    private var usernames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewuser)

        lblUN = findViewById(R.id.lblUN)
        lblPW = findViewById(R.id.lblPW)
        lblName = findViewById(R.id.lblName)
        btnEdit = findViewById(R.id.btnEdit)
        btnDel = findViewById(R.id.btnDel)

        user = getUserExtra(intent) ?: User()
        position = intent.getIntExtra("position", -1)
        usernames = intent.getStringArrayListExtra("usernames") ?: ArrayList()

        btnEdit.setOnClickListener(this)
        btnDel.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        renderUser()
    }

    private fun renderUser() {
        lblUN.text = "Username: " + user.username
        lblPW.text = "Password: " + "*".repeat(user.password.length)
        lblName.text = "Fullname: " + user.fullname
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnEdit -> {
                val intent = Intent(this, EditUserAct::class.java).apply {
                    putExtra("user", user)
                    putExtra("usernames", usernames)
                }
                startActivityForResult(intent, REQ_EDIT)
            }
            R.id.btnDel -> {
                AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa người dùng \"" + user.fullname + "\" không?")
                    .setPositiveButton("Có") { _, _ ->
                        val result = Intent().apply {
                            putExtra("action", "delete")
                            putExtra("position", position)
                        }
                        setResult(RESULT_OK, result)
                        finish()
                    }
                    .setNegativeButton("Không", null)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_EDIT && resultCode == RESULT_OK && data != null) {
            val edited = getUserExtra(data) ?: return
            // Cập nhật luôn danh sách username, để lần sửa kế tiếp không bị
            // chặn nhầm bởi tên cũ vừa được giải phóng
            val idx = usernames.indexOfFirst { it.equals(user.username, ignoreCase = true) }
            if (idx >= 0) usernames[idx] = edited.username
            user = edited
            wasUpdated = true
            renderUser()
        }
    }

    override fun onBackPressed() {
        if (wasUpdated) {
            val result = Intent().apply {
                putExtra("action", "update")
                putExtra("user", user)
                putExtra("position", position)
            }
            setResult(RESULT_OK, result)
        }
        super.onBackPressed()
    }

    private fun getUserExtra(source: Intent): User? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            source.getSerializableExtra("user", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            source.getSerializableExtra("user") as? User
        }
    }
}
