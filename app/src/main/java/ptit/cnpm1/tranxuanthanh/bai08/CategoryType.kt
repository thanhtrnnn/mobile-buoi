package ptit.cnpm1.tranxuanthanh.bai08

import java.io.Serializable

/** Kiểu thu/chi được tạo sẵn trong cơ sở dữ liệu. */
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
