package ptit.cnpm1.tranxuanthanh.bai07

import java.io.Serializable
import java.util.Date

/** transaction whose category determines income or expense */
data class Transaction(
    var id: Int = 0,
    var date: Date = Date(),
    var amount: Float = 0f,
    var note: String = "",
    var category: Category = Category()
) : Serializable
