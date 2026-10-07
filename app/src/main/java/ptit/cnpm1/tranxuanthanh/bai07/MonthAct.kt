package ptit.cnpm1.tranxuanthanh.bai07

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Calendar
import java.util.Date

/** month calendar and selected-day details */
class MonthAct : Activity(), View.OnClickListener {

    private lateinit var lblMonth: TextView
    private lateinit var calendarGrid: GridLayout
    private lateinit var btnBack: ImageButton
    // month and selected day
    private var selectedDate: Date = Date()
    private lateinit var transactionDAO: TransactionDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.month)

        lblMonth = findViewById(R.id.lblMonth)
        calendarGrid = findViewById(R.id.calendarGrid)
        btnBack = findViewById(R.id.btnBack)
        // read selected date from home
        selectedDate = Date(intent.getLongExtra(EXTRA_DATE, Date().time))
        transactionDAO = TransactionDAO(this)
        btnBack.setOnClickListener(this)
    }

    /** handle back button */
    override fun onClick(view: View?) {
        if (view?.id == R.id.btnBack) returnToHome()
    }

    /** draw month grid */
    private fun renderMonth() {
        lblMonth.text = WalletCalendar.monthTitle(selectedDate)
        // full monday-to-sunday rows
        val dates = WalletCalendar.monthDates(selectedDate)
        // totals follow the date order
        val totals = dates.map { transactionDAO.getTimeStats(it) }

        calendarGrid.removeAllViews()
        calendarGrid.columnCount = 7
        calendarGrid.rowCount = dates.size / 7 + 1
        val headerHeight = R.dimen.month_header_height
        // weekday header row
        for ((column, dayName) in WalletCalendar.weekdayHeaders().withIndex()) {
            val header = TextView(this).apply {
                text = dayName
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.month_header_text))
                setTextColor(Color.DKGRAY)
                setTypeface(typeface, Typeface.BOLD)
            }
            calendarGrid.addView(header, gridParams(row = 0, column = column, heightRes = headerHeight))
        }

        for ((index, date) in dates.withIndex()) {
            val dateTotals = totals[index]
            // mute and disable dates outside this month
            val inMonth = WalletCalendar.isSameMonth(date, selectedDate)
            val selected = WalletCalendar.isSameDay(date, selectedDate)
            val cell = createDateCell(date, dateTotals, inMonth, selected)
            cell.setCalendarGestures(
                onTap = { if (inMonth) openSelectedDay(date) },
                onWeekOrMonthSwipe = { direction -> changeMonth(direction) }
            )
            calendarGrid.addView(
                cell,
                gridParams(row = index / 7 + 1, column = index % 7, heightRes = R.dimen.month_cell_height)
            )
        }
    }

    /** create one date cell */
    private fun createDateCell(
        date: Date,
        totals: TimeStat,
        inMonth: Boolean,
        selected: Boolean
    ) = LinearLayout(this).apply {
        val dayNumber = Calendar.getInstance().apply { time = date }.get(Calendar.DAY_OF_MONTH)
        val textColor = if (inMonth) Color.BLACK else Color.LTGRAY
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        val verticalPadding = resources.getDimensionPixelSize(R.dimen.month_cell_padding_vertical)
        setPadding(0, verticalPadding, 0, verticalPadding)
        background = cellBackground(selected, muted = !inMonth)
        contentDescription = buildString {
            // include totals for this month only
            append("Ngày $dayNumber")
            if (inMonth) {
                append(", thu ${Money.format(totals.totalIn)}")
                append(", chi ${Money.format(totals.totalOut)}")
            }
        }

        addView(calendarLabel(dayNumber.toString(), R.dimen.month_day_text, textColor, bold = selected))
        addView(
            calendarLabel(
                if (totals.totalIn == 0f) "" else "+${Money.format(totals.totalIn)}",
                R.dimen.month_total_text,
                if (inMonth) getColor(R.color.thu) else Color.LTGRAY
            )
        )
        addView(
            calendarLabel(
                if (totals.totalOut == 0f) "" else "−${Money.format(totals.totalOut)}",
                R.dimen.month_total_text,
                if (inMonth) getColor(R.color.chi) else Color.LTGRAY
            )
        )
    }

    /** open selected day in home */
    private fun openSelectedDay(date: Date) {
        selectedDate = date
        val intent = Intent(this, HomeAct::class.java)
            .putExtra(HomeAct.EXTRA_DATE, date.time)
            .putExtra(HomeAct.EXTRA_MONTH_DAY, true)
        startActivityForResult(intent, REQUEST_DAY_DETAIL)
    }

    /** restore the selected date from day details */
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_DAY_DETAIL && resultCode == RESULT_OK && data != null) {
            selectedDate = Date(data.getLongExtra(HomeAct.EXTRA_SELECTED_DATE, selectedDate.time))
            renderMonth()
        }
    }

    /** switch month */
    private fun changeMonth(amount: Int) {
        selectedDate = WalletCalendar.changeMonth(selectedDate, amount)
        renderMonth()
    }

    /** equal-width grid cell params */
    private fun gridParams(row: Int, column: Int, heightRes: Int) =
        GridLayout.LayoutParams(
            GridLayout.spec(row),
            GridLayout.spec(column, 1, 1f)
        ).apply {
            width = 0
            height = resources.getDimensionPixelSize(heightRes)
            val margin = resources.getDimensionPixelSize(R.dimen.month_cell_margin)
            setMargins(margin, margin, margin, margin)
        }

    /** centered calendar label */
    private fun calendarLabel(
        text: String,
        sizeRes: Int,
        color: Int,
        bold: Boolean = false
    ) = TextView(this).apply {
        this.text = text
        setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(sizeRes))
        setTextColor(color)
        gravity = Gravity.CENTER
        maxLines = 1
        if (bold) setTypeface(typeface, Typeface.BOLD)
        layoutParams = android.widget.LinearLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    /** selected style takes priority */
    private fun cellBackground(selected: Boolean, muted: Boolean) = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(5).toFloat()
        setColor(
            when {
                selected -> Color.rgb(224, 245, 230)
                muted -> Color.rgb(247, 247, 247)
                else -> Color.WHITE
            }
        )
        setStroke(dp(1), if (selected) Color.rgb(0, 145, 65) else Color.rgb(215, 215, 215))
    }

    /** dp to px */
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    /** return selected date to home */
    private fun returnToHome() {
        setResult(RESULT_OK, Intent().putExtra(EXTRA_SELECTED_DATE, selectedDate.time))
        finish()
    }

    /** handle device back */
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        returnToHome()
    }

    // redraw when visible
    override fun onResume() {
        super.onResume()
        if (::lblMonth.isInitialized) renderMonth()
    }

    companion object {
        // intent keys and request code
        const val EXTRA_DATE = "month_initial_date"
        const val EXTRA_SELECTED_DATE = "month_selected_date"
        private const val REQUEST_DAY_DETAIL = 8
    }
}
