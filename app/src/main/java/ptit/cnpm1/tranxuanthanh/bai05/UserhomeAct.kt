package ptit.cnpm1.tranxuanthanh.bai05

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

class UserhomeAct : Activity(), View.OnClickListener {

    private lateinit var txtWelcome: TextView
    private lateinit var btnAdd: Button
    private lateinit var lwUsers: ListView

    private var user: User? = null
    private val listUser = ArrayList<User>()
    private lateinit var adapter: ArrayAdapter<User>

    private lateinit var dao: UserDAO

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userhome)

        txtWelcome = findViewById(R.id.lblWelcome)
        btnAdd = findViewById(R.id.btnAdd)
        lwUsers = findViewById(R.id.lwUsers)

        dao = UserDAO(this)

        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("user", User::class.java)
        } else {
            intent.getSerializableExtra("user") as? User
        }
        user?.let { txtWelcome.text = "Xin chào " + it.fullname }

        adapter = object : ArrayAdapter<User>(this, 0, listUser) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = convertView as? ShowUserFrag ?: ShowUserFrag(this@UserhomeAct)
                row.bind(listUser[position])
                // Bấm ••• thì xổ menu Sửa / Xóa ngay dưới chính dấu ••• đó
                row.setOnMenuClickListener { anchor ->
                    MenuItemFrag(this@UserhomeAct).showAt(
                        anchor,
                        onEdit = {
                            val edit = Intent(this@UserhomeAct, EditAct::class.java).apply {
                                putExtra("user", listUser[position])
                            }
                            startActivity(edit)
                        },
                        onDelete = { confirmDelete(listUser[position]) }
                    )
                }
                return row
            }
        }
        lwUsers.adapter = adapter

        // Danh sách ban đầu do onResume() đổ vào, vì onResume() luôn chạy
        // ngay sau onCreate()

        btnAdd.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnAdd) {
            startActivity(Intent(this, AddAct::class.java))
        }
    }

    /** Đọc lại toàn bộ người dùng từ CSDL và vẽ lại ListView. */
    private fun reload() {
        listUser.clear()
        listUser.addAll(dao.getAll())
        adapter.notifyDataSetChanged()
    }

    private fun confirmDelete(target: User) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa người dùng \"" + target.fullname + "\" không?")
            .setPositiveButton("Có") { _, _ ->
                if (dao.delete(target.id)) {
                    reload()
                } else {
                    Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                }
            }
            // Bấm Không thì không làm gì, ở nguyên trang home
            .setNegativeButton("Không", null)
            .show()
    }

    /**
     * Mỗi lần quay lại trang home (từ màn Thêm hoặc màn Sửa) thì đọc lại CSDL.
     * AddAct và EditAct đã tự gọi add() / edit() rồi, nên ở đây chỉ cần lấy
     * danh sách mới nhất về hiển thị; bấm Hủy thì CSDL không đổi nên danh sách
     * vẽ lại vẫn y nguyên.
     */
    override fun onResume() {
        super.onResume()
        reload()
    }
}
