package ptit.cnpm1.tranxuanthanh.thuchanh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnCau1;
    private Button btnCau2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 4 dòng EdgeToEdge + insets là code Android Studio tự sinh khi tạo project.
        // Nó tìm view có id "main", nên layout nào dùng code này cũng phải giữ id đó.
        EdgeToEdge.enable(this);
        setContentView(R.layout.main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCau1 = findViewById(R.id.btnCau1);
        btnCau2 = findViewById(R.id.btnCau2);

        btnCau1.setOnClickListener(this);
        btnCau2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCau1) {
            startActivity(new Intent(this, Cau1Act.class));
        } else if (v.getId() == R.id.btnCau2) {
            startActivity(new Intent(this, Cau2Act.class));
        }
    }
}
