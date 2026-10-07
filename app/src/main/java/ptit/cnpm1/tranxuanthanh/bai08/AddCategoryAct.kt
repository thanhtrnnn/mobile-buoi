package ptit.cnpm1.tranxuanthanh.bai08

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast

/** Thêm mục thu hoặc chi. */
class AddCategoryAct : Activity(), View.OnClickListener {

    private lateinit var swType: Switch
    private lateinit var spParent: Spinner
    private lateinit var btnNewParent: Button
    private lateinit var txtName: EditText
    private lateinit var txtNote: EditText
    private lateinit var spIcon: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    /** Kiểu thu/chi nhận từ màn hình giao dịch. */
    private var idType = CategoryType.ID_CHI

    /** Các mục cha có thể chọn; null nghĩa là không có cha. */
    private val parents = ArrayList<Category?>()
    private lateinit var parentAdapter: CategoryAdapter
    private lateinit var logoAdapter: LogoAdapter

    private lateinit var categoryDAO: CategoryDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addcategory)

        swType = findViewById(R.id.swType)
        spParent = findViewById(R.id.spParent)
        btnNewParent = findViewById(R.id.btnNewParent)
        txtName = findViewById(R.id.txtName)
        txtNote = findViewById(R.id.txtNote)
        spIcon = findViewById(R.id.spIcon)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        categoryDAO = CategoryDAO(this)

        idType = intent.getIntExtra("idType", CategoryType.ID_CHI)

        parentAdapter = CategoryAdapter(this, parents)
        spParent.adapter = parentAdapter

        logoAdapter = LogoAdapter(this)
        logoAdapter.idType = idType
        spIcon.adapter = logoAdapter

        btnNewParent.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        // Nạp lại mục cha khi thay đổi kiểu thu/chi.
        swType.isChecked = idType == CategoryType.ID_THU
        swType.setOnCheckedChangeListener { _, isChecked ->
            idType = if (isChecked) CategoryType.ID_THU else CategoryType.ID_CHI
            logoAdapter.idType = idType
            logoAdapter.notifyDataSetChanged()
            // Bỏ chọn mục cha khi đổi kiểu thu/chi.
            reloadParents(0)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // Mở màn hình thêm mục cha mới.
            R.id.btnNewParent -> {
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
        val name = txtName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên mục thu/chi", Toast.LENGTH_SHORT).show()
            return
        }

        val category = Category(
            name = name,
            icon = Icons.nameAt(spIcon.selectedItemPosition),
            note = txtNote.text.toString().trim(),
            type = typeOf(idType),
            parent = selectedParent()
        )

        if (categoryDAO.isDuplicateCategory(category)) {
            Toast.makeText(this, "Mục \"" + name + "\" đã có trong cùng mục cha", Toast.LENGTH_SHORT).show()
            return
        }

        if (!categoryDAO.addCategory(category)) {
            Toast.makeText(this, "Thêm mục thu/chi thất bại", Toast.LENGTH_SHORT).show()
            return
        }

        finish()
    }

    /** Dòng đầu của Spinner tương ứng với không có mục cha. */
    private fun selectedParent(): Category? = parents.getOrNull(spParent.selectedItemPosition)

    private fun typeOf(id: Int): CategoryType =
        categoryDAO.getTypes().firstOrNull { it.id == id } ?: CategoryType(id, "", "")

    /** Nạp lại mục cha và khôi phục lựa chọn theo mã. */
    private fun reloadParents(keepId: Int) {
        parents.clear()
        parents.add(null)
        parents.addAll(categoryDAO.getCategories(idType))
        parentAdapter.notifyDataSetChanged()

        val index = parents.indexOfFirst { it != null && it.id == keepId }
        spParent.setSelection(if (index >= 0) index else 0)
    }

    /** Cập nhật các mục cha sau khi thêm mục mới. */
    override fun onResume() {
        super.onResume()
        reloadParents(selectedParent()?.id ?: 0)
    }
}
