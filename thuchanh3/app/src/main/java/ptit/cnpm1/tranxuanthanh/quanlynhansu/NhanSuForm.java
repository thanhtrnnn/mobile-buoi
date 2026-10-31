package ptit.cnpm1.tranxuanthanh.quanlynhansu;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

// Các ô nhập của form_nhansu.xml, dùng chung cho Activity_add và Activity_detail:
// điền dữ liệu có sẵn, xoá trắng, chọn ảnh, đọc và kiểm tra dữ liệu nhập
public class NhanSuForm {

    // Ảnh lưu vào DB được thu nhỏ để cạnh dài nhất không quá 256px
    private static final int ANH_MAX = 256;

    private final ComponentActivity activity;
    final EditText edtMa;
    private final EditText edtHoTen, edtNgaySinh, edtQueQuan, edtLuong;
    private final RadioGroup rgGioiTinh;
    private final RadioButton rbNam, rbNu;
    private final Spinner spTrinhDo, spPhongBan;
    private final ImageView imgAnh;
    private String ngaySinh; // yyyy-MM-dd, null nếu chưa chọn
    private byte[] anh;

    // Phải tạo trong onCreate vì registerForActivityResult chỉ gọi được trước khi activity chạy
    public NhanSuForm(ComponentActivity activity) {
        this.activity = activity;
        edtMa = activity.findViewById(R.id.edtMa);
        edtHoTen = activity.findViewById(R.id.edtHoTen);
        edtNgaySinh = activity.findViewById(R.id.edtNgaySinh);
        edtQueQuan = activity.findViewById(R.id.edtQueQuan);
        edtLuong = activity.findViewById(R.id.edtLuong);
        rgGioiTinh = activity.findViewById(R.id.rgGioiTinh);
        rbNam = activity.findViewById(R.id.rbNam);
        rbNu = activity.findViewById(R.id.rbNu);
        spTrinhDo = activity.findViewById(R.id.spTrinhDo);
        spPhongBan = activity.findViewById(R.id.spPhongBan);
        imgAnh = activity.findViewById(R.id.imgAnh);
        spTrinhDo.setAdapter(spinnerAdapter(R.array.trinh_do));
        spPhongBan.setAdapter(spinnerAdapter(R.array.phong_ban));

        // Ô ngày sinh không gõ được, bấm vào thì mở lịch chọn ngày
        edtNgaySinh.setOnClickListener(v -> chonNgaySinh());

        // Nút Chọn ảnh mở trình chọn ảnh của máy, chọn xong thì thu nhỏ và hiện lên ImageView
        ActivityResultLauncher<String> chonAnh = activity.registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) setAnh(uri);
                });
        Button btnChonAnh = activity.findViewById(R.id.btnChonAnh);
        btnChonAnh.setOnClickListener(v -> chonAnh.launch("image/*"));
    }

    public void fill(NhanSu ns) {
        edtMa.setText(ns.getMa());
        edtHoTen.setText(ns.getHoTen());
        setNgaySinh(ns.getNgaySinh());
        rgGioiTinh.check(rbNu.getText().toString().equals(ns.getGioiTinh()) ? R.id.rbNu : R.id.rbNam);
        edtQueQuan.setText(ns.getQueQuan());
        selectSpinner(spTrinhDo, R.array.trinh_do, ns.getTrinhDo());
        edtLuong.setText(String.format(Locale.US, "%.0f", ns.getLuong()));
        selectSpinner(spPhongBan, R.array.phong_ban, ns.getPhongBan());
        anh = ns.getAnh();
        showAnh();
    }

    // Xoá trắng mọi ô, kể cả thông báo lỗi còn treo từ lần Submit trước
    public void reset() {
        for (EditText edt : new EditText[]{edtMa, edtHoTen, edtNgaySinh, edtQueQuan, edtLuong}) {
            edt.setText("");
            edt.setError(null);
        }
        ngaySinh = null;
        rgGioiTinh.check(R.id.rbNam);
        spTrinhDo.setSelection(0);
        spPhongBan.setSelection(0);
        anh = null;
        showAnh();
        edtMa.requestFocus();
    }

    // Đọc form; ô nào sai thì báo lỗi ngay tại ô đó và trả về null
    public NhanSu read() {
        String ma = edtMa.getText().toString().trim();
        String hoTen = edtHoTen.getText().toString().trim();
        String queQuan = edtQueQuan.getText().toString().trim();
        String luongText = edtLuong.getText().toString().trim();

        if (ma.isEmpty()) return loi(edtMa, activity.getString(R.string.err_trong, activity.getString(R.string.lbl_ma)));
        if (hoTen.isEmpty()) return loi(edtHoTen, activity.getString(R.string.err_trong, activity.getString(R.string.lbl_hoten)));

        if (ngaySinh == null) return loi(edtNgaySinh, activity.getString(R.string.err_trong, activity.getString(R.string.lbl_ngaysinh)));

        if (queQuan.isEmpty()) return loi(edtQueQuan, activity.getString(R.string.err_trong, activity.getString(R.string.lbl_quequan)));

        double luong;
        try {
            luong = Double.parseDouble(luongText);
        } catch (NumberFormatException e) {
            luong = 0;
        }
        if (luong <= 0) return loi(edtLuong, activity.getString(R.string.err_luong));

        String gioiTinh = rgGioiTinh.getCheckedRadioButtonId() == R.id.rbNu
                ? rbNu.getText().toString() : rbNam.getText().toString();
        return new NhanSu(ma.toUpperCase(), hoTen, ngaySinh, gioiTinh, queQuan,
                spTrinhDo.getSelectedItem().toString(), anh, luong,
                spPhongBan.getSelectedItem().toString());
    }

    private NhanSu loi(EditText edt, String message) {
        edt.setError(message);
        edt.requestFocus();
        // Ô ngày sinh không nhận focus nên không hiện bóng lỗi, báo thêm bằng Toast
        if (!edt.isFocusable()) Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        return null;
    }

    // Lịch mở sẵn ở ngày đang chọn, chưa chọn thì ở 01/01 của năm cách đây 25 năm; không cho chọn ngày tương lai
    private void chonNgaySinh() {
        int nam, thang, ngay;
        if (ngaySinh != null) {
            String[] p = ngaySinh.split("-");
            nam = Integer.parseInt(p[0]);
            thang = Integer.parseInt(p[1]) - 1;
            ngay = Integer.parseInt(p[2]);
        } else {
            nam = Calendar.getInstance().get(Calendar.YEAR) - 25;
            thang = Calendar.JANUARY;
            ngay = 1;
        }
        DatePickerDialog dialog = new DatePickerDialog(activity,
                (view, y, m, d) -> setNgaySinh(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                nam, thang, ngay);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void setNgaySinh(String ngay) {
        ngaySinh = ngay;
        edtNgaySinh.setText(NhanSu.hienThiNgay(ngay));
        edtNgaySinh.setError(null);
    }

    // Spinner dùng spinner_item.xml để cỡ chữ theo dimens (tablet to hơn)
    private ArrayAdapter<String> spinnerAdapter(int arrayId) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.spinner_item,
                activity.getResources().getStringArray(arrayId));
        adapter.setDropDownViewResource(R.layout.spinner_item);
        return adapter;
    }

    private void selectSpinner(Spinner spinner, int arrayId, String value) {
        int index = Arrays.asList(activity.getResources().getStringArray(arrayId)).indexOf(value);
        spinner.setSelection(Math.max(index, 0));
    }

    private void showAnh() {
        if (anh != null) {
            imgAnh.setImageBitmap(BitmapFactory.decodeByteArray(anh, 0, anh.length));
        } else {
            imgAnh.setImageResource(R.drawable.ic_person);
        }
    }

    // Đọc ảnh vừa chọn, thu nhỏ và nén JPEG để lưu vào cột BLOB
    private void setAnh(Uri uri) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try (InputStream in = activity.getContentResolver().openInputStream(uri)) {
                BitmapFactory.decodeStream(in, null, options);
            }
            // Giảm độ phân giải ngay khi đọc để ảnh chụp lớn không làm tràn bộ nhớ
            int sample = 1;
            while (Math.max(options.outWidth, options.outHeight) / (sample * 2) >= ANH_MAX) {
                sample *= 2;
            }
            options = new BitmapFactory.Options();
            options.inSampleSize = sample;
            Bitmap bitmap;
            try (InputStream in = activity.getContentResolver().openInputStream(uri)) {
                bitmap = BitmapFactory.decodeStream(in, null, options);
            }
            if (bitmap == null) throw new IOException("decode failed");

            float scale = Math.min(1f, (float) ANH_MAX / Math.max(bitmap.getWidth(), bitmap.getHeight()));
            Bitmap small = Bitmap.createScaledBitmap(bitmap,
                    Math.round(bitmap.getWidth() * scale), Math.round(bitmap.getHeight() * scale), true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            small.compress(Bitmap.CompressFormat.JPEG, 85, out);
            anh = out.toByteArray();
            showAnh();
        } catch (IOException | SecurityException e) {
            Toast.makeText(activity, R.string.err_anh, Toast.LENGTH_SHORT).show();
        }
    }
}
