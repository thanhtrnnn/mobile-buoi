package ptit.cnpm1.tranxuanthanh.bai06

import java.io.Serializable

/**
 * Kiểu của một mục: thu hoặc chi (bảng tblType, slide 10).
 *
 * Chỉ có đúng hai dòng, do [DBHelper] gieo sẵn lúc tạo CSDL, người dùng không
 * thêm bớt được.
 */
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
