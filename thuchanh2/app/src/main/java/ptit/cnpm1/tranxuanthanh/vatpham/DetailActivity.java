package ptit.cnpm1.tranxuanthanh.vatpham;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Màn hình 2: hiện ảnh, tên và mô tả của vật phẩm được chọn ở màn hình 1. */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM = "item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imgItem = findViewById(R.id.imgItem);
        TextView lblName = findViewById(R.id.lblName);
        TextView lblDescription = findViewById(R.id.lblDescription);

        Item item = IntentCompat.getSerializableExtra(getIntent(), EXTRA_ITEM, Item.class);
        if (item == null) {
            finish();
            return;
        }
        imgItem.setImageResource(item.getImageRes());
        lblName.setText(item.getName());
        lblDescription.setText(item.getDescription());
    }
}
