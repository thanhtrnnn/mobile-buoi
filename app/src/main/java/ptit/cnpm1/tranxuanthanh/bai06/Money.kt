package ptit.cnpm1.tranxuanthanh.bai06

import java.util.Locale

/**
 * Cách hiện số tiền theo yêu cầu slide 11: từ 1.000 hiện đơn vị k, từ
 * 1.000.000 hiện M, từ 1.000.000.000 hiện B, làm tròn 2 chữ số sau dấu phẩy.
 * Ví dụ 1537000 hiện "1,54M".
 */
object Money {

    fun format(amount: Float): String = when {
        amount >= 1_000_000_000f -> short(amount / 1_000_000_000f) + "B"
        amount >= 1_000_000f -> short(amount / 1_000_000f) + "M"
        amount >= 1_000f -> short(amount / 1_000f) + "k"
        else -> short(amount)
    }

    /** Làm tròn 2 chữ số, dùng dấu phẩy thập phân và bỏ phần ",00" thừa. */
    private fun short(value: Float): String {
        val text = String.format(Locale.US, "%.2f", value)
            .trimEnd('0')
            .trimEnd('.')
        return text.replace('.', ',')
    }
}
