package ptit.cnpm1.tranxuanthanh.bai08

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.ImageButton
import android.widget.TextView

/** Quá trình thu/chi của 12 tháng gần nhất; chạm tháng để mở thống kê tỉ lệ tháng đó. */
class ProcessStatAct : Activity() {
    private lateinit var chart: ProcessChartView
    private lateinit var dao: TransactionDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.processstat)
        findViewById<TextView>(R.id.lblStatTitle).text = "Thu/chi 12 tháng gần nhất"
        findViewById<ImageButton>(R.id.btnBackStat).setOnClickListener { finish() }
        findViewById<TextView>(R.id.lblLegend).text = SpannableString("● Thu     ● Chi").apply {
            setSpan(ForegroundColorSpan(getColor(R.color.thu)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(getColor(R.color.chi)), 10, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        dao = TransactionDAO(this)
        chart = findViewById(R.id.processChart)
        chart.onMonthClick = { period ->
            startActivity(period.putInto(Intent(this, RatioStatAct::class.java)))
        }
    }

    override fun onResume() {
        super.onResume()
        val periods = StatPeriod.lastTwelveMonths()
        chart.submit(periods, dao.getMonthlyStats(periods))
    }
}
