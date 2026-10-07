package ptit.cnpm1.tranxuanthanh.bai07

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

/** category spinner rows with indented children */
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
            // keep the icon space so labels stay aligned
            imgIcon.visibility = View.INVISIBLE
            lblName.text = "--- trống ---"
        } else {
            imgIcon.visibility = View.VISIBLE
            imgIcon.setImageResource(Icons.resOf(c.icon))
            // match income and expense colors on home
            imgIcon.setColorFilter(Icons.colorOf(context, c.type.id))
            lblName.text = c.name
        }

        // indent each child level by 40dp
        val level = if (indent && c != null) c.level() else 0
        val baseIndent = context.resources.getDimension(R.dimen.spinner_row_padding_horizontal)
        val indentStep = context.resources.getDimension(R.dimen.spinner_indent_step)
        row.setPadding(
            (baseIndent + indentStep * level).toInt(),
            row.paddingTop,
            row.paddingRight,
            row.paddingBottom
        )
        return row
    }
}
