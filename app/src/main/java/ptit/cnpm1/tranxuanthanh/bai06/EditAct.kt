package ptit.cnpm1.tranxuanthanh.bai06

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Màn sửa giao dịch (slide 6): giống màn thêm nhưng điền sẵn giao dịch nhận
 * được từ trang home và có thêm nút Xóa.
 */
class EditAct : Activity(), View.OnClickListener {

    private lateinit var btnThu: Button
    private lateinit var btnChi: Button
    private lateinit var spCategory: Spinner
    private lateinit var btnNewCategory: Button
    private lateinit var txtAmount: EditText
    private lateinit var txtNote: EditText
    private lateinit var txtDate: EditText
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button
    private lateinit var btnCancel: Button

    /** Giao dịch đang được sửa, nhận từ trang home. */
    private lateinit var trans: Transaction

    private var idType = CategoryType.ID_CHI

    private val categories = ArrayList<Category>()
    private lateinit var adapter: ArrayAdapter<Category>

    private lateinit var dao: WalletDAO

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit)

        btnThu = findViewById(R.id.btnThu)
        btnChi = findViewById(R.id.btnChi)
        spCategory = findViewById(R.id.spCategory)
        btnNewCategory = findViewById(R.id.btnNewCategory)
        txtAmount = findViewById(R.id.txtAmount)
        txtNote = findViewById(R.id.txtNote)
        txtDate = findViewById(R.id.txtDate)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)
        btnCancel = findViewById(R.id.btnCancel)

        dao = WalletDAO(this)

        val received = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("transaction", Transaction::class.java)
        } else {
            intent.getSerializableExtra("transaction") as? Transaction
        }
        trans = received ?: Transaction()
        idType = trans.category.type.id

        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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

        // Điền sẵn thông tin cũ
        txtAmount.setText(trans.amount.toLong().toString())
        txtNote.setText(trans.note)
        txtDate.setText(WalletDAO.toText(trans.date))
        txtDate.setOnClickListener { pickDate() }

        btnThu.setOnClickListener(this)
        btnChi.setOnClickListener(this)
        btnNewCategory.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        showType()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // Đổi kiểu thu/chi thì danh sách mục cũng đổi theo
            R.id.btnThu -> {
                idType = CategoryType.ID_THU
                showType()
                reloadCategories(0)
            }
            R.id.btnChi -> {
                idType = CategoryType.ID_CHI
                showType()
                reloadCategories(0)
            }
            R.id.btnNewCategory -> {
                val add = Intent(this, AddCategoryAct::class.java).apply {
                    putExtra("idType", idType)
                }
                startActivity(add)
            }
            R.id.btnSave -> save()
            R.id.btnDelete -> confirmDelete()
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

        // Giữ nguyên id để WalletDAO biết sửa đúng dòng nào trong CSDL
        trans = Transaction(
            id = trans.id,
            date = WalletDAO.toDate(dateText),
            amount = amountText.toFloat(),
            note = txtNote.text.toString().trim(),
            category = category
        )

        if (!dao.editTransaction(trans)) {
            Toast.makeText(this, "Sửa giao dịch thất bại", Toast.LENGTH_SHORT).show()
            return
        }

        finish()
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa giao dịch này không?")
            .setPositiveButton("Có") { _, _ ->
                if (dao.deleteTransaction(trans.id)) {
                    finish()
                } else {
                    Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                }
            }
            // Bấm Không thì không làm gì, ở nguyên màn sửa
            .setNegativeButton("Không", null)
            .show()
    }

    /** Nút của kiểu đang chọn thì bật (nền xanh), nút kia tắt (nền trắng). */
    private fun showType() {
        val thuOn = idType == CategoryType.ID_THU
        btnThu.setBackgroundResource(if (thuOn) R.drawable.bg_menu else R.drawable.bg_input)
        btnChi.setBackgroundResource(if (thuOn) R.drawable.bg_input else R.drawable.bg_menu)
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

        val index = categories.indexOfFirst { it.id == keepId }
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

    /**
     * Lần đầu vào màn thì chọn sẵn mục của giao dịch đang sửa; các lần sau
     * (quay lại từ màn thêm/sửa mục) thì giữ nguyên mục đang chọn.
     */
    override fun onResume() {
        super.onResume()
        reloadCategories(selectedCategory()?.id ?: trans.category.id)
    }
}
