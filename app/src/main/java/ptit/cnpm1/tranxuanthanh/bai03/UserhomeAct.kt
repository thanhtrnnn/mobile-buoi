package ptit.cnpm1.tranxuanthanh.bai03

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView

class UserhomeAct : Activity(), View.OnClickListener {

    companion object {
        private const val REQ_ADD = 1
        private const val REQ_VIEW = 2
    }

    private lateinit var txtWelcome: TextView
    private lateinit var btnAdd: Button
    private lateinit var lwUsers: ListView

    private var user: User? = null
    private val listUser = ArrayList<User>()
    private lateinit var adapter: ArrayAdapter<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userhome)

        txtWelcome = findViewById(R.id.txtWelcome)
        btnAdd = findViewById(R.id.btnAdd)
        lwUsers = findViewById(R.id.lwUsers)

        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("user", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("user") as? User
        }
        user?.let { txtWelcome.text = "Xin chào " + it.fullname }

        seedInitialUsers()

        adapter = object : ArrayAdapter<User>(
            this,
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            listUser
        ) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val u = listUser[position]
                view.setBackgroundResource(R.drawable.bg_list_item)
                val pad = (16 * resources.displayMetrics.density).toInt()
                view.setPadding(pad, pad, pad, pad)
                view.findViewById<TextView>(android.R.id.text1).apply {
                    text = u.username
                    textSize = 18f
                    gravity = android.view.Gravity.CENTER_HORIZONTAL
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                }
                view.findViewById<TextView>(android.R.id.text2).apply {
                    text = u.fullname
                    textSize = 16f
                    gravity = android.view.Gravity.CENTER_HORIZONTAL
                }
                return view
            }
        }
        lwUsers.adapter = adapter

        btnAdd.setOnClickListener(this)
        lwUsers.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, ViewUserAct::class.java).apply {
                putExtra("user", listUser[position])
                putExtra("position", position)
            }
            startActivityForResult(intent, REQ_VIEW)
        }
    }

    private fun seedInitialUsers() {
        if (listUser.isEmpty()) {
            listUser.add(User("nd1", "12345678", "Nguyễn B"))
            listUser.add(User("nd2", "12345678", "Trần C"))
            listUser.add(User("nd3", "12345678", "Lê Thị D"))
            listUser.add(User("nd4", "12345678", "Hoàng E"))
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnAdd) {
            startActivityForResult(Intent(this, AddUserAct::class.java), REQ_ADD)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) return

        when (requestCode) {
            REQ_ADD -> {
                val newUser = getUserExtra(data) ?: return
                listUser.add(newUser)
                adapter.notifyDataSetChanged()
            }
            REQ_VIEW -> {
                val position = data.getIntExtra("position", -1)
                if (position !in listUser.indices) return
                when (data.getStringExtra("action")) {
                    "delete" -> listUser.removeAt(position)
                    "update" -> {
                        val updated = getUserExtra(data) ?: return
                        listUser[position] = updated
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
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
