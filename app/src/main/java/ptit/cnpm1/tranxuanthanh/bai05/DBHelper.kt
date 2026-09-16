package ptit.cnpm1.tranxuanthanh.bai05

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Lớp lo phần "cái file CSDL SQLite nằm ở đâu, bên trong có bảng gì".
 *
 * Android tự gọi [onCreate] đúng một lần, lúc mở CSDL mà file chưa tồn tại.
 * Mọi câu lệnh đọc/ghi dữ liệu nằm ở lớp [UserDAO], không nằm ở đây.
 */
class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "quanlind.db"
        private const val DB_VERSION = 1

        const val TABLE = "user"
        const val COL_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"
        const val COL_FULLNAME = "fullname"
        const val COL_DOB = "dob"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // username là UNIQUE: nhờ vậy insert một username đã có sẽ tự hỏng và
        // UserDAO.add() trả về false, không cần tự đi dò danh sách nữa.
        db.execSQL(
            "CREATE TABLE $TABLE (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_USERNAME TEXT NOT NULL UNIQUE, " +
                "$COL_PASSWORD TEXT NOT NULL, " +
                "$COL_FULLNAME TEXT NOT NULL, " +
                "$COL_DOB TEXT NOT NULL)"
        )

        // Tài khoản đăng nhập (UN = mã SV, PW = ngày sinh 8 số) và vài người
        // dùng mẫu, để lần chạy đầu tiên đã có dữ liệu như wireframe slide 5.
        insertSeed(db, "B23DCAT280", "20082005", "Trần Xuân Thành", "20/08/2005")
        insertSeed(db, "nd1", "12345678", "Nguyễn B", "12/03/2005")
        insertSeed(db, "nd2", "12345678", "Trần C", "02/11/2004")
        insertSeed(db, "nd3", "12345678", "Lê Thị D", "25/07/2005")
        insertSeed(db, "nd4", "12345678", "Hoàng E", "08/01/2003")
    }

    private fun insertSeed(
        db: SQLiteDatabase,
        username: String,
        password: String,
        fullname: String,
        dob: String
    ) {
        db.execSQL(
            "INSERT INTO $TABLE ($COL_USERNAME, $COL_PASSWORD, $COL_FULLNAME, $COL_DOB) " +
                "VALUES (?, ?, ?, ?)",
            arrayOf(username, password, fullname, dob)
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }
}
