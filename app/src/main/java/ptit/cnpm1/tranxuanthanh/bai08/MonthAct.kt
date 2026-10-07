package ptit.cnpm1.tranxuanthanh.bai08

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

/** Lịch tháng và chi tiết ngày được chọn. */
class MonthAct : Activity(), View.OnClickListener {

    private lateinit var lblMonth: TextView
    private lateinit var calendarGrid: GridLayout
    private lateinit var btnBack: ImageButton
    // Tháng hiển thị và ngày đang chọn.
    private var selectedDate: Date = Date()
    private lateinit var transactionDAO: TransactionDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.month)

        lblMonth = findViewById(R.id.lblMonth)
        calendarGrid = findViewById(R.id.calendarGrid)
        btnBack = findViewById(R.id.btnBack)
        // Nhận ngày được chọn từ trang chủ.
        selectedDate = Date(intent.getLongExtra(EXTRA_DATE, Date().time))
        transactionDAO = TransactionDAO(this)
        btnBack.setOnClickListener(this)
    }

    /** Xử lý nút quay lại. */
    override fun onClick(view: View?) {
        if (view?.id == R.id.btnBack) returnToHome()
    }

    /** Vẽ lưới lịch tháng. */
    private fun renderMonth() {
        lblMonth.text = WalletCalendar.monthTitle(selectedDate)
        // Mỗi hàng có đủ thứ Hai đến Chủ nhật.
        val dates = WalletCalendar.monthDates(selectedDate)
        // Tổng thu/chi được trả về theo thứ tự ngày.
        val totals = transactionDAO.getTimeStats(dates)

        calendarGrid.removeAllViews()
        calendarGrid.columnCount = 7
        calendarGrid.rowCount = dates.size / 7 + 1
        val headerHeight = R.dimen.month_header_height
        // Hàng tiêu đề thứ trong tuần.
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
            // Làm mờ và vô hiệu hóa các ngày ngoài tháng.
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

    /** Tạo một ô ngày trong lịch. */
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
            // Chỉ hiển thị tổng thu/chi của ngày thuộc tháng này.
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

    /** Mở chi tiết ngày bằng màn hình trang chủ. */
    private fun openSelectedDay(date: Date) {
        selectedDate = date
        val intent = Intent(this, HomeAct::class.java)
            .putExtra(HomeAct.EXTRA_DATE, date.time)
            .putExtra(HomeAct.EXTRA_MONTH_DAY, true)
        startActivityForResult(intent, REQUEST_DAY_DETAIL)
    }

    /** Khôi phục ngày được chọn khi quay về từ chi tiết. */
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_DAY_DETAIL && resultCode == RESULT_OK && data != null) {
            selectedDate = Date(data.getLongExtra(HomeAct.EXTRA_SELECTED_DATE, selectedDate.time))
            renderMonth()
        }
    }

    /** Chuyển sang tháng liền trước hoặc liền sau. */
    private fun changeMonth(amount: Int) {
        selectedDate = WalletCalendar.changeMonth(selectedDate, amount)
        renderMonth()
    }

    /** Thiết lập các ô có chiều rộng bằng nhau. */
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

    /** Nhãn căn giữa trong ô lịch. */
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

    /** Ưu tiên kiểu hiển thị của ngày được chọn. */
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

    /** Đổi đơn vị dp sang pixel. */
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    /** Trả ngày được chọn về trang chủ. */
    private fun returnToHome() {
        setResult(RESULT_OK, Intent().putExtra(EXTRA_SELECTED_DATE, selectedDate.time))
        finish()
    }

    /** Xử lý nút quay lại của thiết bị. */
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        returnToHome()
    }

    // Vẽ lại khi màn hình được hiển thị.
    override fun onResume() {
        super.onResume()
        if (::lblMonth.isInitialized) renderMonth()
    }

    companion object {
        // Các khóa Intent và mã yêu cầu.
        const val EXTRA_DATE = "month_initial_date"
        const val EXTRA_SELECTED_DATE = "month_selected_date"
        private const val REQUEST_DAY_DETAIL = 8
    }
}
