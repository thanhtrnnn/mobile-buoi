package ptit.cnpm1.tranxuanthanh.bai06

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import java.text.Collator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Lớp duy nhất được phép nói chuyện với CSDL. Các màn hình chỉ gọi các hàm ở
 * đây và nhận về [Transaction], [Category], [CategoryType], không đụng tới SQL.
 *
 * Ngày được cất dưới dạng chuỗi "dd/MM/yyyy" cho dễ đọc khi mở file db ra xem;
 * [toText] và [toDate] lo phần đổi qua lại với [Date].
 */
class WalletDAO(context: Context) {

    private val dbHelper = DBHelper(context.applicationContext)

    // ------------------------------------------------------------------ kiểu

    /** Hai kiểu thu và chi, gieo sẵn lúc tạo CSDL. */
    fun getTypes(): ArrayList<CategoryType> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DBHelper.TB_TYPE} ORDER BY ${DBHelper.COL_ID}", null
        )
        val list = ArrayList<CategoryType>()
        while (cursor.moveToNext()) list.add(typeOf(cursor))
        cursor.close()
        return list
    }

    // -------------------------------------------------------------- mục thu/chi

    /**
     * Cây mục thu/chi của một kiểu, đã duỗi thành danh sách theo đúng thứ tự
     * cần hiện (slide 12):
     * - mục cha xếp theo tổng số lần dùng của cả nhánh, nhiều hơn thì đứng
     *   trước; lần đầu chưa ai dùng nên mọi tổng đều bằng 0 và thứ tự rơi về
     *   abc của tên mục cha,
     * - mục con/cháu của cùng một cha thì luôn xếp theo abc,
     * - con nằm ngay dưới cha của nó, cháu nằm ngay dưới con.
     */
    fun getCategories(idType: Int): ArrayList<Category> {
        val all = loadCategories(idType)
        val usage = countUsage()

        // Tổng số lần dùng của một nhánh = của chính nó cộng của mọi con cháu.
        // Cộng dồn bằng cách với mỗi mục, đi ngược lên hết đường cha của nó.
        val branch = HashMap<Int, Int>()
        for (category in all.values) {
            val own = usage[category.id] ?: 0
            if (own == 0) continue
            var node: Category? = category
            while (node != null) {
                branch[node.id] = (branch[node.id] ?: 0) + own
                node = node.parent
            }
        }

        val childrenOf = HashMap<Int, ArrayList<Category>>()
        val roots = ArrayList<Category>()
        for (category in all.values) {
            val parent = category.parent
            if (parent == null) {
                roots.add(category)
            } else {
                childrenOf.getOrPut(parent.id) { ArrayList() }.add(category)
            }
        }

        // So tên bằng Collator tiếng Việt, nếu so chuỗi thô thì "Ăn uống" bị
        // xếp sau "Thuê nhà" vì chữ Ă có mã lớn hơn chữ T
        val collator = Collator.getInstance(Locale("vi", "VN"))
        val byName = Comparator<Category> { a, b -> collator.compare(a.name, b.name) }
        roots.sortWith(compareByDescending<Category> { branch[it.id] ?: 0 }.then(byName))
        for (children in childrenOf.values) children.sortWith(byName)

        val ordered = ArrayList<Category>()
        for (root in roots) appendBranch(root, childrenOf, ordered)
        return ordered
    }

    /** Thêm mục cha vào danh sách rồi tới lượt các con cháu của nó. */
    private fun appendBranch(
        category: Category,
        childrenOf: HashMap<Int, ArrayList<Category>>,
        out: ArrayList<Category>
    ) {
        out.add(category)
        childrenOf[category.id]?.forEach { appendBranch(it, childrenOf, out) }
    }

    /** Đọc mọi mục của một kiểu và nối con về đúng đối tượng cha của nó. */
    private fun loadCategories(idType: Int): LinkedHashMap<Int, Category> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DBHelper.TB_CATEGORY} WHERE ${DBHelper.COL_ID_TYPE} = ?",
            arrayOf(idType.toString())
        )

        val all = LinkedHashMap<Int, Category>()
        val idParentOf = HashMap<Int, Int>()
        val type = getTypes().firstOrNull { it.id == idType } ?: CategoryType(idType, "", "")
        while (cursor.moveToNext()) {
            val category = categoryOf(cursor, type)
            all[category.id] = category

            val indexParent = cursor.getColumnIndexOrThrow(DBHelper.COL_ID_PARENT)
            if (!cursor.isNull(indexParent)) {
                idParentOf[category.id] = cursor.getInt(indexParent)
            }
        }
        cursor.close()

        for ((id, idParent) in idParentOf) all[id]?.parent = all[idParent]
        return all
    }

    /** Số giao dịch đã dùng từng mục, dùng để xếp mục cha theo tần suất. */
    private fun countUsage(): HashMap<Int, Int> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT ${DBHelper.COL_ID_CATEGORY}, COUNT(*) FROM ${DBHelper.TB_TRANSACTION} " +
                "GROUP BY ${DBHelper.COL_ID_CATEGORY}",
            null
        )
        val usage = HashMap<Int, Int>()
        while (cursor.moveToNext()) usage[cursor.getInt(0)] = cursor.getInt(1)
        cursor.close()
        return usage
    }

    /** Thêm một mục thu/chi. Trả về false nếu không ghi được. */
    fun addCategory(c: Category): Boolean {
        val db = dbHelper.writableDatabase
        return db.insert(DBHelper.TB_CATEGORY, null, valuesOf(c)) != -1L
    }

    /** Sửa một mục theo [Category.id]. Trả về false nếu không có dòng nào đổi. */
    fun editCategory(c: Category): Boolean {
        val db = dbHelper.writableDatabase
        val rows = db.update(
            DBHelper.TB_CATEGORY,
            valuesOf(c),
            "${DBHelper.COL_ID} = ?",
            arrayOf(c.id.toString())
        )
        return rows > 0
    }

    /**
     * Xóa một mục. Xóa cả con cháu của nó và mọi giao dịch đã dùng những mục
     * đó, nếu không sẽ còn lại giao dịch trỏ vào mục không tồn tại.
     */
    fun deleteCategory(id: Int): Boolean {
        val db = dbHelper.writableDatabase

        // Gom id của cả nhánh: bắt đầu từ chính nó rồi lần xuống các đời con
        val branch = ArrayList<Int>()
        var frontier = listOf(id)
        while (frontier.isNotEmpty()) {
            branch.addAll(frontier)
            val next = ArrayList<Int>()
            for (parentId in frontier) {
                val cursor = db.rawQuery(
                    "SELECT ${DBHelper.COL_ID} FROM ${DBHelper.TB_CATEGORY} " +
                        "WHERE ${DBHelper.COL_ID_PARENT} = ?",
                    arrayOf(parentId.toString())
                )
                while (cursor.moveToNext()) next.add(cursor.getInt(0))
                cursor.close()
            }
            frontier = next
        }

        val marks = branch.joinToString(", ") { "?" }
        val args = branch.map { it.toString() }.toTypedArray()
        db.delete(DBHelper.TB_TRANSACTION, "${DBHelper.COL_ID_CATEGORY} IN ($marks)", args)
        val rows = db.delete(DBHelper.TB_CATEGORY, "${DBHelper.COL_ID} IN ($marks)", args)
        return rows > 0
    }

    // ----------------------------------------------------------- giao dịch

    /** Mọi giao dịch trong một ngày, mục nào thêm trước thì hiện trước. */
    fun getTransactions(date: Date): ArrayList<Transaction> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DBHelper.TB_TRANSACTION} WHERE ${DBHelper.COL_DATE} = ? " +
                "ORDER BY ${DBHelper.COL_ID}",
            arrayOf(toText(date))
        )

        val categories = HashMap<Int, Category>()
        for (type in getTypes()) categories.putAll(loadCategories(type.id))

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

    /** Tổng thu (hoặc tổng chi) của một ngày, hiện ở đầu trang home. */
    fun total(date: Date, idType: Int): Float {
        var sum = 0f
        for (t in getTransactions(date)) {
            if (t.category.type.id == idType) sum += t.amount
        }
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

    // -------------------------------------------------------------- đóng gói

    private fun valuesOf(c: Category) = ContentValues().apply {
        put(DBHelper.COL_NAME, c.name)
        put(DBHelper.COL_ICON, c.icon)
        put(DBHelper.COL_NOTE, c.note)
        put(DBHelper.COL_ID_TYPE, c.type.id)
        val parent = c.parent
        if (parent == null) putNull(DBHelper.COL_ID_PARENT)
        else put(DBHelper.COL_ID_PARENT, parent.id)
    }

    private fun valuesOf(t: Transaction) = ContentValues().apply {
        put(DBHelper.COL_DATE, toText(t.date))
        put(DBHelper.COL_AMOUNT, t.amount)
        put(DBHelper.COL_NOTE, t.note)
        put(DBHelper.COL_ID_CATEGORY, t.category.id)
    }

    private fun typeOf(c: Cursor) = CategoryType(
        id = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ID)),
        name = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_NAME)),
        note = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_NOTE)) ?: ""
    )

    private fun categoryOf(c: Cursor, type: CategoryType) = Category(
        id = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ID)),
        name = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_NAME)),
        icon = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_ICON)),
        note = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_NOTE)) ?: "",
        type = type,
        parent = null
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
