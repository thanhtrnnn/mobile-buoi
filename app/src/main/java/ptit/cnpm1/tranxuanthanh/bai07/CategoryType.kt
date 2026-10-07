package ptit.cnpm1.tranxuanthanh.bai07

import java.io.Serializable

/** income or expense type seeded by the database */
data class CategoryType(
    var id: Int = 0,
    var name: String = "",
    var note: String = ""
) : Serializable {

    companion object {
        const val ID_THU = 1
        const val ID_CHI = 2
    }

    override fun toString(): String = name
}
