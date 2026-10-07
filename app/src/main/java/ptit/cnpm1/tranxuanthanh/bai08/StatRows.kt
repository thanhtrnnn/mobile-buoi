package ptit.cnpm1.tranxuanthanh.bai08

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale

/** Dòng thống kê có chấm màu trùng với lát biểu đồ, logo mục và tổng tiền. */
class StatRow(context: Context) : LinearLayout(context) {
    private val dot = TextView(context).apply { text = "●"; textSize = 20f }
    private val icon = ImageView(context)
    private val name = TextView(context).apply { textSize = 17f }
    private val amount = TextView(context).apply { textSize = 17f; gravity = Gravity.END }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(8), dp(12), dp(8), dp(12))
        setBackgroundResource(R.drawable.bg_list_item)
        addView(dot, LayoutParams(dp(24), LayoutParams.WRAP_CONTENT))
        addView(icon, LayoutParams(dp(36), dp(36)))
        addView(name, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply { marginStart = dp(10) })
        addView(amount, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        layoutParams = android.widget.AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun bind(stat: CategoryStat, total: Double, index: Int) {
        dot.setTextColor(PieChartView.colorAt(index))
        icon.setImageResource(Icons.resOf(stat.icon))
        val color = context.getColor(if (stat.type.id == CategoryType.ID_THU) R.color.thu else R.color.chi)
        icon.setColorFilter(color)
        name.text = stat.name
        name.setTextColor(color)
        val percent = if (total > 0) stat.total / total * 100 else 0.0
        amount.text = "${Money.format(stat.total)}\n${String.format(Locale("vi", "VN"), "%.2f%%", percent)}"
        amount.setTextColor(color)
        contentDescription = "${stat.name}, ${amount.text}"
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
