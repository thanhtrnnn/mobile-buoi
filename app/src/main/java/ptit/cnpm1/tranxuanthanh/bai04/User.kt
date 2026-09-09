package ptit.cnpm1.tranxuanthanh.bai04

import java.io.Serializable
import java.util.Date

data class User(
    var username: String = "",
    var password: String = "",
    var fullname: String = "",
    var dob: Date = Date()
) : Serializable
