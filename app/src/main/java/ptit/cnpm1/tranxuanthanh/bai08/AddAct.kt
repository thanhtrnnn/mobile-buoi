package ptit.cnpm1.tranxuanthanh.bai08

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Thêm giao dịch mới. */
class AddAct : Activity(), View.OnClickListener {

    private lateinit var swType: Switch
    private lateinit var spCategory: Spinner
    private lateinit var btnNewCategory: Button
    private lateinit var txtAmount: EditText
    private lateinit var txtNote: EditText
    private lateinit var txtDate: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    /** Kiểu đang chọn, mặc định là chi. */
    private var idType = CategoryType.ID_CHI

    private val categories = ArrayList<Category?>()
    private lateinit var adapter: CategoryAdapter

    private lateinit var categoryDAO: CategoryDAO
    private lateinit var transactionDAO: TransactionDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add)

        swType = findViewById(R.id.swType)
        spCategory = findViewById(R.id.spCategory)
        btnNewCategory = findViewById(R.id.btnNewCategory)
        txtAmount = findViewById(R.id.txtAmount)
        txtNote = findViewById(R.id.txtNote)
        txtDate = findViewById(R.id.txtDate)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        categoryDAO = CategoryDAO(this)
        transactionDAO = TransactionDAO(this)

        adapter = CategoryAdapter(this, categories)
        spCategory.adapter = adapter

        // Nhấn giữ mục thu/chi để mở màn hình sửa.
        spCategory.setOnLongClickListener {
            selectedCategory()?.let {
                val edit = Intent(this, EditCategoryAct::class.java).apply {
                    putExtra("category", it)
                }
                startActivity(edit)
            }
            true
        }

        val initialDate = Date(intent.getLongExtra(EXTRA_INITIAL_DATE, Date().time))
        txtDate.setText(TransactionDAO.toText(initialDate))
        txtDate.setOnClickListener { pickDate() }

        btnNewCategory.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        // Đặt kiểu ban đầu trước khi nghe sự kiện để giữ mục đang chọn.
        swType.isChecked = idType == CategoryType.ID_THU
        swType.setOnCheckedChangeListener { _, isChecked ->
            idType = if (isChecked) CategoryType.ID_THU else CategoryType.ID_CHI
            reloadCategories(0)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // Mở màn hình thêm mục thuộc kiểu thu/chi đang chọn.
            R.id.btnNewCategory -> {
                val add = Intent(this, AddCategoryAct::class.java).apply {
                    putExtra("idType", idType)
                }
                startActivity(add)
            }
            R.id.btnSave -> save()
            // Hủy và đóng màn hình.
            R.id.btnCancel -> finish()
        }
    }

    private fun save() {
        val category = selectedCategory()
        if (category == null) {
            Toast.makeText(this, "Chưa có mục thu/chi nào để chọn", Toast.LENGTH_SHORT).show()
            return
        }

        val amountText = txtAmount.text.toString().trim()
        val dateText = txtDate.text.toString().trim()
        if (amountText.isEmpty() || dateText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toFloatOrNull()
        if (amount == null || !amount.isFinite() || amount <= 0f) {
            txtAmount.error = "Số tiền phải là số dương hợp lệ"
            return
        }

        val t = Transaction(
            date = TransactionDAO.toDate(dateText),
            amount = amount,
            note = txtNote.text.toString().trim(),
            category = category
        )

        if (!transactionDAO.addTransaction(t)) {
            Toast.makeText(this, "Thêm giao dịch thất bại", Toast.LENGTH_SHORT).show()
            return
        }

        finish()
    }

    private fun selectedCategory(): Category? =
        categories.getOrNull(spCategory.selectedItemPosition)

    /** Nạp lại danh sách mục và giữ mục đang chọn. */
    private fun reloadCategories(keepId: Int) {
        categories.clear()
        categories.addAll(categoryDAO.getCategories(idType))
        adapter.notifyDataSetChanged()

        val index = categories.indexOfFirst { it?.id == keepId }
        spCategory.setSelection(if (index >= 0) index else 0)
    }

    private fun pickDate() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val typed = txtDate.text.toString().trim()
        if (typed.isNotEmpty()) format.parse(typed)?.let { calendar.time = it }

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val picked = Calendar.getInstance()
                picked.set(year, month, dayOfMonth, 0, 0, 0)
                picked.set(Calendar.MILLISECOND, 0)
                txtDate.setText(format.format(picked.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /** Cập nhật danh sách sau khi quay về từ màn hình sửa mục. */
    override fun onResume() {
        super.onResume()
        reloadCategories(selectedCategory()?.id ?: 0)
    }

    companion object {
        const val EXTRA_INITIAL_DATE = "add_transaction_initial_date"
    }
}
