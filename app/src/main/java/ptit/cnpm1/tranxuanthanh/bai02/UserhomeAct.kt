package ptit.cnpm1.tranxuanthanh.bai02

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.TextView

class UserhomeAct : Activity() {

    private lateinit var txtWelcome: TextView
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userhome)

        txtWelcome = findViewById(R.id.txtWelcome)

        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("user", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("user") as? User
        }

        user?.let {
            txtWelcome.text = "Xin chào " + it.fullname
        }
    }
}
