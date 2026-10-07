package ptit.cnpm1.tranxuanthanh.bai07

import java.io.Serializable

/** income and expense totals for a day or period */
data class TimeStat(
    val name: String = "",
    val totalIn: Float = 0f,
    val totalOut: Float = 0f
) : Serializable
