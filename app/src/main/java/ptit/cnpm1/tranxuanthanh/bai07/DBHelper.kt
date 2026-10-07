package ptit.cnpm1.tranxuanthanh.bai07

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** opens the sqlite database and creates its tables */
class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "mywallet.db"
        // bump the version to recreate tables and reseed categories
        private const val DB_VERSION = 2

        const val TB_TYPE = "tblType"
        const val TB_CATEGORY = "tblCategory"
        const val TB_TRANSACTION = "tblTransaction"

        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_NOTE = "note"
        const val COL_ICON = "icon"
        const val COL_ID_PARENT = "idParent"
        const val COL_ID_TYPE = "idType"
        const val COL_DATE = "date"
        const val COL_AMOUNT = "amount"
        const val COL_ID_CATEGORY = "idCategory"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TB_TYPE (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_NAME TEXT NOT NULL, " +
                "$COL_NOTE TEXT)"
        )

        // null parent marks a root category
        db.execSQL(
            "CREATE TABLE $TB_CATEGORY (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_NAME TEXT NOT NULL, " +
                "$COL_ICON TEXT NOT NULL, " +
                "$COL_NOTE TEXT, " +
                "$COL_ID_PARENT INTEGER, " +
                "$COL_ID_TYPE INTEGER NOT NULL)"
        )

        db.execSQL(
            "CREATE TABLE $TB_TRANSACTION (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_DATE TEXT NOT NULL, " +
                "$COL_AMOUNT REAL NOT NULL, " +
                "$COL_NOTE TEXT, " +
                "$COL_ID_CATEGORY INTEGER NOT NULL)"
        )

        seed(db)
    }

    /** seed income and expense types with starter categories */
    private fun seed(db: SQLiteDatabase) {
        db.execSQL(
            "INSERT INTO $TB_TYPE ($COL_ID, $COL_NAME, $COL_NOTE) VALUES (?, ?, ?)",
            arrayOf(CategoryType.ID_THU.toString(), "Thu", "")
        )
        db.execSQL(
            "INSERT INTO $TB_TYPE ($COL_ID, $COL_NAME, $COL_NOTE) VALUES (?, ?, ?)",
            arrayOf(CategoryType.ID_CHI.toString(), "Chi", "")
        )

        val chi = listOf(
            "Ăn uống" to "anuong",
            "Chợ & Siêu thị" to "cho",
            "Mua sắm" to "muasam",
            "Di chuyển" to "xe",
            "Nhà cửa" to "nha",
            "Hóa đơn" to "hoadon",
            "Giải trí" to "giaitri",
            "Sức khỏe" to "suckhoe",
            "Giáo dục" to "hoc",
            "Làm đẹp" to "lamdep",
            "Người thân & Bạn bè" to "nguoi",
            "Du lịch" to "dulich",
            "Trả nợ" to "the",
            "Dịch vụ định kỳ" to "dinhky",
            "Khác" to "khac"
        )
        for ((name, icon) in chi) insertCategory(db, name, icon, null, CategoryType.ID_CHI)

        val thu = listOf(
            "Lương" to "tien",
            "Thưởng" to "sao",
            "Làm thêm / Freelance" to "laptop",
            "Kinh doanh" to "cap",
            "Đầu tư" to "bieudo",
            "Lãi / Tiền gửi" to "nganhang",
            "Quà tặng / Được cho" to "qua",
            "Khác" to "khac"
        )
        for ((name, icon) in thu) insertCategory(db, name, icon, null, CategoryType.ID_THU)
    }

    /** insert a category and return its id */
    private fun insertCategory(
        db: SQLiteDatabase,
        name: String,
        icon: String,
        idParent: Long?,
        idType: Int
    ): Long {
        db.execSQL(
            "INSERT INTO $TB_CATEGORY " +
                "($COL_NAME, $COL_ICON, $COL_NOTE, $COL_ID_PARENT, $COL_ID_TYPE) " +
                "VALUES (?, ?, ?, ?, ?)",
            arrayOf(name, icon, "", idParent?.toString(), idType.toString())
        )

        val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
        cursor.moveToFirst()
        val id = cursor.getLong(0)
        cursor.close()
        return id
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TB_TRANSACTION")
        db.execSQL("DROP TABLE IF EXISTS $TB_CATEGORY")
        db.execSQL("DROP TABLE IF EXISTS $TB_TYPE")
        onCreate(db)
    }
}
