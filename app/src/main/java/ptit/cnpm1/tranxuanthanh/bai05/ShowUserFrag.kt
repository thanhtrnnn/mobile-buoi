package ptit.cnpm1.tranxuanthanh.bai05

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * Một dòng trong danh sách người dùng ở màn Home: khung tên + dấu ••• mở menu.
 *
 * ListView tái sử dụng View khi cuộn, nên mọi thứ phải được gán lại đầy đủ
 * trong [bind] ở mỗi lần adapter gọi tới.
 */
class ShowUserFrag @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var txtUser: TextView
    private lateinit var lblMenu: TextView

    init {
        onCreate()
    }

    /**
     * Dựng giao diện cho dòng: nạp ShowUser.xml rồi lấy 2 TextView ra dùng.
     * Đây là lớp View chứ không phải Fragment của Android, nên hàm này do
     * chính khối init gọi chứ không phải hệ thống gọi.
     */
    fun onCreate() {
        LayoutInflater.from(context).inflate(R.layout.showuser, this, true)
        txtUser = findViewById(R.id.lblUser)
        lblMenu = findViewById(R.id.lblMenu)
    }

    /** Đổ dữ liệu của một User vào dòng này. */
    fun bind(user: User) {
        txtUser.text = user.username + "\n" + user.fullname
    }

    /** Đăng ký việc cần làm khi bấm dấu ••• — truyền lại chính View đó để neo menu. */
    fun setOnMenuClickListener(action: (View) -> Unit) {
        lblMenu.setOnClickListener { action(lblMenu) }
    }
}
