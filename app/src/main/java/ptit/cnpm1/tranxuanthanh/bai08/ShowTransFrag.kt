package ptit.cnpm1.tranxuanthanh.bai08

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

/** Dòng giao dịch gồm biểu tượng, tên mục và số tiền. */
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

    /** Nạp giao diện dòng và lấy các thành phần hiển thị. */
    fun onCreate() {
        LayoutInflater.from(context).inflate(R.layout.showtrans, this, true)
        imgIcon = findViewById(R.id.imgIcon)
        lblName = findViewById(R.id.lblName)
        lblAmount = findViewById(R.id.lblAmount)
    }

    /** Gắn giao dịch và tô xanh cho thu, đỏ cho chi. */
    fun bind(t: Transaction, showDate: Boolean = false) {
        imgIcon.setImageResource(Icons.resOf(t.category.icon))
        lblName.text = if (showDate) TransactionDAO.toText(t.date) else t.category.name
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
