package ptit.cnpm1.tranxuanthanh.bai06

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

/** Adapter cho Spinner chọn logo: mỗi dòng vẽ luôn logo bên cạnh tên của nó. */
class LogoAdapter(context: Context) :
    ArrayAdapter<String>(context, 0, Icons.ALL.keys.toList()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        bind(position, convertView, parent)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        bind(position, convertView, parent)

    private fun bind(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.spinnerrow, parent, false)
        val name = Icons.nameAt(position)
        row.findViewById<ImageView>(R.id.imgIcon).setImageResource(Icons.resOf(name))
        row.findViewById<TextView>(R.id.lblName).text = Icons.ALL[name]
        return row
    }
}
