package ptit.cnpm1.tranxuanthanh.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Activity_main: màn hình Đăng nhập
public class MainActivity extends AppCompatActivity {

    // Tài khoản đúng để đăng nhập
    private static final String USERNAME = "B23DCAT280";
    private static final String PASSWORD = "12345678";
    private static final String HO_TEN = "Trần Xuân Thành";

    private EditText edtUsername, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString();

        if (username.equals(USERNAME) && password.equals(PASSWORD)) {
            // Đúng: gửi MSSV và họ tên qua Intent sang Activity_welcome
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            intent.putExtra(WelcomeActivity.EXTRA_MSSV, username);
            intent.putExtra(WelcomeActivity.EXTRA_HO_TEN, HO_TEN);
            startActivity(intent);
        } else {
            // Sai: báo lỗi và ở lại màn hình Đăng nhập
            Toast.makeText(MainActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
}
