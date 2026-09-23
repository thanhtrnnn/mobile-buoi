package ptit.cnpm1.tranxuanthanh.bai06

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * Adapter cho Spinner chọn mục thu/chi: mỗi dòng có logo + tên mục.
 *
 * Khi xổ danh sách ra thì mục con thụt vào so với cha, cháu thụt vào so với
 * con, để nhìn ra cây như wireframe slide 7. Ô đang chọn (lúc Spinner đóng)
 * thì không thụt lề.
 *
 * Phần tử null là dòng "--- trống ---" của Spinner chọn mục cha, nghĩa là
 * không có cha.
 */
class CategoryAdapter(
    context: Context,
    private val items: List<Category?>
) : ArrayAdapter<Category?>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        bind(items[position], convertView, parent, indent = false)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        bind(items[position], convertView, parent, indent = true)

    private fun bind(c: Category?, convertView: View?, parent: ViewGroup, indent: Boolean): View {
        val row = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.spinnerrow, parent, false)
        val imgIcon = row.findViewById<ImageView>(R.id.imgIcon)
        val lblName = row.findViewById<TextView>(R.id.lblName)

        if (c == null) {
            // INVISIBLE chứ không GONE: vẫn giữ chỗ của logo để ô cao bằng
            // các dòng khác và chữ thẳng hàng với tên mục
            imgIcon.visibility = View.INVISIBLE
            lblName.text = "--- trống ---"
        } else {
            imgIcon.visibility = View.VISIBLE
            imgIcon.setImageResource(Icons.resOf(c.icon))
            lblName.text = c.name
        }

        // Mỗi bậc thụt vào 40dp, cộng với 15dp lề sẵn có của dòng
        val density = context.resources.displayMetrics.density
        val level = if (indent && c != null) c.level() else 0
        row.setPadding(((15 + 40 * level) * density).toInt(), row.paddingTop, row.paddingRight, row.paddingBottom)
        return row
    }
}
