package ptit.cnpm1.tranxuanthanh.bai06

import android.content.Context

/**
 * Danh sách logo cho mục thu/chi. CSDL chỉ cất tên logo dưới dạng chuỗi (cột
 * icon), lớp này lo phần đổi chuỗi đó sang ảnh vẽ trong res/drawable.
 */
object Icons {

    const val DEFAULT = "khac"

    /** Tên cất trong CSDL -> chữ hiện trong Spinner chọn logo. */
    val ALL = linkedMapOf(
        "anuong" to "Dao dĩa",
        "cho" to "Giỏ hàng",
        "muasam" to "Túi mua sắm",
        "xe" to "Xe",
        "nha" to "Ngôi nhà",
        "hoadon" to "Hóa đơn",
        "giaitri" to "Tivi",
        "suckhoe" to "Chữ thập y tế",
        "hoc" to "Mũ tốt nghiệp",
        "lamdep" to "Thỏi son",
        "nguoi" to "Hai người",
        "dulich" to "Máy bay giấy",
        "the" to "Thẻ",
        "dinhky" to "Lặp lại",
        "tien" to "Tờ tiền",
        "sao" to "Ngôi sao",
        "laptop" to "Máy tính",
        "cap" to "Cặp công tác",
        "bieudo" to "Biểu đồ tăng",
        "nganhang" to "Ngân hàng",
        "qua" to "Hộp quà",
        DEFAULT to "Khác"
    )

    fun nameAt(position: Int): String = ALL.keys.toList()[position]

    fun positionOf(name: String): Int {
        val index = ALL.keys.indexOf(name)
        return if (index >= 0) index else ALL.keys.indexOf(DEFAULT)
    }

    /** Màu tô logo: mục thu xanh, mục chi đỏ. */
    fun colorOf(context: Context, idType: Int): Int =
        context.getColor(if (idType == CategoryType.ID_THU) R.color.thu else R.color.chi)

    fun resOf(name: String): Int = when (name) {
        "anuong" -> R.drawable.ic_anuong
        "cho" -> R.drawable.ic_cho
        "muasam" -> R.drawable.ic_muasam
        "xe" -> R.drawable.ic_xe
        "nha" -> R.drawable.ic_nha
        "hoadon" -> R.drawable.ic_hoadon
        "giaitri" -> R.drawable.ic_giaitri
        "suckhoe" -> R.drawable.ic_suckhoe
        "hoc" -> R.drawable.ic_hoc
        "lamdep" -> R.drawable.ic_lamdep
        "nguoi" -> R.drawable.ic_nguoi
        "dulich" -> R.drawable.ic_dulich
        "the" -> R.drawable.ic_the
        "dinhky" -> R.drawable.ic_dinhky
        "tien" -> R.drawable.ic_tien
        "sao" -> R.drawable.ic_sao
        "laptop" -> R.drawable.ic_laptop
        "cap" -> R.drawable.ic_cap
        "bieudo" -> R.drawable.ic_bieudo
        "nganhang" -> R.drawable.ic_nganhang
        "qua" -> R.drawable.ic_qua
        else -> R.drawable.ic_khac
    }
}
