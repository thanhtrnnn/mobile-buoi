package ptit.cnpm1.tranxuanthanh.bai06

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

/**
 * Màn sửa mục thu/chi (slide 8 và 13): giống màn thêm nhưng điền sẵn mục nhận
 * được và có thêm nút Xóa.
 */
class EditCategoryAct : Activity(), View.OnClickListener {

    private lateinit var btnThu: Button
    private lateinit var btnChi: Button
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

    private val parents = ArrayList<Category>()
    private val parentLabels = ArrayList<String>()
    private lateinit var parentAdapter: ArrayAdapter<String>

    private lateinit var dao: WalletDAO

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editcategory)

        btnThu = findViewById(R.id.btnThu)
        btnChi = findViewById(R.id.btnChi)
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

        parentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, parentLabels)
        parentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spParent.adapter = parentAdapter

        val iconAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, Icons.ALL.values.toList()
        )
        iconAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spIcon.adapter = iconAdapter

        // Điền sẵn thông tin cũ
        txtName.setText(category.name)
        txtNote.setText(category.note)
        spIcon.setSelection(Icons.positionOf(category.icon))

        btnThu.setOnClickListener(this)
        btnChi.setOnClickListener(this)
        btnNewParent.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        showType()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // Đổi kiểu thì cây mục cha cũng đổi theo, mục cha cũ không còn hợp
            R.id.btnThu -> {
                idType = CategoryType.ID_THU
                showType()
                reloadParents(0)
            }
            R.id.btnChi -> {
                idType = CategoryType.ID_CHI
                showType()
                reloadParents(0)
            }
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

    /** Dòng đầu Spinner là "--- trống ---", nên vị trí 0 nghĩa là không có cha. */
    private fun selectedParent(): Category? {
        val position = spParent.selectedItemPosition
        return if (position <= 0) null else parents.getOrNull(position - 1)
    }

    private fun typeOf(id: Int): CategoryType =
        dao.getTypes().firstOrNull { it.id == id } ?: CategoryType(id, "", "")

    /** Nút của kiểu đang chọn thì bật (nền xanh), nút kia tắt (nền trắng). */
    private fun showType() {
        val thuOn = idType == CategoryType.ID_THU
        btnThu.setBackgroundResource(if (thuOn) R.drawable.bg_menu else R.drawable.bg_input)
        btnChi.setBackgroundResource(if (thuOn) R.drawable.bg_input else R.drawable.bg_menu)
    }

    /**
     * Đọc lại danh sách mục có thể làm cha. Bỏ chính mục đang sửa và con cháu
     * của nó ra, vì nhận một trong số đó làm cha thì cây sẽ thành vòng tròn.
     * [keepId] là mục cha muốn giữ nguyên lựa chọn, 0 nghĩa là để trống.
     */
    private fun reloadParents(keepId: Int) {
        parents.clear()
        parents.addAll(dao.getCategories(idType).filter { !isSelfOrDescendant(it) })

        parentLabels.clear()
        parentLabels.add("--- trống ---")
        parents.forEach { parentLabels.add(it.toString()) }
        parentAdapter.notifyDataSetChanged()

        val index = parents.indexOfFirst { it.id == keepId }
        spParent.setSelection(if (index >= 0) index + 1 else 0)
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
