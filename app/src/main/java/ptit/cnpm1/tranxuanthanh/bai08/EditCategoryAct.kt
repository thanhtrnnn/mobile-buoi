package ptit.cnpm1.tranxuanthanh.bai08

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast

/** Sửa hoặc xóa mục thu/chi. */
class EditCategoryAct : Activity(), View.OnClickListener {

    private lateinit var swType: Switch
    private lateinit var spParent: Spinner
    private lateinit var btnNewParent: Button
    private lateinit var txtName: EditText
    private lateinit var txtNote: EditText
    private lateinit var spIcon: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button
    private lateinit var btnCancel: Button

    /** Mục nhận từ màn hình giao dịch. */
    private lateinit var category: Category

    private var idType = CategoryType.ID_CHI

    /** Giữ true cho đến khi mục cha ban đầu được chọn. */
    private var firstLoad = true

    /** Các mục cha có thể chọn; null nghĩa là không có cha. */
    private val parents = ArrayList<Category?>()
    private lateinit var parentAdapter: CategoryAdapter
    private lateinit var logoAdapter: LogoAdapter

    private lateinit var categoryDAO: CategoryDAO

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editcategory)

        swType = findViewById(R.id.swType)
        spParent = findViewById(R.id.spParent)
        btnNewParent = findViewById(R.id.btnNewParent)
        txtName = findViewById(R.id.txtName)
        txtNote = findViewById(R.id.txtNote)
        spIcon = findViewById(R.id.spIcon)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)
        btnCancel = findViewById(R.id.btnCancel)

        categoryDAO = CategoryDAO(this)

        val received = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("category", Category::class.java)
        } else {
            intent.getSerializableExtra("category") as? Category
        }
        category = received ?: Category()
        idType = category.type.id

        parentAdapter = CategoryAdapter(this, parents)
        spParent.adapter = parentAdapter

        logoAdapter = LogoAdapter(this)
        logoAdapter.idType = idType
        spIcon.adapter = logoAdapter

        // Điền thông tin hiện có vào biểu mẫu.
        txtName.setText(category.name)
        txtNote.setText(category.note)
        spIcon.setSelection(Icons.positionOf(category.icon))

        btnNewParent.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        // Đặt kiểu ban đầu trước khi nghe sự kiện để giữ mục cha.
        swType.isChecked = idType == CategoryType.ID_THU
        swType.setOnCheckedChangeListener { _, isChecked ->
            idType = if (isChecked) CategoryType.ID_THU else CategoryType.ID_CHI
            logoAdapter.idType = idType
            logoAdapter.notifyDataSetChanged()
            reloadParents(0)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnNewParent -> {
                val add = Intent(this, AddCategoryAct::class.java).apply {
                    putExtra("idType", idType)
                }
                startActivity(add)
            }
            R.id.btnSave -> save()
            R.id.btnDelete -> confirmDelete()
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

        // Giữ nguyên mã để DAO cập nhật đúng bản ghi.
        val updated = Category(
            id = category.id,
            name = name,
            icon = Icons.nameAt(spIcon.selectedItemPosition),
            note = txtNote.text.toString().trim(),
            type = typeOf(idType),
            parent = selectedParent()
        )

        // Kiểm tra trùng tên trước khi cập nhật mục.
        if (categoryDAO.isDuplicateCategory(updated)) {
            Toast.makeText(this, "Mục \"" + name + "\" đã có trong cùng mục cha", Toast.LENGTH_SHORT).show()
            return
        }
        category = updated

        if (!categoryDAO.editCategory(category)) {
            Toast.makeText(this, "Sửa mục thu/chi thất bại", Toast.LENGTH_SHORT).show()
            return
        }

        finish()
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage(
                "Xóa mục \"" + category.name + "\" sẽ xóa luôn các mục con cháu " +
                    "và mọi giao dịch đã dùng những mục đó. Bạn có chắc không?"
            )
            .setPositiveButton("Có") { _, _ ->
                if (categoryDAO.deleteCategory(category.id)) {
                    finish()
                } else {
                    Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                }
            }
            // Tiếp tục sửa nếu hủy thao tác xóa.
            .setNegativeButton("Không", null)
            .show()
    }

    /** Dòng đầu của Spinner tương ứng với không có mục cha. */
    private fun selectedParent(): Category? = parents.getOrNull(spParent.selectedItemPosition)

    private fun typeOf(id: Int): CategoryType =
        categoryDAO.getTypes().firstOrNull { it.id == id } ?: CategoryType(id, "", "")

    /** Nạp mục cha hợp lệ, loại chính mục này và toàn bộ con cháu. */
    private fun reloadParents(keepId: Int) {
        parents.clear()
        parents.add(null)
        parents.addAll(categoryDAO.getCategories(idType).filter { !isSelfOrDescendant(it) })
        parentAdapter.notifyDataSetChanged()

        val index = parents.indexOfFirst { it != null && it.id == keepId }
        spParent.setSelection(if (index >= 0) index else 0)
    }

    private fun isSelfOrDescendant(other: Category): Boolean {
        var node: Category? = other
        while (node != null) {
            if (node.id == category.id) return true
            node = node.parent
        }
        return false
    }

    /** Chọn cha ban đầu một lần, sau đó giữ lựa chọn của người dùng. */
    override fun onResume() {
        super.onResume()
        // Giữ lựa chọn không có mục cha của người dùng.
        val keep = if (firstLoad) category.parent?.id ?: 0 else selectedParent()?.id ?: 0
        firstLoad = false
        reloadParents(keep)
    }
}
