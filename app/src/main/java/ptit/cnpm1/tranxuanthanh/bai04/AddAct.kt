package ptit.cnpm1.tranxuanthanh.bai04

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddAct : Activity(), View.OnClickListener {

    private lateinit var txtUN: EditText
    private lateinit var txtPW: EditText
    private lateinit var txtName: EditText
    private lateinit var txtDob: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add)

        txtUN = findViewById(R.id.txtUN)
        txtPW = findViewById(R.id.txtPW)
        txtName = findViewById(R.id.txtName)
        txtDob = findViewById(R.id.txtDob)
        btnAdd = findViewById(R.id.btnAdd)
        btnCancel = findViewById(R.id.btnCancel)

        // Ô ngày sinh không gõ tay được, bấm vào thì mở lịch chọn
        txtDob.setOnClickListener {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val calendar = Calendar.getInstance()
            val typed = txtDob.text.toString().trim()
            if (typed.isNotEmpty()) format.parse(typed)?.let { calendar.time = it }

            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val picked = Calendar.getInstance()
                    picked.set(year, month, dayOfMonth, 0, 0, 0)
                    picked.set(Calendar.MILLISECOND, 0)
                    txtDob.setText(format.format(picked.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnAdd.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAdd -> {
                val username = txtUN.text.toString().trim()
                val password = txtPW.text.toString().trim()
                val fullname = txtName.text.toString().trim()
                val dobText = txtDob.text.toString().trim()

                if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || dobText.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return
                }

                val dob = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dobText) ?: return
                val newUser = User(username, password, fullname, dob)

                val result = Intent().putExtra("user", newUser)
                setResult(RESULT_OK, result)
                finish()
            }
            R.id.btnCancel -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }
}
