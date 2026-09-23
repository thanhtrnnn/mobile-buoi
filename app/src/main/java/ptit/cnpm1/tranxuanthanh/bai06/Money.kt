package ptit.cnpm1.tranxuanthanh.bai06

import java.util.Locale

/**
 * Cách hiện số tiền theo yêu cầu slide 11: từ 1.000 hiện đơn vị k, từ
 * 1.000.000 hiện M, từ 1.000.000.000 hiện B, làm tròn 2 chữ số sau dấu phẩy.
 * Ví dụ 1537000 hiện "1,54M".
 */
object Money {

    private val UNITS = listOf("", "k", "M", "B")

    fun format(amount: Float): String {
        var value = amount
        var unit = 0
        while (value >= 1000f && unit < UNITS.size - 1) {
            value /= 1000f
            unit++
        }
        // Chọn đơn vị xong mới làm tròn thì 999999 thành 999,999k -> "1000k".
        // Làm tròn mà chạm 1000 thì phải lên đơn vị kế: 999999 hiện "1M".
        if (unit in 1 until UNITS.size - 1 && Math.round(value * 100) >= 100_000) {
            value /= 1000f
            unit++
        }
        return short(value) + UNITS[unit]
    }

    /** Làm tròn 2 chữ số, dùng dấu phẩy thập phân và bỏ phần ",00" thừa. */
    private fun short(value: Float): String {
        val text = String.format(Locale.US, "%.2f", value)
            .trimEnd('0')
            .trimEnd('.')
        return text.replace('.', ',')
    }
}
