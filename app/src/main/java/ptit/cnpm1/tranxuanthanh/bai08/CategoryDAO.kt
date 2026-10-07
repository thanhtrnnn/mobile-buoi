package ptit.cnpm1.tranxuanthanh.bai08

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import java.text.Collator
import java.util.Locale

/** Các thao tác cơ sở dữ liệu cho mục và kiểu thu/chi. */
class CategoryDAO(context: Context) {

    private val dbHelper = DBHelper(context.applicationContext)

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

    /** Lập bảng tra mục theo mã để gắn vào giao dịch. */
    internal fun getCategoryIndex(): Map<Int, Category> {
        val all = HashMap<Int, Category>()
        for (type in getTypes()) all.putAll(loadCategories(type.id))
        return all
    }

    // Các mục thu/chi.

    /** Trải phẳng cây mục, sắp xếp theo tần suất nhánh và tên. */
    fun getCategories(idType: Int): ArrayList<Category> {
        val all = loadCategories(idType)
        val usage = countUsage()

        // Cộng lượt dùng cho mục giao dịch và các mục tổ tiên.
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

        // Sắp xếp tên theo bảng chữ cái tiếng Việt.
        val collator = Collator.getInstance(Locale("vi", "VN"))
        val byName = Comparator<Category> { a, b -> collator.compare(a.name, b.name) }
        roots.sortWith(compareByDescending<Category> { branch[it.id] ?: 0 }.then(byName))
        for (children in childrenOf.values) children.sortWith(byName)

        val ordered = ArrayList<Category>()
        for (root in roots) appendBranch(root, childrenOf, ordered)
        return ordered
    }

    /** Thêm mục cha trước, sau đó thêm các mục con cháu. */
    private fun appendBranch(
        category: Category,
        childrenOf: HashMap<Int, ArrayList<Category>>,
        out: ArrayList<Category>
    ) {
        out.add(category)
        childrenOf[category.id]?.forEach { appendBranch(it, childrenOf, out) }
    }

    /** Nạp danh sách và liên kết từng mục với cha của nó. */
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

    /** Đếm số giao dịch của từng mục để sắp xếp. */
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

    fun isDuplicateCategory(c: Category): Boolean {
        val locale = Locale("vi", "VN")
        val key = c.name.trim().lowercase(locale)
        return loadCategories(c.type.id).values.any {
            it.id != c.id &&
                it.parent?.id == c.parent?.id &&
                it.name.trim().lowercase(locale) == key
        }
    }

    /** Thêm một mục thu/chi. */
    fun addCategory(c: Category): Boolean {
        val db = dbHelper.writableDatabase
        return db.insert(DBHelper.TB_CATEGORY, null, valuesOf(c)) != -1L
    }

    /** Sửa mục và chuyển toàn bộ con cháu về cùng kiểu thu/chi. */
    fun editCategory(c: Category): Boolean {
        val db = dbHelper.writableDatabase
        val rows = db.update(
            DBHelper.TB_CATEGORY,
            valuesOf(c),
            "${DBHelper.COL_ID} = ?",
            arrayOf(c.id.toString())
        )
        if (rows == 0) return false

        val branch = branchIds(c.id)
        val marks = branch.joinToString(", ") { "?" }
        val values = ContentValues().apply { put(DBHelper.COL_ID_TYPE, c.type.id) }
        db.update(
            DBHelper.TB_CATEGORY,
            values,
            "${DBHelper.COL_ID} IN ($marks)",
            branch.map { it.toString() }.toTypedArray()
        )
        return true
    }

    /** Lấy mã của mục này và toàn bộ con cháu. */
    private fun branchIds(id: Int): ArrayList<Int> {
        val db = dbHelper.readableDatabase
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
        return branch
    }

    /** Xóa mục, con cháu và các giao dịch liên quan. */
    fun deleteCategory(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val branch = branchIds(id)

        val marks = branch.joinToString(", ") { "?" }
        val args = branch.map { it.toString() }.toTypedArray()
        db.delete(DBHelper.TB_TRANSACTION, "${DBHelper.COL_ID_CATEGORY} IN ($marks)", args)
        val rows = db.delete(DBHelper.TB_CATEGORY, "${DBHelper.COL_ID} IN ($marks)", args)
        return rows > 0
    }

    private fun valuesOf(c: Category) = ContentValues().apply {
        put(DBHelper.COL_NAME, c.name)
        put(DBHelper.COL_ICON, c.icon)
        put(DBHelper.COL_NOTE, c.note)
        put(DBHelper.COL_ID_TYPE, c.type.id)
        val parent = c.parent
        if (parent == null) putNull(DBHelper.COL_ID_PARENT)
        else put(DBHelper.COL_ID_PARENT, parent.id)
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

}
