package ptit.cnpm1.tranxuanthanh.bai06

import java.io.Serializable
import java.util.Date

/**
 * Một giao dịch thu hoặc chi (slide 9). Thu hay chi là do [category] quyết
 * định, bản thân giao dịch không giữ kiểu riêng.
 */
data class Transaction(
    var id: Int = 0,
    var date: Date = Date(),
    var amount: Float = 0f,
    var note: String = "",
    var category: Category = Category()
) : Serializable
