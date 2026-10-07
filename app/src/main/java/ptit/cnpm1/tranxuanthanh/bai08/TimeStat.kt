package ptit.cnpm1.tranxuanthanh.bai08

import java.io.Serializable

/** Tổng thu và tổng chi của một ngày hoặc một kỳ. */
data class TimeStat(
    val name: String = "",
    val totalIn: Float = 0f,
    val totalOut: Float = 0f
) : Serializable
