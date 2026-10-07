package ptit.cnpm1.tranxuanthanh.bai07

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

/** transaction row with category icon, name, and amount */
class ShowTransFrag @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var imgIcon: ImageView
    private lateinit var lblName: TextView
    private lateinit var lblAmount: TextView

    init {
        onCreate()
    }

    /** inflate the row layout and find its views */
    fun onCreate() {
        LayoutInflater.from(context).inflate(R.layout.showtrans, this, true)
        imgIcon = findViewById(R.id.imgIcon)
        lblName = findViewById(R.id.lblName)
        lblAmount = findViewById(R.id.lblAmount)
    }

    /** bind a transaction and color income green or expense red */
    fun bind(t: Transaction) {
        imgIcon.setImageResource(Icons.resOf(t.category.icon))
        lblName.text = t.category.name
        lblAmount.text = Money.format(t.amount)

        val color = if (t.category.type.id == CategoryType.ID_THU) {
            resources.getColor(R.color.thu)
        } else {
            resources.getColor(R.color.chi)
        }
        lblName.setTextColor(color)
        lblAmount.setTextColor(color)
        imgIcon.setColorFilter(color)
    }
}
