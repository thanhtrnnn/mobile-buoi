package ptit.cnpm1.tranxuanthanh.bai08

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/** Tự vẽ biểu đồ tròn dạng vành khuyên như slide 23, không cần thư viện ngoài. */
class PieChartView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var stats: List<CategoryStat> = emptyList()

    fun submit(items: List<CategoryStat>) {
        stats = items.toList()
        contentDescription = if (stats.isEmpty()) "Chưa có giao dịch" else stats.joinToString { "${it.name}: ${Money.format(it.total)}" }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = minOf(width, height) * 0.43f
        val cx = width / 2f
        val cy = height / 2f
        val bounds = RectF(cx - radius, cy - radius, cx + radius, cy + radius)
        val total = stats.sumOf { it.total.toDouble() }
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = radius * 0.32f
        if (total <= 0) {
            paint.color = Color.LTGRAY
            canvas.drawOval(bounds, paint)
        } else {
            var start = -90f
            stats.forEachIndexed { index, stat ->
                val sweep = (stat.total / total * 360).toFloat()
                paint.color = colorAt(index)
                canvas.drawArc(bounds, start, sweep, false, paint)
                start += sweep
            }
        }
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.DKGRAY
        paint.textSize = 16f * resources.displayMetrics.scaledDensity
        val text = if (total <= 0) "Chưa có dữ liệu" else "${stats.size} mục"
        canvas.drawText(text, cx, cy - (paint.ascent() + paint.descent()) / 2, paint)
    }

    companion object {
        private val colors = intArrayOf(0xFF147D92.toInt(), 0xFFF3AD37.toInt(), 0xFF845EC2.toInt(),
            0xFFDF6684.toInt(), 0xFF369C75.toInt(), 0xFF4476CC.toInt(), 0xFFB87938.toInt(), 0xFF6B8A91.toInt())
        fun colorAt(index: Int): Int = colors[index % colors.size]
    }
}
