package ptit.cnpm1.tranxuanthanh.bai06

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast

/**
 * Màn thêm mục thu/chi (slide 8 và 13). Mục cha chọn theo cây mục đã có; để
 * trống mục cha nghĩa là thêm một mục cha ngang hàng các mục cha đã có.
 */
class AddCategoryAct : Activity(), View.OnClickListener {

    private lateinit var swType: Switch
    private lateinit var spParent: Spinner
    private lateinit var btnNewParent: Button
    private lateinit var txtName: EditText
    private lateinit var txtNote: EditText
    private lateinit var spIcon: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    /** Kiểu của mục sắp thêm, nhận từ màn giao dịch gọi tới. */
    private var idType = CategoryType.ID_CHI

    /** Các mục có thể làm cha; phần tử đầu là null, tức dòng "--- trống ---". */
    private val parents = ArrayList<Category?>()
    private lateinit var parentAdapter: CategoryAdapter
    private lateinit var logoAdapter: LogoAdapter

    private lateinit var dao: WalletDAO

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

        dao = WalletDAO(this)

        idType = intent.getIntExtra("idType", CategoryType.ID_CHI)

        parentAdapter = CategoryAdapter(this, parents)
        spParent.adapter = parentAdapter

        logoAdapter = LogoAdapter(this)
        logoAdapter.idType = idType
        spIcon.adapter = logoAdapter

        btnNewParent.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        // Gạt nút on/off thì cây mục cha đổi theo kiểu thu/chi mới
        swType.isChecked = idType == CategoryType.ID_THU
        swType.setOnCheckedChangeListener { _, isChecked ->
            idType = if (isChecked) CategoryType.ID_THU else CategoryType.ID_CHI
            logoAdapter.idType = idType
            logoAdapter.notifyDataSetChanged()
            reloadParents()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // Dấu + cạnh mục cha: thêm tiếp một mục nữa để làm cha
            R.id.btnNewParent -> {
                val add = Intent(this, AddCategoryAct::class.java).apply {
                    putExtra("idType", idType)
                }
                startActivity(add)
            }
            R.id.btnSave -> save()
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

        val category = Category(
            name = name,
            icon = Icons.nameAt(spIcon.selectedItemPosition),
            note = txtNote.text.toString().trim(),
            type = typeOf(idType),
            parent = selectedParent()
        )

        if (!dao.addCategory(category)) {
            Toast.makeText(this, "Thêm mục thu/chi thất bại", Toast.LENGTH_SHORT).show()
            return
        }

        finish()
    }

    /** Dòng đầu Spinner là null ("--- trống ---"), tức là không có cha. */
    private fun selectedParent(): Category? = parents.getOrNull(spParent.selectedItemPosition)

    private fun typeOf(id: Int): CategoryType =
        dao.getTypes().firstOrNull { it.id == id } ?: CategoryType(id, "", "")

    private fun reloadParents() {
        parents.clear()
        parents.add(null)
        parents.addAll(dao.getCategories(idType))
        parentAdapter.notifyDataSetChanged()
    }

    /** Quay lại từ màn thêm mục cha thì cây mục có thể đã khác. */
    override fun onResume() {
        super.onResume()
        reloadParents()
    }
}
