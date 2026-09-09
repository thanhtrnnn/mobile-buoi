package ptit.cnpm1.tranxuanthanh.bai04

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
import java.util.Calendar

class UserhomeAct : Activity(), View.OnClickListener {

    companion object {
        private const val REQ_ADD = 1
        private const val REQ_EDIT = 2
    }

    private lateinit var txtWelcome: TextView
    private lateinit var btnAdd: Button
    private lateinit var lwUsers: ListView

    private var user: User? = null
    private val listUser = ArrayList<User>()
    private lateinit var adapter: ArrayAdapter<User>

    /** Vị trí dòng đang được sửa, để biết ghi kết quả trả về vào đâu. */
    private var editingPosition = -1

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userhome)

        txtWelcome = findViewById(R.id.lblWelcome)
        btnAdd = findViewById(R.id.btnAdd)
        lwUsers = findViewById(R.id.lwUsers)

        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("user", User::class.java)
        } else {
            intent.getSerializableExtra("user") as? User
        }
        user?.let { txtWelcome.text = "Xin chào " + it.fullname }

        fun dateOf(year: Int, month: Int, day: Int) = Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        if (listUser.isEmpty()) {
            listUser.add(User("nd1", "12345678", "Nguyễn B", dateOf(2005, 3, 12)))
            listUser.add(User("nd2", "12345678", "Trần C", dateOf(2004, 11, 2)))
            listUser.add(User("nd3", "12345678", "Lê Thị D", dateOf(2005, 7, 25)))
            listUser.add(User("nd4", "12345678", "Hoàng E", dateOf(2003, 1, 8)))
        }

        adapter = object : ArrayAdapter<User>(this, 0, listUser) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = convertView as? ShowUserFrag ?: ShowUserFrag(this@UserhomeAct)
                row.bind(listUser[position])
                // Bấm ••• thì xổ menu Sửa / Xóa ngay dưới chính dấu ••• đó
                row.setOnMenuClickListener { anchor ->
                    MenuItemFrag(this@UserhomeAct).showAt(
                        anchor,
                        onEdit = {
                            editingPosition = position
                            val edit = Intent(this@UserhomeAct, EditAct::class.java).apply {
                                putExtra("user", listUser[position])
                            }
                            startActivityForResult(edit, REQ_EDIT)
                        },
                        onDelete = {
                            AlertDialog.Builder(this@UserhomeAct)
                                .setTitle("Xác nhận xóa")
                                .setMessage("Bạn có chắc chắn muốn xóa người dùng \"" + listUser[position].fullname + "\" không?")
                                .setPositiveButton("Có") { _, _ ->
                                    listUser.removeAt(position)
                                    adapter.notifyDataSetChanged()
                                }
                                .setNegativeButton("Không", null)
                                .show()
                        }
                    )
                }
                return row
            }
        }
        lwUsers.adapter = adapter

        btnAdd.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnAdd) {
            startActivityForResult(Intent(this, AddAct::class.java), REQ_ADD)
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) return

        val returned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            data.getSerializableExtra("user", User::class.java)
        } else {
            data.getSerializableExtra("user") as? User
        } ?: return

        when (requestCode) {
            REQ_ADD -> {
                // Chặn trùng username: danh sách nằm ở đây nên kiểm tra ngay tại đây
                if (listUser.any { it.username.equals(returned.username, ignoreCase = true) }) {
                    Toast.makeText(this, "Username \"" + returned.username + "\" đã tồn tại", Toast.LENGTH_SHORT).show()
                    return
                }
                listUser.add(returned)
            }
            REQ_EDIT -> {
                if (editingPosition !in listUser.indices) return
                val clash = listUser.indices.any { i ->
                    i != editingPosition && listUser[i].username.equals(returned.username, ignoreCase = true)
                }
                if (clash) {
                    Toast.makeText(this, "Username \"" + returned.username + "\" đã tồn tại", Toast.LENGTH_SHORT).show()
                    return
                }
                listUser[editingPosition] = returned
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}
