package ptit.cnpm1.tranxuanthanh.bai07

import java.util.Locale

/** formats amounts with k, m, or b suffixes */
object Money {

    private val UNITS = listOf("", "k", "M", "B")

    fun format(amount: Float): String {
        var value = amount
        var unit = 0
        while (value >= 1000f && unit < UNITS.size - 1) {
            value /= 1000f
            unit++
        }
        // round up to the next suffix when the value reaches 1000
        if (unit in 1 until UNITS.size - 1 && Math.round(value * 100) >= 100_000) {
            value /= 1000f
            unit++
        }
        return short(value) + UNITS[unit]
    }

    /** round to two decimals and trim trailing zeros */
    private fun short(value: Float): String {
        val text = String.format(Locale.US, "%.2f", value)
            .trimEnd('0')
            .trimEnd('.')
        return text.replace('.', ',')
    }
}
