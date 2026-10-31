package ptit.cnpm1.tranxuanthanh.quanlynhansu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Activity_detail: xem và sửa 1 nhân sự, Update ghi vào DB rồi quay về danh sách
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MA = "ma";

    private DBHelper db;
    private NhanSuForm form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        db = new DBHelper(this);
        form = new NhanSuForm(this);

        NhanSu ns = db.getByMa(getIntent().getStringExtra(EXTRA_MA));
        if (ns == null) {
            finish();
            return;
        }
        form.fill(ns);
        // Mã là khoá chính nên không cho sửa
        form.edtMa.setEnabled(false);

        Button btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(v -> update());
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private void update() {
        NhanSu ns = form.read();
        if (ns == null) return;

        db.update(ns);
        Toast.makeText(this, getString(R.string.da_sua, ns.getHoTen()), Toast.LENGTH_SHORT).show();
        finish();
    }
}
