package ptit.cnpm1.tranxuanthanh.bai07

import java.io.Serializable

/** income or expense category with an optional parent */
open class Category(
    open var id: Int = 0,
    open var name: String = "",
    open var icon: String = Icons.DEFAULT,
    open var note: String = "",
    open var type: CategoryType = CategoryType(),
    open var parent: Category? = null
) : Serializable {

    /** hierarchy depth used to indent spinner rows */
    fun level(): Int {
        var level = 0
        var p = parent
        while (p != null) {
            level++
            p = p.parent
        }
        return level
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is Category && javaClass == other.javaClass &&
            id == other.id && name == other.name && icon == other.icon && note == other.note &&
            type == other.type && parent == other.parent)

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + note.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "Category(id=$id, name=$name, icon=$icon, note=$note, type=$type, parent=$parent)"
}
