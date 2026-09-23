package ptit.cnpm1.tranxuanthanh.bai06

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

/**
 * Màn thêm giao dịch (slide 6 và 12): chọn kiểu thu/chi, chọn mục thu/chi
 * tương ứng, nhập số tiền, ghi chú và thời gian.
 */
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

    private lateinit var dao: WalletDAO

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

        dao = WalletDAO(this)

        adapter = CategoryAdapter(this, categories)
        spCategory.adapter = adapter

        // Bấm giữ một mục để sửa chính mục đó (slide 13)
        spCategory.setOnLongClickListener {
            selectedCategory()?.let {
                val edit = Intent(this, EditCategoryAct::class.java).apply {
                    putExtra("category", it)
                }
                startActivity(edit)
            }
            true
        }

        txtDate.setText(WalletDAO.toText(Date()))
        txtDate.setOnClickListener { pickDate() }

        btnNewCategory.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        // Gạt nút on/off thì danh sách mục đổi theo kiểu thu/chi mới.
        // Gán trạng thái đầu trước khi gắn listener, để lần gán đó không
        // làm mất mục đang chọn sẵn.
        swType.isChecked = idType == CategoryType.ID_THU
        swType.setOnCheckedChangeListener { _, isChecked ->
            idType = if (isChecked) CategoryType.ID_THU else CategoryType.ID_CHI
            reloadCategories(0)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // Dấu + cạnh mục thu/chi: sang màn thêm mục mới cùng kiểu đang chọn
            R.id.btnNewCategory -> {
                val add = Intent(this, AddCategoryAct::class.java).apply {
                    putExtra("idType", idType)
                }
                startActivity(add)
            }
            R.id.btnSave -> save()
            // Hủy: quay về home mà không ghi gì vào CSDL
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

        val t = Transaction(
            date = WalletDAO.toDate(dateText),
            amount = amountText.toFloat(),
            note = txtNote.text.toString().trim(),
            category = category
        )

        if (!dao.addTransaction(t)) {
            Toast.makeText(this, "Thêm giao dịch thất bại", Toast.LENGTH_SHORT).show()
            return
        }

        finish()
    }

    private fun selectedCategory(): Category? =
        categories.getOrNull(spCategory.selectedItemPosition)

    /**
     * Đọc lại cây mục thu/chi của kiểu đang chọn. [keepId] là mục muốn giữ
     * nguyên lựa chọn sau khi vẽ lại, 0 nghĩa là chọn mục đầu tiên.
     */
    private fun reloadCategories(keepId: Int) {
        categories.clear()
        categories.addAll(dao.getCategories(idType))
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

    /** Quay lại từ màn thêm/sửa mục thì danh sách mục có thể đã khác. */
    override fun onResume() {
        super.onResume()
        reloadCategories(selectedCategory()?.id ?: 0)
    }
}
