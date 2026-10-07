package ptit.cnpm1.tranxuanthanh.bai08

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ListView
import android.widget.TextView
import java.util.Calendar
import java.util.Date

/** Trang chủ gồm lịch tuần, giao dịch trong ngày và menu thống kê. */
class HomeAct : Activity(), View.OnClickListener {

    private lateinit var lblDate: TextView
    private lateinit var lblTotalThu: TextView
    private lateinit var lblTotalChi: TextView
    private lateinit var weekDays: LinearLayout
    private lateinit var lwTrans: ListView
    private lateinit var btnAdd: Button
    private lateinit var btnMonth: Button
    private lateinit var btnBack: ImageButton
    private var monthDayDetail = false

    private var selectedDate = Date()

    private val listTrans = ArrayList<Transaction>()
    private lateinit var adapter: ArrayAdapter<Transaction>
    private lateinit var transactionDAO: TransactionDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        lblDate = findViewById(R.id.lblDate)
        lblTotalThu = findViewById(R.id.lblTotalThu)
        lblTotalChi = findViewById(R.id.lblTotalChi)
        weekDays = findViewById(R.id.weekDays)
        lwTrans = findViewById(R.id.lwTrans)
        btnAdd = findViewById(R.id.btnAdd)
        btnMonth = findViewById(R.id.btnMonth)
        btnBack = findViewById(R.id.btnBack)

