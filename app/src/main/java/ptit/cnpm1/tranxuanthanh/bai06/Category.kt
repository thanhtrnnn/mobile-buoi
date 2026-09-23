package ptit.cnpm1.tranxuanthanh.bai06

import java.io.Serializable

/**
 * Một mục thu/chi, ví dụ "Sinh hoạt", "Ăn uống", "Học phí" (slide 9).
 *
 * [parent] để null nghĩa là mục cha ngang hàng các mục cha đã có; mục con thì
 * trỏ về cha của nó, nhờ vậy danh sách mục xếp được thành cây cha - con - cháu
 * như wireframe slide 7.
 */
data class Category(
    var id: Int = 0,
    var name: String = "",
    var icon: String = Icons.DEFAULT,
    var note: String = "",
    var type: CategoryType = CategoryType(),
    var parent: Category? = null
) : Serializable {

    /** Mục cha ở bậc 0, con bậc 1, cháu bậc 2 — dùng để thụt lề khi hiển thị. */
    fun level(): Int {
        var level = 0
        var p = parent
        while (p != null) {
            level++
            p = p.parent
        }
        return level
    }

    override fun toString(): String = "    ".repeat(level()) + name
}
