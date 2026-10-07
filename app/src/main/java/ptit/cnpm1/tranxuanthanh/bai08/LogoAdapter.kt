package ptit.cnpm1.tranxuanthanh.bai08

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

/** Tô màu biểu tượng trong Spinner theo kiểu thu/chi. */
class LogoAdapter(context: Context) :
    ArrayAdapter<String>(context, 0, Icons.ALL.keys.toList()) {

    var idType = CategoryType.ID_CHI

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        bind(position, convertView, parent)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        bind(position, convertView, parent)

    private fun bind(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.spinnerrow, parent, false)
        val name = Icons.nameAt(position)
        val imgIcon = row.findViewById<ImageView>(R.id.imgIcon)
        imgIcon.setImageResource(Icons.resOf(name))
        imgIcon.setColorFilter(Icons.colorOf(context, idType))
        row.findViewById<TextView>(R.id.lblName).text = Icons.ALL[name]
        return row
    }
}
