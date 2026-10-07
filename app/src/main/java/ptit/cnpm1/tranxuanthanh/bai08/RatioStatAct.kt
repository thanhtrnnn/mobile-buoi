package ptit.cnpm1.tranxuanthanh.bai08

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView

/** Thống kê theo tỉ lệ: chọn tháng/năm, chọn kỳ, chuyển thu/chi rồi xem chi tiết mục. */
class RatioStatAct : Activity() {
    private lateinit var dao: TransactionDAO
    private lateinit var spPeriod: Spinner
    private lateinit var chart: PieChartView
    private lateinit var totalLabel: TextView
    private lateinit var adapter: ArrayAdapter<CategoryStat>
    private val stats = ArrayList<CategoryStat>()
    private var periods: List<StatPeriod> = emptyList()
    private var period = StatPeriod.current()
    private var idType = CategoryType.ID_CHI
    private var byYear = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ratiostat)
        dao = TransactionDAO(this)
        period = if (savedInstanceState == null) StatPeriod.from(intent) else StatPeriod(
            savedInstanceState.getInt("year"), savedInstanceState.getInt("month")
        )
        idType = savedInstanceState?.getInt("type") ?: intent.getIntExtra("stat_type", CategoryType.ID_CHI)
        byYear = period.month == 0
        findViewById<TextView>(R.id.lblStatTitle).text = "Thống kê theo tỉ lệ"
        findViewById<ImageButton>(R.id.btnBackStat).setOnClickListener { finish() }
        spPeriod = findViewById(R.id.spPeriod)
        chart = findViewById(R.id.pieChart)
        totalLabel = findViewById(R.id.lblStatTotal)
        val list = findViewById<ListView>(R.id.lwStats)
        adapter = object : ArrayAdapter<CategoryStat>(this, 0, stats) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
                (convertView as? StatRow ?: StatRow(this@RatioStatAct)).apply {
                    bind(stats[position], stats.sumOf { it.total.toDouble() }, position)
                }
        }
        list.adapter = adapter
        list.emptyView = findViewById(R.id.lblStatEmpty)
        list.setOnItemClickListener { _, _, position, _ ->
            val item = stats[position]
            startActivity(period.putInto(Intent(this, CategoryTransactionsAct::class.java))
                .putExtra("category_id", item.id))
        }
        val typeSpinner = findViewById<Spinner>(R.id.spPeriodType)
        typeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Theo tháng", "Theo năm")).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        typeSpinner.setSelection(if (byYear) 1 else 0)
        typeSpinner.onItemSelectedListener = selectionListener { position ->
            val nextByYear = position == 1
            if (nextByYear != byYear || periods.isEmpty()) {
                byYear = nextByYear
                period = StatPeriod(period.year, if (byYear) 0 else period.month.takeIf { it > 0 } ?: StatPeriod.current().month)
                reloadPeriods()
            }
        }
        spPeriod.onItemSelectedListener = selectionListener { position ->
            periods.getOrNull(position)?.let { period = it; reloadStats() }
        }
        findViewById<Switch>(R.id.swStatType).apply {
            isChecked = idType == CategoryType.ID_THU
            setOnCheckedChangeListener { _, checked ->
                idType = if (checked) CategoryType.ID_THU else CategoryType.ID_CHI
                reloadStats()
            }
        }
        reloadPeriods()
    }

    /** Đổi loại kỳ sẽ thay toàn bộ lựa chọn của Spinner thứ hai. */
    private fun reloadPeriods() {
        periods = dao.getStatPeriods(byYear, period)
        spPeriod.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periods).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spPeriod.setSelection(periods.indexOf(period).coerceAtLeast(0))
        reloadStats()
    }

    private fun reloadStats() {
        stats.clear()
        stats.addAll(dao.getCategoryStats(period, idType))
        chart.submit(stats)
        val total = stats.sumOf { it.total.toDouble() }.toFloat()
        totalLabel.text = "Tổng ${if (idType == CategoryType.ID_THU) "thu" else "chi"}: ${Money.format(total)}"
        totalLabel.setTextColor(getColor(if (idType == CategoryType.ID_THU) R.color.thu else R.color.chi))
        adapter.notifyDataSetChanged()
    }

    /** Quay về từ màn sửa giao dịch phải cập nhật cả lựa chọn kỳ lẫn biểu đồ. */
    override fun onResume() {
        super.onResume()
        if (::dao.isInitialized) reloadPeriods()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("year", period.year)
        outState.putInt("month", period.month)
        outState.putInt("type", idType)
        super.onSaveInstanceState(outState)
    }

    private fun selectionListener(select: (Int) -> Unit) = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = select(position)
        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
    }
}
