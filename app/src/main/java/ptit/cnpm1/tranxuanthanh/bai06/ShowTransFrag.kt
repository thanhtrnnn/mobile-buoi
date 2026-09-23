package ptit.cnpm1.tranxuanthanh.bai06

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * Một dòng giao dịch ở trang home: logo của mục, tên mục, số tiền.
 *
 * ListView tái sử dụng View khi cuộn, nên mọi thứ phải được gán lại đầy đủ
 * trong [bind] ở mỗi lần adapter gọi tới.
 */
class ShowTransFrag @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var imgIcon: ImageView
    private lateinit var lblName: TextView
    private lateinit var lblAmount: TextView

    init {
        onCreate()
    }

    /**
     * Dựng giao diện cho dòng: nạp showtrans.xml rồi lấy 3 View ra dùng.
     * Đây là lớp View chứ không phải Fragment của Android, nên hàm này do
     * chính khối init gọi chứ không phải hệ thống gọi.
     */
    fun onCreate() {
        LayoutInflater.from(context).inflate(R.layout.showtrans, this, true)
        imgIcon = findViewById(R.id.imgIcon)
        lblName = findViewById(R.id.lblName)
        lblAmount = findViewById(R.id.lblAmount)
    }

    /** Đổ dữ liệu một giao dịch vào dòng này. Khoản thu xanh, khoản chi đỏ, cả logo lẫn chữ. */
    fun bind(t: Transaction) {
        imgIcon.setImageResource(Icons.resOf(t.category.icon))
        lblName.text = t.category.name
        lblAmount.text = Money.format(t.amount)

        val color = if (t.category.type.id == CategoryType.ID_THU) {
            resources.getColor(R.color.thu)
        } else {
            resources.getColor(R.color.chi)
        }
        lblName.setTextColor(color)
        lblAmount.setTextColor(color)
        imgIcon.setColorFilter(color)
    }
}
