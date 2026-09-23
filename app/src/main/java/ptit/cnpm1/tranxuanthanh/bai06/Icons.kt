package ptit.cnpm1.tranxuanthanh.bai06

/**
 * Danh sách logo cho mục thu/chi. CSDL chỉ cất tên logo dưới dạng chuỗi (cột
 * icon), lớp này lo phần đổi chuỗi đó sang ảnh vẽ trong res/drawable.
 */
object Icons {

    const val DEFAULT = "khac"

    /** Tên cất trong CSDL -> chữ hiện trong Spinner chọn logo. */
    val ALL = linkedMapOf(
        "anuong" to "Ăn uống",
        "nha" to "Nhà cửa",
        "hoc" to "Học tập",
        "giaitri" to "Giải trí",
        "tien" to "Tiền",
        DEFAULT to "Khác"
    )

    fun labelOf(name: String): String = ALL[name] ?: ALL.getValue(DEFAULT)

    fun nameAt(position: Int): String = ALL.keys.toList()[position]

    fun positionOf(name: String): Int {
        val index = ALL.keys.indexOf(name)
        return if (index >= 0) index else ALL.keys.indexOf(DEFAULT)
    }

    fun resOf(name: String): Int = when (name) {
        "anuong" -> R.drawable.ic_anuong
        "nha" -> R.drawable.ic_nha
        "hoc" -> R.drawable.ic_hoc
        "giaitri" -> R.drawable.ic_giaitri
        "tien" -> R.drawable.ic_tien
        else -> R.drawable.ic_khac
    }
}
