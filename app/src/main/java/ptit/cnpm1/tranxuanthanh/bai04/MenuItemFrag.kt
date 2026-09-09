package ptit.cnpm1.tranxuanthanh.bai04

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow

/**
 * Menu xổ ra khi bấm ••• trên một dòng người dùng: hai lựa chọn Sửa / Xóa.
 *
 * Tự bọc mình trong một PopupWindow neo dưới dấu ••• vừa bấm, nên UserhomeAct
 * chỉ cần nói "hiện menu ở đây, sửa thì làm gì, xóa thì làm gì".
 */
class MenuItemFrag @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val btnEdit: Button
    private val btnDel: Button

    private var popup: PopupWindow? = null
    private var onEdit: (() -> Unit)? = null
    private var onDelete: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.menuitem, this, true)
        btnEdit = findViewById(R.id.btnEdit)
        btnDel = findViewById(R.id.btnDel)

        btnEdit.setOnClickListener(this)
        btnDel.setOnClickListener(this)
    }

    /** Xổ menu ngay dưới [anchor] — chính là dấu ••• của dòng đó. */
    fun showAt(anchor: View, onEdit: () -> Unit, onDelete: () -> Unit) {
        this.onEdit = onEdit
        this.onDelete = onDelete

        popup = PopupWindow(
            this,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply { showAsDropDown(anchor) }
    }

    override fun onClick(v: View?) {
        // Đóng menu trước rồi mới chạy hành động, để dialog xác nhận xóa
        // không bị menu che mất
        popup?.dismiss()
        when (v?.id) {
            R.id.btnEdit -> onEdit?.invoke()
            R.id.btnDel -> onDelete?.invoke()
        }
    }
}
