package ptit.cnpm1.tranxuanthanh.bai08

import java.io.Serializable
import java.util.Date

/** Giao dịch xác định thu/chi thông qua mục được chọn. */
data class Transaction(
    var id: Int = 0,
    var date: Date = Date(),
    var amount: Float = 0f,
    var note: String = "",
    var category: Category = Category()
) : Serializable
