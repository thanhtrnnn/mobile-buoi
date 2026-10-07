package ptit.cnpm1.tranxuanthanh.bai08

import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Các hàm hỗ trợ ngày tháng cho lịch tuần và lịch tháng. */
object WalletCalendar {

    private val locale = Locale("vi", "VN")

    fun weekDates(anchor: Date): List<Date> {
        val calendar = calendar(anchor)
        val daysAfterMonday = (calendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7
        calendar.add(Calendar.DAY_OF_MONTH, -daysAfterMonday)
        return List(7) {
            calendar.time.also { calendar.add(Calendar.DAY_OF_MONTH, 1) }
        }
    }

    /** Tạo đủ các hàng từ thứ Hai đến Chủ nhật của tháng. */
    fun monthDates(anchor: Date): List<Date> {
        val first = calendar(anchor).apply { set(Calendar.DAY_OF_MONTH, 1) }
        val lastDay = first.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstOffset = (first.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7
        val cellCount = ((firstOffset + lastDay + 6) / 7) * 7
        first.add(Calendar.DAY_OF_MONTH, -firstOffset)
        return List(cellCount) {
            first.time.also { first.add(Calendar.DAY_OF_MONTH, 1) }
        }
    }

    fun changeMonth(anchor: Date, amount: Int): Date {
        val calendar = calendar(anchor)
        val preferredDay = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MONTH, amount)
        calendar.set(
            Calendar.DAY_OF_MONTH,
            minOf(preferredDay, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        )
        return calendar.time
    }

    fun isSameDay(first: Date, second: Date): Boolean {
        val a = calendar(first)
        val b = calendar(second)
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    }

    fun isSameMonth(first: Date, second: Date): Boolean {
        val a = calendar(first)
        val b = calendar(second)
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
    }

    fun weekDayShort(date: Date): String = when (calendar(date).get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "T2"
        Calendar.TUESDAY -> "T3"
        Calendar.WEDNESDAY -> "T4"
        Calendar.THURSDAY -> "T5"
        Calendar.FRIDAY -> "T6"
        Calendar.SATURDAY -> "T7"
        else -> "CN"
    }

    fun weekdayHeaders(): List<String> = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

    fun monthTitle(date: Date): String {
        val calendar = calendar(date)
        return "Tháng ${calendar.get(Calendar.MONTH) + 1}, ${calendar.get(Calendar.YEAR)}"
    }

    private fun calendar(date: Date) = Calendar.getInstance(locale).apply {
        firstDayOfWeek = Calendar.MONDAY
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}
