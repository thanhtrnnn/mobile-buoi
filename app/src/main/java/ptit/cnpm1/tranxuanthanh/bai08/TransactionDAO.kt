package ptit.cnpm1.tranxuanthanh.bai08

import android.content.ContentValues
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Thêm, đọc, sửa, xóa giao dịch và tính tổng cho lịch. */
class TransactionDAO(context: Context) {

    private val dbHelper = DBHelper(context.applicationContext)
    private val categoryDAO = CategoryDAO(context)

    fun getTransactions(date: Date): ArrayList<Transaction> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DBHelper.TB_TRANSACTION} WHERE ${DBHelper.COL_DATE} = ? " +
                "ORDER BY ${DBHelper.COL_ID}",
            arrayOf(toText(date))
        )

        val categories = categoryDAO.getCategoryIndex()

        val list = ArrayList<Transaction>()
        while (cursor.moveToNext()) {
            val idCategory = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID_CATEGORY))
            list.add(
                Transaction(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)),
                    date = toDate(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_DATE))),
                    amount = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHelper.COL_AMOUNT)),
                    note = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NOTE)) ?: "",
                    category = categories[idCategory] ?: Category()
                )
            )
        }
        cursor.close()
        return list
    }

    /** Tính tổng theo ngày và kiểu thu/chi. */
    fun total(date: Date, idType: Int): Float {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COALESCE(SUM(t.${DBHelper.COL_AMOUNT}), 0) " +
                "FROM ${DBHelper.TB_TRANSACTION} t " +
                "JOIN ${DBHelper.TB_CATEGORY} c ON c.${DBHelper.COL_ID} = t.${DBHelper.COL_ID_CATEGORY} " +
                "WHERE t.${DBHelper.COL_DATE} = ? AND c.${DBHelper.COL_ID_TYPE} = ?",
            arrayOf(toText(date), idType.toString())
        )
        cursor.moveToFirst()
        val sum = cursor.getFloat(0)
        cursor.close()
        return sum
    }

    /** Trả tổng tiền theo đúng thứ tự các ngày được yêu cầu. */
    fun getTimeStats(dates: List<Date>): Array<TimeStat> {
        val keys = dates.map { toText(it) }.distinct()
        if (keys.isEmpty()) return emptyArray()

        val marks = keys.joinToString(", ") { "?" }
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT t.${DBHelper.COL_DATE}, " +
                "COALESCE(SUM(CASE WHEN c.${DBHelper.COL_ID_TYPE} = ? " +
                "THEN t.${DBHelper.COL_AMOUNT} ELSE 0 END), 0) AS income, " +
                "COALESCE(SUM(CASE WHEN c.${DBHelper.COL_ID_TYPE} = ? " +
                "THEN t.${DBHelper.COL_AMOUNT} ELSE 0 END), 0) AS expense " +
                "FROM ${DBHelper.TB_TRANSACTION} t " +
                "JOIN ${DBHelper.TB_CATEGORY} c " +
                "ON c.${DBHelper.COL_ID} = t.${DBHelper.COL_ID_CATEGORY} " +
                "WHERE t.${DBHelper.COL_DATE} IN ($marks) " +
                "GROUP BY t.${DBHelper.COL_DATE}",
            arrayOf(CategoryType.ID_THU.toString(), CategoryType.ID_CHI.toString(), *keys.toTypedArray())
        )
        val totalsByDate = HashMap<String, TimeStat>()
        while (cursor.moveToNext()) {
            val date = cursor.getString(0)
            totalsByDate[date] = TimeStat(
                name = date,
                totalIn = cursor.getFloat(1),
                totalOut = cursor.getFloat(2)
            )
        }
        cursor.close()

        // Khôi phục thứ tự đầu vào; ngày không có giao dịch nhận tổng bằng 0.
        return dates.map { date ->
            val key = toText(date)
            totalsByDate[key] ?: TimeStat(name = key)
        }.toTypedArray()
    }

    /** Tính tổng theo mục, bao gồm mục chưa có giao dịch. */
    fun getCategoryStats(date: Date): ArrayList<CategoryStat> {
        val categories = categoryDAO.getCategoryIndex()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT c.${DBHelper.COL_ID}, COALESCE(SUM(t.${DBHelper.COL_AMOUNT}), 0) " +
                "FROM ${DBHelper.TB_CATEGORY} c " +
                "LEFT JOIN ${DBHelper.TB_TRANSACTION} t " +
                "ON t.${DBHelper.COL_ID_CATEGORY} = c.${DBHelper.COL_ID} " +
                "AND t.${DBHelper.COL_DATE} = ? " +
                "GROUP BY c.${DBHelper.COL_ID} " +
                "ORDER BY c.${DBHelper.COL_ID}",
            arrayOf(toText(date))
        )

        val stats = ArrayList<CategoryStat>()
        while (cursor.moveToNext()) {
            val category = categories[cursor.getInt(0)] ?: continue
            stats.add(CategoryStat(category, cursor.getFloat(1)))
        }
        cursor.close()
        return stats
    }

    // CSDL bài trước lưu dd/MM/yyyy; đổi sang yyyy-MM-dd trong truy vấn để so sánh
    // đúng thứ tự thời gian, kể cả khi kỳ thống kê đi qua tháng hoặc năm khác.
    private val sqlDate = "substr(t.${DBHelper.COL_DATE}, 7, 4) || '-' || " +
        "substr(t.${DBHelper.COL_DATE}, 4, 2) || '-' || substr(t.${DBHelper.COL_DATE}, 1, 2)"

    /** Các kỳ có dữ liệu, cộng thêm kỳ hiện tại và kỳ đang xem để vẫn chọn được kỳ trống. */
    fun getStatPeriods(byYear: Boolean, selected: StatPeriod): List<StatPeriod> {
        val periods = linkedSetOf<StatPeriod>()
        dbHelper.readableDatabase.rawQuery(
            "SELECT DISTINCT substr(${DBHelper.COL_DATE}, 7, 4), " +
                "substr(${DBHelper.COL_DATE}, 4, 2) FROM ${DBHelper.TB_TRANSACTION}", null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val year = cursor.getString(0).toIntOrNull() ?: continue
                val month = cursor.getString(1).toIntOrNull() ?: continue
                if (year in 1..9999 && month in 1..12) periods.add(StatPeriod(year, if (byYear) 0 else month))
            }
        }
        val current = StatPeriod.current()
        periods.add(StatPeriod(current.year, if (byYear) 0 else current.month))
        periods.add(StatPeriod(selected.year, if (byYear) 0 else selected.month.takeIf { it > 0 } ?: current.month))
        return periods.sortedWith(compareByDescending<StatPeriod> { it.year }.thenByDescending { it.month })
    }

    /** Tổng theo từng mục thực tế; không cộng trùng giao dịch vào cả mục cha và mục con. */
    fun getCategoryStats(period: StatPeriod, idType: Int): ArrayList<CategoryStat> {
        val categories = categoryDAO.getCategoryIndex()
        val stats = ArrayList<CategoryStat>()
        dbHelper.readableDatabase.rawQuery(
            "SELECT c.${DBHelper.COL_ID}, SUM(t.${DBHelper.COL_AMOUNT}) AS total " +
                "FROM ${DBHelper.TB_TRANSACTION} t JOIN ${DBHelper.TB_CATEGORY} c " +
                "ON c.${DBHelper.COL_ID} = t.${DBHelper.COL_ID_CATEGORY} " +
                "WHERE ($sqlDate) >= ? AND ($sqlDate) < ? AND c.${DBHelper.COL_ID_TYPE} = ? " +
                "GROUP BY c.${DBHelper.COL_ID} HAVING total > 0 " +
                "ORDER BY total DESC, c.${DBHelper.COL_NAME} COLLATE NOCASE, c.${DBHelper.COL_ID}",
            arrayOf(period.start, period.end, idType.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                categories[cursor.getInt(0)]?.let { stats.add(CategoryStat(it, cursor.getFloat(1))) }
            }
        }
        return stats
    }

    /** Giao dịch của đúng mục và kỳ đã chọn, xếp thời gian tăng dần rồi theo mã. */
    fun getTransactions(period: StatPeriod, idCategory: Int): ArrayList<Transaction> {
        val category = categoryDAO.getCategoryIndex()[idCategory] ?: return arrayListOf()
        val result = ArrayList<Transaction>()
        dbHelper.readableDatabase.rawQuery(
            "SELECT t.* FROM ${DBHelper.TB_TRANSACTION} t " +
                "WHERE ($sqlDate) >= ? AND ($sqlDate) < ? AND t.${DBHelper.COL_ID_CATEGORY} = ? " +
                "ORDER BY ($sqlDate), t.${DBHelper.COL_ID}",
            arrayOf(period.start, period.end, idCategory.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) result.add(Transaction(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)),
                date = toDate(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_DATE))),
                amount = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHelper.COL_AMOUNT)),
                note = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NOTE)) ?: "",
                category = category
            ))
        }
        return result
    }

    /** Một truy vấn cho toàn bộ 12 tháng; tháng không có giao dịch được bổ sung số 0. */
    fun getMonthlyStats(periods: List<StatPeriod>): List<TimeStat> {
        if (periods.isEmpty()) return emptyList()
        val totals = HashMap<String, TimeStat>()
        dbHelper.readableDatabase.rawQuery(
            "SELECT substr(t.${DBHelper.COL_DATE}, 7, 4) || '-' || substr(t.${DBHelper.COL_DATE}, 4, 2) AS month, " +
                "SUM(CASE WHEN c.${DBHelper.COL_ID_TYPE} = ? THEN t.${DBHelper.COL_AMOUNT} ELSE 0 END), " +
                "SUM(CASE WHEN c.${DBHelper.COL_ID_TYPE} = ? THEN t.${DBHelper.COL_AMOUNT} ELSE 0 END) " +
                "FROM ${DBHelper.TB_TRANSACTION} t JOIN ${DBHelper.TB_CATEGORY} c " +
                "ON c.${DBHelper.COL_ID} = t.${DBHelper.COL_ID_CATEGORY} " +
                "WHERE ($sqlDate) >= ? AND ($sqlDate) < ? GROUP BY month",
            arrayOf(CategoryType.ID_THU.toString(), CategoryType.ID_CHI.toString(),
                periods.minBy { it.start }.start, periods.maxBy { it.end }.end)
        ).use { cursor ->
            while (cursor.moveToNext()) totals[cursor.getString(0)] = TimeStat(
                cursor.getString(0), cursor.getFloat(1), cursor.getFloat(2)
            )
        }
        return periods.map { totals[it.start.substring(0, 7)] ?: TimeStat(it.title) }
    }

    fun addTransaction(t: Transaction): Boolean {
        val db = dbHelper.writableDatabase
        return db.insert(DBHelper.TB_TRANSACTION, null, valuesOf(t)) != -1L
    }

    fun editTransaction(t: Transaction): Boolean {
        val db = dbHelper.writableDatabase
        val rows = db.update(
            DBHelper.TB_TRANSACTION,
            valuesOf(t),
            "${DBHelper.COL_ID} = ?",
            arrayOf(t.id.toString())
        )
        return rows > 0
    }

    fun deleteTransaction(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val rows = db.delete(
            DBHelper.TB_TRANSACTION, "${DBHelper.COL_ID} = ?", arrayOf(id.toString())
        )
        return rows > 0
    }

    private fun valuesOf(t: Transaction) = ContentValues().apply {
        put(DBHelper.COL_DATE, toText(t.date))
        put(DBHelper.COL_AMOUNT, t.amount)
        put(DBHelper.COL_NOTE, t.note)
        put(DBHelper.COL_ID_CATEGORY, t.category.id)
    }

    companion object {
        private fun format() = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun toText(date: Date): String = format().format(date)

        fun toDate(text: String): Date = try {
            format().parse(text) ?: Date()
        } catch (e: java.text.ParseException) {
            Date()
        }
    }
}
