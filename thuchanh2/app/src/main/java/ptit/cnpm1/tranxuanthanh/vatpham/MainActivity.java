package ptit.cnpm1.tranxuanthanh.vatpham;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

/** Màn hình 1: chọn danh mục ở Spinner, ListView hiện các vật phẩm của danh mục đó. */
public class MainActivity extends AppCompatActivity {

    private Spinner spnCategory;
    private ListView lvItems;

    private final List<Item> items = new ArrayList<>();
    private final List<String> names = new ArrayList<>();
    private ArrayAdapter<String> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 4 dòng EdgeToEdge + insets là code Android Studio tự sinh khi tạo project.
        EdgeToEdge.enable(this);
        setContentView(R.layout.main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spnCategory = findViewById(R.id.spnCategory);
        lvItems = findViewById(R.id.lvItems);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ItemData.CATEGORIES);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(categoryAdapter);

        itemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        lvItems.setAdapter(itemAdapter);

        // Spinner gọi onItemSelected ngay lần đầu với vị trí 0, nên list "Đồ dùng" tự hiện
        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadItems(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lvItems.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_ITEM, items.get(position));
            startActivity(intent);
        });
    }

    /** Nạp lại ListView theo danh mục vừa chọn. */
    private void loadItems(int category) {
        items.clear();
        items.addAll(ItemData.getItems(category));
        names.clear();
        for (Item item : items) {
            names.add(item.getName());
        }
        itemAdapter.notifyDataSetChanged();
    }
}
