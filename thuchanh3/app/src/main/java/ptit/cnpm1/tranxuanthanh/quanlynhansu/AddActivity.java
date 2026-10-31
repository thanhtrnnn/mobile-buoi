package ptit.cnpm1.tranxuanthanh.quanlynhansu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Activity_add: nhập nhân sự mới, Submit ghi vào DB rồi quay về danh sách, Reset xoá trắng form
public class AddActivity extends AppCompatActivity {

    private DBHelper db;
    private NhanSuForm form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        db = new DBHelper(this);
        form = new NhanSuForm(this);

        Button btnSubmit = findViewById(R.id.btnSubmit);
        Button btnReset = findViewById(R.id.btnReset);
        btnSubmit.setOnClickListener(v -> submit());
        btnReset.setOnClickListener(v -> form.reset());
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private void submit() {
        NhanSu ns = form.read();
        if (ns == null) return;

        if (db.getByMa(ns.getMa()) != null || !db.insert(ns)) {
            form.edtMa.setError(getString(R.string.err_trung_ma, ns.getMa()));
            form.edtMa.requestFocus();
            return;
        }
        Toast.makeText(this, getString(R.string.da_them, ns.getHoTen()), Toast.LENGTH_SHORT).show();
        finish();
    }
}
