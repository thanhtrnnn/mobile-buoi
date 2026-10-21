package ptit.cnpm1.tranxuanthanh.bai09p1

import android.content.ContentValues
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Thêm, đọc, sửa, xóa giao dịch và tính tổng thu/chi của một ngày. */
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
