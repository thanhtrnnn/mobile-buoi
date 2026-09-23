package ptit.cnpm1.tranxuanthanh.bai06

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

/**
 * Màn thêm mục thu/chi (slide 8 và 13). Mục cha chọn theo cây mục đã có; để
 * trống mục cha nghĩa là thêm một mục cha ngang hàng các mục cha đã có.
 */
class AddCategoryAct : Activity(), View.OnClickListener {

    private lateinit var btnThu: Button
    private lateinit var btnChi: Button
    private lateinit var spParent: Spinner
    private lateinit var btnNewParent: Button
    private lateinit var txtName: EditText
    private lateinit var txtNote: EditText
    private lateinit var spIcon: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    /** Kiểu của mục sắp thêm, nhận từ màn giao dịch gọi tới. */
    private var idType = CategoryType.ID_CHI

    /** Các mục có thể làm cha, cùng thứ tự với Spinner trừ dòng "--- trống ---". */
    private val parents = ArrayList<Category>()
    private val parentLabels = ArrayList<String>()
    private lateinit var parentAdapter: ArrayAdapter<String>

    private lateinit var dao: WalletDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addcategory)

        btnThu = findViewById(R.id.btnThu)
        btnChi = findViewById(R.id.btnChi)
        spParent = findViewById(R.id.spParent)
        btnNewParent = findViewById(R.id.btnNewParent)
        txtName = findViewById(R.id.txtName)
        txtNote = findViewById(R.id.txtNote)
        spIcon = findViewById(R.id.spIcon)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        dao = WalletDAO(this)

        idType = intent.getIntExtra("idType", CategoryType.ID_CHI)

        parentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, parentLabels)
        parentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spParent.adapter = parentAdapter

        val iconAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, Icons.ALL.values.toList()
        )
        iconAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spIcon.adapter = iconAdapter

        btnThu.setOnClickListener(this)
        btnChi.setOnClickListener(this)
        btnNewParent.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        showType()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // Đổi kiểu thì cây mục cha cũng đổi theo
            R.id.btnThu -> {
                idType = CategoryType.ID_THU
                showType()
                reloadParents()
            }
            R.id.btnChi -> {
                idType = CategoryType.ID_CHI
                showType()
                reloadParents()
            }
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

    private fun reloadParents() {
        parents.clear()
        parents.addAll(dao.getCategories(idType))

        parentLabels.clear()
        parentLabels.add("--- trống ---")
        parents.forEach { parentLabels.add(it.toString()) }
        parentAdapter.notifyDataSetChanged()
    }

    /** Quay lại từ màn thêm mục cha thì cây mục có thể đã khác. */
    override fun onResume() {
        super.onResume()
        reloadParents()
    }
}
