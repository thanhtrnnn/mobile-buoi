package ptit.cnpm1.tranxuanthanh.bai03

import java.io.Serializable

data class User(
    var username: String = "",
    var password: String = "",
    var fullname: String = ""
) : Serializable
