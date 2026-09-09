package ptit.cnpm1.tranxuanthanh.bai03

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
import java.util.Calendar

class UserhomeAct : Activity(), View.OnClickListener {

    companion object {
        private const val REQ_ADD = 1
        private const val REQ_EDIT = 2
    }

    private lateinit var lblWelcome: TextView
    private lateinit var btnAdd: Button
    private lateinit var lwUsers: ListView

    private var user: User? = null
    private val listUser = ArrayList<User>()
    private lateinit var adapter: ArrayAdapter<User>

    /** Vị trí dòng đang được sửa, để biết ghi kết quả trả về vào đâu. */
    private var editingPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userhome)

        lblWelcome = findViewById(R.id.lblWelcome)
        btnAdd = findViewById(R.id.btnAdd)
        lwUsers = findViewById(R.id.lwUsers)

        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("user", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("user") as? User
        }
        user?.let { lblWelcome.text = "Xin chào " + it.fullname }

        seedInitialUsers()

        adapter = object : ArrayAdapter<User>(this, 0, listUser) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = convertView as? ShowUserFrag ?: ShowUserFrag(this@UserhomeAct)
                row.bind(listUser[position])
                row.setOnMenuClickListener { anchor -> showRowMenu(anchor, position) }
                return row
            }
        }
        lwUsers.adapter = adapter

        btnAdd.setOnClickListener(this)
    }

    private fun seedInitialUsers() {
        if (listUser.isEmpty()) {
            listUser.add(User("nd1", "12345678", "Nguyễn B", dateOf(2005, 3, 12)))
            listUser.add(User("nd2", "12345678", "Trần C", dateOf(2004, 11, 2)))
            listUser.add(User("nd3", "12345678", "Lê Thị D", dateOf(2005, 7, 25)))
            listUser.add(User("nd4", "12345678", "Hoàng E", dateOf(2003, 1, 8)))
        }
    }

    private fun dateOf(year: Int, month: Int, day: Int) = Calendar.getInstance().apply {
        set(year, month - 1, day, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    /** Xổ menu Sửa / Xóa ngay dưới dấu ••• của dòng thứ [position]. */
    private fun showRowMenu(anchor: View, position: Int) {
        MenuItemFrag(this).showAt(
            anchor,
            onEdit = { openEdit(position) },
            onDelete = { confirmDelete(position) }
        )
    }

    private fun openEdit(position: Int) {
        editingPosition = position
        val intent = Intent(this, EditUserAct::class.java).apply {
            putExtra("user", listUser[position])
            putExtra("usernames", takenUsernames())
        }
        startActivityForResult(intent, REQ_EDIT)
    }

    private fun confirmDelete(position: Int) {
        val target = listUser[position]
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa người dùng \"" + target.fullname + "\" không?")
            .setPositiveButton("Có") { _, _ ->
                listUser.removeAt(position)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Không", null)
            .show()
    }

    /** Danh sách username đang có, gửi sang màn Thêm/Sửa để chặn trùng. */
    private fun takenUsernames(): ArrayList<String> {
        val names = ArrayList<String>()
        for (u in listUser) names.add(u.username)
        return names
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnAdd) {
            val intent = Intent(this, AddUserAct::class.java).apply {
                putExtra("usernames", takenUsernames())
            }
            startActivityForResult(intent, REQ_ADD)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) return

        val returned = getUserExtra(data) ?: return
        when (requestCode) {
            REQ_ADD -> listUser.add(returned)
            REQ_EDIT -> {
                if (editingPosition !in listUser.indices) return
                listUser[editingPosition] = returned
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun getUserExtra(data: Intent): User? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            data.getSerializableExtra("user", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            data.getSerializableExtra("user") as? User
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}
