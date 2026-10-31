package ptit.cnpm1.tranxuanthanh.quanlynhansu;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

// Activity_main: danh sách nhân sự, tìm kiếm, sắp xếp theo lương, xoá (nhấn giữ), mở màn thêm / sửa
public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private final List<NhanSu> list = new ArrayList<>();
    private NhanSuAdapter adapter;

    private EditText edtSearch;
    private Button btnSort;
    private TextView tvCount;

    private String keyword = "";
    private int sort = DBHelper.SORT_NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper(this);

        edtSearch = findViewById(R.id.edtSearch);
        btnSort = findViewById(R.id.btnSort);
        tvCount = findViewById(R.id.tvCount);
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnAdd = findViewById(R.id.btnAdd);
        ListView lvNhanSu = findViewById(R.id.lvNhanSu);

        adapter = new NhanSuAdapter(this, list);
        lvNhanSu.setAdapter(adapter);
        lvNhanSu.setEmptyView(findViewById(R.id.tvEmpty));

        // (V) Tìm kiếm theo mã hoặc họ tên
        btnSearch.setOnClickListener(v -> search());
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search();
                return true;
            }
            return false;
        });

        // Sắp xếp theo lương: bấm lần lượt đổi tăng dần <-> giảm dần
        btnSort.setOnClickListener(v -> {
            sort = sort == DBHelper.SORT_LUONG_TANG ? DBHelper.SORT_LUONG_GIAM : DBHelper.SORT_LUONG_TANG;
            btnSort.setText(sort == DBHelper.SORT_LUONG_TANG ? R.string.btn_sort_asc : R.string.btn_sort_desc);
            loadData();
        });

        // (II) ADD mở Activity_add
        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddActivity.class)));

        // (III) Bấm 1 dòng mở Activity_detail để sửa
        lvNhanSu.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_MA, list.get(position).getMa());
            startActivity(intent);
        });

        // (IV) Nhấn giữ 1 dòng để xoá
        lvNhanSu.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmDelete(list.get(position));
            return true;
        });
    }

    // Quay về từ màn thêm / sửa thì đọc lại DB để danh sách luôn mới (refresh)
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private void search() {
        keyword = edtSearch.getText().toString().trim();
        InputMethodManager imm = getSystemService(InputMethodManager.class);
        imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
        loadData();
    }

    private void confirmDelete(NhanSu ns) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_delete)
                .setMessage(getString(R.string.msg_delete, ns.getMa(), ns.getHoTen()))
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    db.delete(ns.getMa());
                    Toast.makeText(this, getString(R.string.da_xoa, ns.getHoTen()), Toast.LENGTH_SHORT).show();
                    loadData();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void loadData() {
        list.clear();
        list.addAll(db.getAll(keyword, sort));
        adapter.notifyDataSetChanged();
        tvCount.setText(getString(R.string.so_nhan_su, list.size()));
    }
}
