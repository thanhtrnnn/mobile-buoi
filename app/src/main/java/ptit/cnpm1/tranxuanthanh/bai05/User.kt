package ptit.cnpm1.tranxuanthanh.bai05

import java.io.Serializable
import java.util.Date

/**
 * Một người dùng. [id] là khóa chính trong CSDL, bài 4 không có vì lúc đó danh
 * sách chỉ nằm trong bộ nhớ; bài 5 cần nó để UserDAO.delete(id) và edit() biết
 * phải sửa đúng dòng nào.
 */
data class User(
    var id: Int = 0,
    var username: String = "",
    var password: String = "",
    var fullname: String = "",
    var dob: Date = Date()
) : Serializable
