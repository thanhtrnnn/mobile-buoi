package ptit.cnpm1.tranxuanthanh.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// Activity_welcome: nhận MSSV, họ tên từ Intent và hiển thị cùng ảnh cá nhân
public class WelcomeActivity extends AppCompatActivity {

    public static final String EXTRA_MSSV = "mssv";
    public static final String EXTRA_HO_TEN = "ho_ten";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Intent intent = getIntent();
        String mssv = intent.getStringExtra(EXTRA_MSSV);
        String hoTen = intent.getStringExtra(EXTRA_HO_TEN);

        TextView tvHello = findViewById(R.id.tvHello);
        TextView tvHoTen = findViewById(R.id.tvHoTen);
        tvHello.setText(getString(R.string.hello, mssv));
        tvHoTen.setText(hoTen);
    }
}