        monthDayDetail = intent.getBooleanExtra(EXTRA_MONTH_DAY, false)
        if (monthDayDetail) {
            selectedDate = Date(intent.getLongExtra(EXTRA_DATE, Date().time))
            btnMonth.visibility = View.INVISIBLE
            btnBack.visibility = View.VISIBLE
            btnAdd.visibility = View.GONE
            val root = findViewById<ConstraintLayout>(R.id.homeRoot)
            ConstraintSet().apply {
                clone(root)
                clear(lblDate.id, ConstraintSet.END)
                connect(lblDate.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                clear(lblDate.id, ConstraintSet.TOP)
                clear(lblDate.id, ConstraintSet.BOTTOM)
                connect(lblDate.id, ConstraintSet.TOP, btnBack.id, ConstraintSet.TOP)
                connect(lblDate.id, ConstraintSet.BOTTOM, btnBack.id, ConstraintSet.BOTTOM)
                applyTo(root)
            }
        }

        transactionDAO = TransactionDAO(this)
        adapter = object : ArrayAdapter<Transaction>(this, 0, listTrans) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = convertView as? ShowTransFrag ?: ShowTransFrag(this@HomeAct)
                row.bind(listTrans[position])
                return row
            }
        }
        lwTrans.adapter = adapter
        lwTrans.setOnItemClickListener { _, _, position, _ -> editTransaction(listTrans[position]) }

        btnAdd.setOnClickListener(this)
        btnMonth.setOnClickListener(this)
        btnBack.setOnClickListener(this)
        val btnStats = findViewById<Button>(R.id.btnStats)
        btnStats.visibility = if (monthDayDetail) View.GONE else View.VISIBLE
        btnStats.setOnClickListener {
            // Hai lựa chọn thống kê đúng theo menu ở slide 23.
            PopupMenu(this, btnStats).apply {
                menu.add(0, 1, 0, "Thống kê theo tỉ lệ")
                menu.add(0, 2, 1, "Thống kê theo quá trình")
                setOnMenuItemClickListener { item ->
                    val screen = if (item.itemId == 1) RatioStatAct::class.java else ProcessStatAct::class.java
                    val calendar = Calendar.getInstance().apply { time = selectedDate }
                    val period = StatPeriod(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
                    startActivity(period.putInto(Intent(this@HomeAct, screen)))
                    true
                }
                show()
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnAdd -> startActivity(
                Intent(this, AddAct::class.java)
                    .putExtra(AddAct.EXTRA_INITIAL_DATE, selectedDate.time)
            )
            R.id.btnBack -> returnToMonth()
            R.id.btnMonth -> {
                val intent = Intent(this, MonthAct::class.java)
                    .putExtra(MonthAct.EXTRA_DATE, selectedDate.time)
                startActivityForResult(intent, REQUEST_MONTH)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MONTH && resultCode == RESULT_OK && data != null) {
            selectedDate = Date(data.getLongExtra(MonthAct.EXTRA_SELECTED_DATE, selectedDate.time))
            reload()
        }
    }

    private fun editTransaction(transaction: Transaction) {
        startActivity(Intent(this, EditAct::class.java).putExtra("transaction", transaction))
    }

    private fun reload() {
        renderWeek()
        listTrans.clear()
        listTrans.addAll(transactionDAO.getTransactions(selectedDate))
        adapter.notifyDataSetChanged()
    }

    private fun renderWeek() {
        val dates = WalletCalendar.weekDates(selectedDate)
        val totals = transactionDAO.getTimeStats(dates)
        weekDays.removeAllViews()

        for ((index, date) in dates.withIndex()) {
            val amount = totals[index]
            val cell = createWeekCell(date, amount)
            cell.setCalendarGestures(
                onTap = {
                    selectedDate = date
                    reload()
                },
                onWeekOrMonthSwipe = { direction -> moveWeek(direction) }
            )
            val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            val gap = resources.getDimensionPixelSize(R.dimen.home_week_cell_gap)
            params.setMargins(gap, 0, gap, 0)
            weekDays.addView(cell, params)
        }

        lblDate.text = TransactionDAO.toText(selectedDate)
        val selectedIndex = dates.indexOfFirst { WalletCalendar.isSameDay(it, selectedDate) }
        val selectedTotals = totals.getOrElse(selectedIndex) { TimeStat() }
        lblTotalThu.text = "Tổng thu: ${Money.format(selectedTotals.totalIn)}"
        lblTotalChi.text = "Tổng chi: ${Money.format(selectedTotals.totalOut)}"
    }

    private fun createWeekCell(date: Date, totals: TimeStat): LinearLayout {
        val selected = WalletCalendar.isSameDay(date, selectedDate)
        val dayNumber = Calendar.getInstance().apply { time = date }.get(Calendar.DAY_OF_MONTH)
        val cell = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            val horizontalPadding = resources.getDimensionPixelSize(R.dimen.home_week_cell_padding_horizontal)
            val verticalPadding = resources.getDimensionPixelSize(R.dimen.home_week_cell_padding_vertical)
            setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
            background = cellBackground(selected, muted = false)
            contentDescription = buildString {
                append("${WalletCalendar.weekDayShort(date)}, ngày $dayNumber")
                append(", thu ${Money.format(totals.totalIn)}")
                append(", chi ${Money.format(totals.totalOut)}")
            }
        }

        cell.addView(label("${WalletCalendar.weekDayShort(date)}\n$dayNumber", R.dimen.home_week_day_text, bold = selected))
        cell.addView(label(if (totals.totalIn == 0f) "" else "+${Money.format(totals.totalIn)}", R.dimen.home_week_total_text, color = getColor(R.color.thu)))
        cell.addView(label(if (totals.totalOut == 0f) "" else "−${Money.format(totals.totalOut)}", R.dimen.home_week_total_text, color = getColor(R.color.chi)))
        return cell
    }

    private fun moveWeek(direction: Int) {
        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            time = selectedDate
            add(Calendar.WEEK_OF_YEAR, direction)
        }
        selectedDate = calendar.time
        reload()
    }

    private fun label(text: String, size: Int, color: Int = Color.BLACK, bold: Boolean = false) =
        TextView(this).apply {
            this.text = text
            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(size))
            setTextColor(color)
            gravity = Gravity.CENTER
            maxLines = 2
            if (bold) setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

    private fun cellBackground(selected: Boolean, muted: Boolean): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(7).toFloat()
            setColor(
                when {
                    selected -> Color.rgb(224, 245, 230)
                    muted -> Color.rgb(246, 246, 246)
                    else -> Color.WHITE
                }
            )
            setStroke(dp(1), if (selected) Color.rgb(0, 145, 65) else Color.rgb(210, 210, 210))
        }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun returnToMonth() {
        setResult(RESULT_OK, Intent().putExtra(EXTRA_SELECTED_DATE, selectedDate.time))
        finish()
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (monthDayDetail) returnToMonth() else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    companion object {
        const val EXTRA_DATE = "month_day_date"
        const val EXTRA_MONTH_DAY = "month_day_detail"
        const val EXTRA_SELECTED_DATE = "month_day_selected_date"
        private const val REQUEST_MONTH = 7
    }
}
