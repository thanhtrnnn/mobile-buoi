package ptit.cnpm1.tranxuanthanh.bai06

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

/**
 * Màn sửa mục thu/chi (slide 8 và 13): giống màn thêm nhưng điền sẵn mục nhận
 * được và có thêm nút Xóa.
 */
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

    /** Mục đang được sửa, nhận từ màn giao dịch. */
    private lateinit var category: Category

    private var idType = CategoryType.ID_CHI

    /** Các mục có thể làm cha; phần tử đầu là null, tức dòng "--- trống ---". */
    private val parents = ArrayList<Category?>()
    private lateinit var parentAdapter: CategoryAdapter
    private lateinit var logoAdapter: LogoAdapter

    private lateinit var dao: WalletDAO

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

        dao = WalletDAO(this)

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

        // Điền sẵn thông tin cũ
        txtName.setText(category.name)
        txtNote.setText(category.note)
        spIcon.setSelection(Icons.positionOf(category.icon))

        btnNewParent.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        // Gạt nút on/off thì cây mục cha đổi theo, mục cha cũ không còn hợp.
        // Gán trạng thái đầu trước khi gắn listener, để lần gán đó không
        // làm mất mục cha đang chọn sẵn.
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
            // Hủy: quay lại màn trước mà không ghi gì vào CSDL
            R.id.btnCancel -> finish()
        }
    }

    private fun save() {
        val name = txtName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên mục thu/chi", Toast.LENGTH_SHORT).show()
            return
        }

        // Giữ nguyên id để WalletDAO biết sửa đúng dòng nào trong CSDL
        category = Category(
            id = category.id,
            name = name,
            icon = Icons.nameAt(spIcon.selectedItemPosition),
            note = txtNote.text.toString().trim(),
            type = typeOf(idType),
            parent = selectedParent()
        )

        if (!dao.editCategory(category)) {
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
                if (dao.deleteCategory(category.id)) {
                    finish()
                } else {
                    Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                }
            }
            // Bấm Không thì không làm gì, ở nguyên màn sửa
            .setNegativeButton("Không", null)
            .show()
    }

    /** Dòng đầu Spinner là null ("--- trống ---"), tức là không có cha. */
    private fun selectedParent(): Category? = parents.getOrNull(spParent.selectedItemPosition)

    private fun typeOf(id: Int): CategoryType =
        dao.getTypes().firstOrNull { it.id == id } ?: CategoryType(id, "", "")

    /**
     * Đọc lại danh sách mục có thể làm cha. Bỏ chính mục đang sửa và con cháu
     * của nó ra, vì nhận một trong số đó làm cha thì cây sẽ thành vòng tròn.
     * [keepId] là mục cha muốn giữ nguyên lựa chọn, 0 nghĩa là để trống.
     */
    private fun reloadParents(keepId: Int) {
        parents.clear()
        parents.add(null)
        parents.addAll(dao.getCategories(idType).filter { !isSelfOrDescendant(it) })
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

    /**
     * Lần đầu vào màn thì chọn sẵn mục cha cũ; các lần sau (quay lại từ màn
     * thêm mục cha) thì giữ nguyên mục cha đang chọn.
     */
    override fun onResume() {
        super.onResume()
        reloadParents(selectedParent()?.id ?: category.parent?.id ?: 0)
    }
}
