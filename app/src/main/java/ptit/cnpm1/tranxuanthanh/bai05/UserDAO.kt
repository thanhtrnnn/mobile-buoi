package ptit.cnpm1.tranxuanthanh.bai05

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Lớp duy nhất được phép nói chuyện với CSDL. Các màn hình chỉ gọi 6 hàm dưới
 * đây (đúng như UML slide 7) và nhận về đối tượng [User], không đụng tới SQL.
 *
 * Ngày sinh được cất trong CSDL dưới dạng chuỗi "dd/MM/yyyy" cho dễ đọc khi mở
 * file db ra xem; [toText] và [toDate] lo phần đổi qua lại với [Date].
 */
class UserDAO(context: Context) {

    private val dbHelper = DBHelper(context.applicationContext)

    /** Thêm một người dùng mới. Trả về false nếu username đã có người dùng. */
    fun add(u: User): Boolean {
        val db = dbHelper.writableDatabase
        // insertWithOnConflict mặc định trả về -1 khi vi phạm ràng buộc UNIQUE
        val id = db.insert(DBHelper.TABLE, null, valuesOf(u))
        return id != -1L
    }

    /**
     * Cập nhật người dùng theo [User.id]. Trả về false nếu không sửa được dòng
     * nào — hoặc vì không có id đó, hoặc vì username mới trùng người khác
     * (CONFLICT_IGNORE để câu update hỏng lặng lẽ thay vì ném lỗi).
     */
    fun edit(u: User): Boolean {
        val db = dbHelper.writableDatabase
        val rows = db.updateWithOnConflict(
            DBHelper.TABLE,
            valuesOf(u),
            "${DBHelper.COL_ID} = ?",
            arrayOf(u.id.toString()),
            SQLiteDatabase.CONFLICT_IGNORE
        )
        return rows > 0
    }

    /** Xóa người dùng theo id. Trả về false nếu không có dòng nào bị xóa. */
    fun delete(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val rows = db.delete(DBHelper.TABLE, "${DBHelper.COL_ID} = ?", arrayOf(id.toString()))
        return rows > 0
    }

    /**
     * Xóa mọi người dùng có username hoặc họ tên **chứa** [key].
     * Trả về số dòng đã xóa, 0 nếu không ai khớp.
     *
     * Hai chỗ phải cẩn thận:
     * - Chuỗi rỗng sẽ thành `LIKE '%%'`, khớp mọi dòng và quét sạch bảng, nên
     *   chặn ngay từ đầu.
     * - Trong LIKE thì `%` và `_` là ký tự đại diện, nên người dùng gõ đúng hai
     *   ký tự đó phải được thoát bằng `ESCAPE`, nếu không "a_b" sẽ khớp cả
     *   "axb".
     */
    fun deleteByKeyword(key: String): Int {
        if (key.isEmpty()) return 0

        val escaped = key.replace("!", "!!").replace("%", "!%").replace("_", "!_")
        val like = "%$escaped%"

        val db = dbHelper.writableDatabase
        return db.delete(
            DBHelper.TABLE,
            "${DBHelper.COL_USERNAME} LIKE ? ESCAPE '!' " +
                "OR ${DBHelper.COL_FULLNAME} LIKE ? ESCAPE '!'",
            arrayOf(like, like)
        )
    }

    /** Tìm theo username hoặc họ tên có chứa [key]. */
    fun search(key: String): ArrayList<User> {
        val like = "%$key%"
        return query(
            "SELECT * FROM ${DBHelper.TABLE} " +
                "WHERE ${DBHelper.COL_USERNAME} LIKE ? OR ${DBHelper.COL_FULLNAME} LIKE ? " +
                "ORDER BY ${DBHelper.COL_ID}",
            arrayOf(like, like)
        )
    }

    /** Toàn bộ danh sách người dùng, dùng để đổ vào ListView ở màn Home. */
    fun getAll(): ArrayList<User> =
        query("SELECT * FROM ${DBHelper.TABLE} ORDER BY ${DBHelper.COL_ID}", null)

    /** Đúng username và password thì trả về true. */
    fun checkLogin(u: User): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT ${DBHelper.COL_ID} FROM ${DBHelper.TABLE} " +
                "WHERE ${DBHelper.COL_USERNAME} = ? AND ${DBHelper.COL_PASSWORD} = ?",
            arrayOf(u.username, u.password)
        )
        val found = cursor.moveToFirst()
        cursor.close()
        return found
    }

    /** Chạy một câu SELECT và đóng gói kết quả thành danh sách [User]. */
    private fun query(sql: String, args: Array<String>?): ArrayList<User> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(sql, args)
        val list = ArrayList<User>()
        while (cursor.moveToNext()) {
            list.add(userOf(cursor))
        }
        cursor.close()
        return list
    }

    private fun valuesOf(u: User) = ContentValues().apply {
        put(DBHelper.COL_USERNAME, u.username)
        put(DBHelper.COL_PASSWORD, u.password)
        put(DBHelper.COL_FULLNAME, u.fullname)
        put(DBHelper.COL_DOB, toText(u.dob))
    }

    private fun userOf(c: Cursor) = User(
        id = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ID)),
        username = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_USERNAME)),
        password = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PASSWORD)),
        fullname = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_FULLNAME)),
        dob = toDate(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_DOB)))
    )

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
