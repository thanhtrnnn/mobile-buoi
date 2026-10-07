package ptit.cnpm1.tranxuanthanh.bai08

import java.util.Locale

/** Định dạng số tiền với hậu tố k, M hoặc B. */
object Money {

    private val UNITS = listOf("", "k", "M", "B")

    fun format(amount: Float): String {
        var value = amount
        var unit = 0
        while (value >= 1000f && unit < UNITS.size - 1) {
            value /= 1000f
            unit++
        }
        // Chuyển sang đơn vị kế tiếp nếu làm tròn đạt 1000.
        if (unit in 1 until UNITS.size - 1 && Math.round(value * 100) >= 100_000) {
            value /= 1000f
            unit++
        }
        return short(value) + UNITS[unit]
    }

    /** Làm tròn hai chữ số thập phân và bỏ số 0 thừa. */
    private fun short(value: Float): String {
        val text = String.format(Locale.US, "%.2f", value)
            .trimEnd('0')
            .trimEnd('.')
        return text.replace('.', ',')
    }
}
