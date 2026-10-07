package ptit.cnpm1.tranxuanthanh.bai08

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView

/** Danh sách giao dịch của mục trong kỳ đã chọn; chạm một dòng để sửa giao dịch. */
class CategoryTransactionsAct : Activity() {
    private lateinit var dao: TransactionDAO
    private lateinit var adapter: ArrayAdapter<Transaction>
    private val transactions = ArrayList<Transaction>()
    private var period = StatPeriod.current()
    private var categoryId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categorytransactions)
        period = StatPeriod.from(intent)
        categoryId = intent.getIntExtra("category_id", 0)
        dao = TransactionDAO(this)
        findViewById<ImageButton>(R.id.btnBackStat).setOnClickListener { finish() }
        findViewById<TextView>(R.id.lblStatPeriod).text = period.title
        val list = findViewById<ListView>(R.id.lwStats)
        adapter = object : ArrayAdapter<Transaction>(this, 0, transactions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
                (convertView as? ShowTransFrag ?: ShowTransFrag(this@CategoryTransactionsAct)).apply {
                    // Slide 23 yêu cầu hiển thị ngày giao dịch thay cho tên mục ở màn chi tiết.
                    bind(transactions[position], showDate = true)
                }
        }
        list.adapter = adapter
        list.emptyView = findViewById(R.id.lblStatEmpty)
        list.setOnItemClickListener { _, _, position, _ ->
            startActivity(Intent(this, EditAct::class.java).putExtra("transaction", transactions[position]))
        }
    }

    override fun onResume() {
        super.onResume()
        // Đọc lại mục và giao dịch vì người dùng có thể sửa, đổi mục hoặc xóa giao dịch.
        val category = CategoryDAO(this).getCategoryIndex()[categoryId]
        findViewById<TextView>(R.id.lblStatTitle).text = category?.name ?: "Mục đã bị xóa"
        transactions.clear()
        transactions.addAll(dao.getTransactions(period, categoryId))
        findViewById<TextView>(R.id.lblStatTotal).apply {
            val income = category?.type?.id == CategoryType.ID_THU
            text = "Tổng ${if (income) "thu" else "chi"}: ${Money.format(transactions.sumOf { it.amount.toDouble() }.toFloat())}"
            setTextColor(getColor(if (income) R.color.thu else R.color.chi))
        }
        adapter.notifyDataSetChanged()
    }
}
