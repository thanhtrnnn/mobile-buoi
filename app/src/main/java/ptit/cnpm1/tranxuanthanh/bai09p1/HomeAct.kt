package ptit.cnpm1.tranxuanthanh.bai09p1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import java.util.Date

/** Trang chủ hiển thị tổng thu/chi và các giao dịch của ngày hiện tại. */
class HomeAct : Activity(), View.OnClickListener {

    private lateinit var lblDate: TextView
    private lateinit var lblTotalThu: TextView
    private lateinit var lblTotalChi: TextView
    private lateinit var lwTrans: ListView
    private lateinit var btnAdd: Button

    private val listTrans = ArrayList<Transaction>()
    private lateinit var adapter: ArrayAdapter<Transaction>
    private lateinit var transactionDAO: TransactionDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        lblDate = findViewById(R.id.lblDate)
        lblTotalThu = findViewById(R.id.lblTotalThu)
        lblTotalChi = findViewById(R.id.lblTotalChi)
        lwTrans = findViewById(R.id.lwTrans)
        btnAdd = findViewById(R.id.btnAdd)

        transactionDAO = TransactionDAO(this)
        adapter = object : ArrayAdapter<Transaction>(this, 0, listTrans) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = convertView as? ShowTransFrag ?: ShowTransFrag(this@HomeAct)
                row.bind(listTrans[position])
                return row
            }
        }
        lwTrans.adapter = adapter
        // Bấm một dòng để sửa giao dịch đó.
        lwTrans.setOnItemClickListener { _, _, position, _ ->
            startActivity(Intent(this, EditAct::class.java).putExtra("transaction", listTrans[position]))
        }

        btnAdd.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.btnAdd) startActivity(Intent(this, AddAct::class.java))
    }

    /** Đọc lại ngày hiện tại, tổng thu/chi và danh sách giao dịch. */
    private fun reload() {
        val today = Date()
        lblDate.text = TransactionDAO.toText(today)
        lblTotalThu.text = "Tổng thu: ${Money.format(transactionDAO.total(today, CategoryType.ID_THU))}"
        lblTotalChi.text = "Tổng chi: ${Money.format(transactionDAO.total(today, CategoryType.ID_CHI))}"
        listTrans.clear()
        listTrans.addAll(transactionDAO.getTransactions(today))
        adapter.notifyDataSetChanged()
    }

    // Quay về từ màn thêm/sửa thì cập nhật lại trang chủ.
    override fun onResume() {
        super.onResume()
        reload()
    }
}
