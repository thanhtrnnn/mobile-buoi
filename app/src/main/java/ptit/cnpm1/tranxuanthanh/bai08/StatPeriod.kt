package ptit.cnpm1.tranxuanthanh.bai08

import android.content.Intent
import java.util.Calendar
import java.util.Locale

/** Kỳ thống kê: tháng từ 1 đến 12; tháng bằng 0 nghĩa là cả năm. */
data class StatPeriod(val year: Int, val month: Int = 0) {
    init {
        require(year in 1..9999 && month in 0..12)
    }

    val title: String get() = if (month == 0) "Năm $year" else "Tháng $month/$year"
    val start: String get() = String.format(Locale.US, "%04d-%02d-01", year, if (month == 0) 1 else month)
    val end: String get() = if (month == 0 || month == 12) {
        String.format(Locale.US, "%04d-01-01", year + 1)
    } else String.format(Locale.US, "%04d-%02d-01", year, month + 1)

    /** Truyền cùng một kỳ qua các màn hình để biểu đồ và chi tiết dùng chung bộ lọc. */
    fun putInto(intent: Intent): Intent = intent.putExtra("stat_year", year).putExtra("stat_month", month)

    override fun toString(): String = title

    companion object {
        fun current(): StatPeriod = Calendar.getInstance().let {
            StatPeriod(it.get(Calendar.YEAR), it.get(Calendar.MONTH) + 1)
        }

        fun from(intent: Intent): StatPeriod {
            val current = current()
            return StatPeriod(intent.getIntExtra("stat_year", current.year), intent.getIntExtra("stat_month", current.month))
        }

        /** Luôn đủ 12 tháng, theo thứ tự từ cũ đến mới và có tháng hiện tại. */
        fun lastTwelveMonths(): List<StatPeriod> {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, -11)
            }
            return List(12) {
                StatPeriod(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1).also {
                    calendar.add(Calendar.MONTH, 1)
                }
            }
        }
    }
}
