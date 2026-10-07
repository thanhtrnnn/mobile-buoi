package ptit.cnpm1.tranxuanthanh.bai08

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.abs

/** Biểu đồ cột chồng thu (xanh) và chi (đỏ), dùng cùng một thang đo cho 12 tháng. */
class ProcessChartView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var periods: List<StatPeriod> = emptyList()
    private var stats: List<TimeStat> = emptyList()
    var onMonthClick: ((StatPeriod) -> Unit)? = null
    private var downX = 0f
    private var downY = 0f
    private var moved = false
    private var clickedIndex = -1
    private val density get() = resources.displayMetrics.density

    fun submit(months: List<StatPeriod>, values: List<TimeStat>) {
        periods = months.toList()
        stats = values.toList()
        contentDescription = months.zip(values).joinToString { (month, total) ->
            "${month.title}: thu ${Money.format(total.totalIn)}, chi ${Money.format(total.totalOut)}"
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (stats.isEmpty()) return
        val top = 48f * density
        val bottom = height - 68f * density
        val chartHeight = (bottom - top).coerceAtLeast(1f)
        val maximum = stats.maxOf { it.totalIn.toDouble() + it.totalOut }.coerceAtLeast(1.0)
        val cellWidth = width.toFloat() / stats.size
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 13f * resources.displayMetrics.scaledDensity
        paint.color = Color.DKGRAY
        canvas.drawText("Thang đo tối đa: ${Money.format(maximum.toFloat())}", width / 2f, 24f * density, paint)
        paint.strokeWidth = density
        canvas.drawLine(0f, bottom, width.toFloat(), bottom, paint)
        stats.forEachIndexed { index, stat ->
            val center = (index + 0.5f) * cellWidth
            val left = center - cellWidth * 0.3f
            val right = center + cellWidth * 0.3f
            val incomeHeight = (stat.totalIn / maximum * chartHeight).toFloat()
            val expenseHeight = (stat.totalOut / maximum * chartHeight).toFloat()
            val middle = bottom - incomeHeight
            paint.color = context.getColor(R.color.thu)
            canvas.drawRect(left, middle, right, bottom, paint)
            paint.color = context.getColor(R.color.chi)
            canvas.drawRect(left, middle - expenseHeight, right, middle, paint)
            // Nhãn luôn có đủ thu/chi, kể cả cột bằng 0 hoặc quá thấp để chứa chữ bên trong.
            paint.color = context.getColor(R.color.thu)
            canvas.drawText(Money.format(stat.totalIn), center, bottom + 19f * density, paint)
            paint.color = context.getColor(R.color.chi)
            canvas.drawText(Money.format(stat.totalOut), center, bottom + 37f * density, paint)
            paint.color = Color.BLACK
            val month = periods[index]
            canvas.drawText("${month.month}/${month.year}", center, bottom + 57f * density, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val slop = ViewConfiguration.get(context).scaledTouchSlop
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                moved = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (abs(event.x - downX) > slop || abs(event.y - downY) > slop) moved = true
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (!moved && abs(event.x - downX) <= slop && abs(event.y - downY) <= slop && periods.isNotEmpty()) {
                    clickedIndex = (event.x / (width.toFloat() / periods.size)).toInt().coerceIn(periods.indices)
                    performClick()
                }
                return true
            }
            MotionEvent.ACTION_CANCEL -> { moved = true; return true }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        periods.getOrNull(clickedIndex)?.let { onMonthClick?.invoke(it) }
        return true
    }
}
