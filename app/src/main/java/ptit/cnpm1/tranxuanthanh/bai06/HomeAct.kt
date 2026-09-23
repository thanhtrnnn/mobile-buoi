package ptit.cnpm1.tranxuanthanh.bai06

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

/**
 * Trang home (slide 5 và 11): các giao dịch của ngày hiện tại, tổng thu và
 * tổng chi của ngày, dấu + để thêm giao dịch, bấm vào một dòng để sửa.
 */
class HomeAct : Activity(), View.OnClickListener {

    private lateinit var lblDate: TextView
    private lateinit var lblTotalThu: TextView
    private lateinit var lblTotalChi: TextView
    private lateinit var lwTrans: ListView
    private lateinit var btnAdd: Button

    /** Ngày đang xem. Bài này chỉ hiện ngày hiện tại. */
    private val today = Date()

    private val listTrans = ArrayList<Transaction>()
    private lateinit var adapter: ArrayAdapter<Transaction>

    private lateinit var dao: WalletDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        lblDate = findViewById(R.id.lblDate)
        lblTotalThu = findViewById(R.id.lblTotalThu)
        lblTotalChi = findViewById(R.id.lblTotalChi)
        lwTrans = findViewById(R.id.lwTrans)
        btnAdd = findViewById(R.id.btnAdd)

        dao = WalletDAO(this)

        lblDate.text = WalletDAO.toText(today)

        adapter = object : ArrayAdapter<Transaction>(this, 0, listTrans) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = convertView as? ShowTransFrag ?: ShowTransFrag(this@HomeAct)
                row.bind(listTrans[position])
                return row
            }
        }
        lwTrans.adapter = adapter

        // Bấm vào một dòng thì sang màn sửa giao dịch đó
        lwTrans.setOnItemClickListener { _, _, position, _ ->
            val edit = Intent(this, EditAct::class.java).apply {
                putExtra("transaction", listTrans[position])
            }
            startActivity(edit)
        }

        // Danh sách ban đầu do onResume() đổ vào, vì onResume() luôn chạy
        // ngay sau onCreate()

        btnAdd.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnAdd) {
            startActivity(Intent(this, AddAct::class.java))
        }
    }

    /** Đọc lại giao dịch của ngày hiện tại và vẽ lại tổng thu, tổng chi. */
    private fun reload() {
        listTrans.clear()
        listTrans.addAll(dao.getTransactions(today))
        adapter.notifyDataSetChanged()

        var totalThu = 0f
        var totalChi = 0f
        for (t in listTrans) {
            if (t.category.type.id == CategoryType.ID_THU) totalThu += t.amount
            else totalChi += t.amount
        }
        lblTotalThu.text = "Tổng thu: " + Money.format(totalThu)
        lblTotalChi.text = "Tổng chi: " + Money.format(totalChi)
    }

    /**
     * Mỗi lần quay lại trang home (từ màn Thêm hoặc màn Sửa) thì đọc lại CSDL,
     * vì hai màn đó đã tự ghi vào CSDL rồi; bấm Hủy thì CSDL không đổi nên
     * danh sách vẽ lại vẫn y nguyên.
     */
    override fun onResume() {
        super.onResume()
        reload()
    }
}
